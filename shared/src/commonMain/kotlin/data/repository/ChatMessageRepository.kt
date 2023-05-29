package data.repository

import analytics.AnalyticsHelper
import analytics.logMessageReceived
import analytics.logMessageSent
import analytics.setUserTotalMessages
import analytics.setUserTotalTokens
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.benasher44.uuid.uuid4
import com.ebfstudio.appgpt.common.ChatMessageEntity
import com.ebfstudio.appgpt.common.ChatMessageEntityQueries
import com.knuddels.jtokkit.api.Encoding
import data.local.PreferenceLocalDataSource
import data.repository.util.suspendRunCatching
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock
import model.ChatMessageStatus
import model.asModel

class ChatMessageRepository(
    private val openAI: OpenAI,
    private val chatMessageQueries: ChatMessageEntityQueries,
    private val preferences: PreferenceLocalDataSource,
    private val coinRepository: CoinRepository,
    private val analyticsHelper: AnalyticsHelper,
    private val encoding: Encoding,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default,
) {

    fun getMessagesStream(chatId: String): Flow<List<ChatMessageEntity>> =
        chatMessageQueries.getChatMessagesWithChatId(chatId)
            .asFlow()
            .mapToList(defaultDispatcher)

    suspend fun sendMessage(
        chatId: String,
        contentMessage: String,
    ): Result<Int> = suspendRunCatching(defaultDispatcher) {
        analyticsHelper.logMessageSent()

        // Save user message
        val userMessage = ChatMessage(role = ChatRole.User, content = contentMessage)
        chatMessageQueries.insertChatMessage(
            id = uuid4().toString(),
            role = userMessage.role,
            content = userMessage.content,
            createdAt = Clock.System.now(),
            chatId = chatId,
            status = ChatMessageStatus.SENT,
        )

        // Send message to OpenAI
        sendReceiveAndSaveAI(chatId = chatId)
    }

    suspend fun retrySendMessage(chatId: String): Result<Int> =
        suspendRunCatching(defaultDispatcher) {
            analyticsHelper.logMessageSent(isRetry = true)

            val failedMessages = chatMessageQueries
                .getChatMessagesWithChatIdAndStatus(chatId, ChatMessageStatus.FAILED)
                .executeAsList()

            failedMessages.forEach { message ->
                chatMessageQueries.deleteChatMessage(message.id)
            }

            // Send message to OpenAI
            sendReceiveAndSaveAI(chatId = chatId)
        }

    suspend fun generateTitleFromChat(
        chatId: String,
    ): Result<String> = suspendRunCatching(defaultDispatcher) {
        val instruction = ChatMessage(
            role = ChatRole.System,
            content = "What would be a short and relevant title for this chat? You must strictly answer with only the title, no other text is allowed.",
        )

        val messages = chatMessageQueries.getChatMessagesWithChatId(chatId)
            .executeAsList()
            .map(ChatMessageEntity::asModel)

        val request = ChatCompletionRequest(
            model = ModelId("gpt-3.5-turbo"),
            messages = messages + instruction,
        )

        val response = openAI.chatCompletion(request)
        response.choices.first().message?.content ?: "?"
    }

    /**
     * Send message to OpenAI and save the response
     * - Save empty assistant message
     * - Send request to OpenAI
     * - Save response
     * - Update assistant message status to sent
     *
     * @param chatId Chat id
     * @return Number of messages sent by the user
     */
    private suspend fun sendReceiveAndSaveAI(chatId: String): Int {
        // Save empty assistant message
        val assistantMessageId = uuid4().toString()
        chatMessageQueries.insertChatMessage(
            id = assistantMessageId,
            role = ChatRole.Assistant,
            content = "",
            createdAt = Clock.System.now(),
            chatId = chatId,
            status = ChatMessageStatus.LOADING,
        )

        val messages = chatMessageQueries.getChatMessagesWithChatId(chatId)
            .executeAsList()
            .map(ChatMessageEntity::asModel)

        // Create request to OpenAI
        val request = ChatCompletionRequest(
            model = ModelId("gpt-3.5-turbo"),
            messages = messages,
        )

        // Get assistant response
        var assistantMessage = ""

        try {
            // Sending request to OpenAI
            openAI.chatCompletions(request).collect { chunk ->
                chunk.choices.first().delta?.content?.let {
                    assistantMessage += it
                    chatMessageQueries.updateChatMessageContent(
                        id = assistantMessageId,
                        content = assistantMessage,
                    )
                }
            }

            // Update assistant message status to sent
            chatMessageQueries.updateChatMessageStatus(
                id = assistantMessageId,
                status = ChatMessageStatus.SENT,
            )

            // Consume one token
            coinRepository.useCoins(remove = 1)

            // Report OpenAI tokens used
            val count = countOpenAITokens(messages, assistantMessage)
            val totalTokens = preferences.addTokens(count?.totalTokens ?: 0)
            val totalMessages = preferences.incrementMessages()
            analyticsHelper.setUserTotalTokens(totalTokens)
            analyticsHelper.setUserTotalMessages(totalMessages)

            // Log message received
            analyticsHelper.logMessageReceived(
                receivedSuccessfully = true,
                promptTokens = count?.promptTokens,
                completionTokens = count?.completionTokens,
                totalTokens = count?.totalTokens,
            )

            // Return number of messages sent by the user
            return chatMessageQueries.countChatMessagesWithChatId(
                chatId = chatId,
                role = ChatRole.User,
                status = ChatMessageStatus.SENT,
            ).executeAsOne().toInt()
        } catch (e: Exception) {
            // Update assistant message status to failed
            chatMessageQueries.updateChatMessageStatus(
                id = assistantMessageId,
                status = ChatMessageStatus.FAILED,
            )

            analyticsHelper.logMessageReceived(receivedSuccessfully = false)
            throw e
        }
    }

    /**
     * Count the number of tokens that will be used by OpenAI
     * See more at https://github.com/openai/openai-cookbook/blob/main/examples/How_to_count_tokens_with_tiktoken.ipynb
     *
     * @param promptMessages List of messages
     * @param response Response of the request
     * @return Number of tokens
     */
    private fun countOpenAITokens(
        promptMessages: List<ChatMessage>,
        response: String,
    ): CountTokens? {
        try {
            // Prompt tokens
            val tokensPerMessage = 4
            var promptTokens = 0
            for (message in promptMessages) {
                promptTokens += tokensPerMessage
                promptTokens += encoding.countTokens(message.role.role)
                promptTokens += encoding.countTokens(message.content)
            }
            promptTokens += 3

            // Completion tokens
            val completionTokens = encoding.countTokens(response)

            val count = CountTokens(
                promptTokens = promptTokens,
                completionTokens = completionTokens,
            )

            Napier.i { "Tokens used: $count" }

            return count
        } catch (e: Exception) {
            // It will not work with Android below API 26
            Napier.i { "Error counting tokens: ${e.message}" }
            return null
        }
    }

    data class CountTokens(
        val promptTokens: Int,
        val completionTokens: Int,
    ) {
        val totalTokens: Int = promptTokens + completionTokens
    }
}
