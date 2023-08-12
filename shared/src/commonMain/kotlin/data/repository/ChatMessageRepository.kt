package data.repository

import analytics.AnalyticsHelper
import analytics.logMessageReceived
import analytics.logMessageSent
import analytics.setUserTotalMessages
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOne
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.benasher44.uuid.uuid4
import com.ebfstudio.appgpt.common.ChatMessageEntity
import com.ebfstudio.appgpt.common.ChatMessageEntityQueries
import data.local.PreferenceLocalDataSource
import data.repository.util.suspendRunCatching
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.datetime.Clock
import model.ChatMessageStatus
import model.asModel

class ChatMessageRepository(
    private val openAI: OpenAI,
    private val chatMessageQueries: ChatMessageEntityQueries,
    private val preferences: PreferenceLocalDataSource,
    private val coinRepository: CoinRepository,
    private val billingRepository: BillingRepository,
    private val analyticsHelper: AnalyticsHelper,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default,
) {
    fun getMessagesStream(chatId: String): Flow<List<ChatMessageEntity>> =
        chatMessageQueries.getChatMessagesWithChatId(chatId)
            .asFlow()
            .mapToList(defaultDispatcher)

    fun getNumberOfMessages(): Flow<Long> =
        chatMessageQueries.countAllChatMessages(
            role = ChatRole.User,
            status = ChatMessageStatus.SENT,
        )
            .asFlow()
            .mapToOne(defaultDispatcher)

    suspend fun sendMessage(
        chatId: String,
        contentMessage: String,
    ): Result<Int> = suspendRunCatching(defaultDispatcher) {
        analyticsHelper.logMessageSent()

        if (coinRepository.coins().first() <= 0) {
            throw NoCoinsException()
        }

        // Save user message
        val userMessage = ChatMessage(role = ChatRole.User, content = contentMessage)
        chatMessageQueries.insertChatMessage(
            id = uuid4().toString(),
            role = userMessage.role,
            content = userMessage.content ?: "?",
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
     * @return Number of messages sent by the user in this chat
     */
    private suspend fun sendReceiveAndSaveAI(chatId: String): Int {
        val messagesToSend = selectMessagesToSend(chatId)

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

        // Create request to OpenAI
        val request = ChatCompletionRequest(
            model = ModelId("gpt-3.5-turbo"),
            messages = messagesToSend,
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
            if (billingRepository.isSubToUnlimited.value.not()) {
                coinRepository.useCoins(remove = 1)
            }

            // Report OpenAI tokens used
            val totalMessages = preferences.incrementMessages()
            analyticsHelper.setUserTotalMessages(totalMessages)

            // Log message received
            analyticsHelper.logMessageReceived(receivedSuccessfully = true)

            // Return number of messages sent by the user in this chat
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
     * Get the selection of messages to send to OpenAI
     *
     * The OpenAI API has a limit of 4096 tokens per request.
     * We need to select the last messages that will not exceed this limit.
     * Also, we fix a limit of 1000 characters to avoid too long requests.
     *
     * @param chatId Chat id
     * @return List of messages to send to OpenAI
     */
    private fun selectMessagesToSend(chatId: String): List<ChatMessage> {
        // Get chat messages
        val localMessages = chatMessageQueries.getChatMessagesWithChatId(chatId)
            .executeAsList()
            .map(ChatMessageEntity::asModel)

        // Here are the message we will send to OpenAI
        val messagesToSend = mutableListOf<ChatMessage>()

        // Select last messages until 1000 characters
        localMessages.reversed().takeWhile { message ->
            messagesToSend.add(message)
            messagesToSend.sumOf { it.content?.length ?: 0 } < 1000
        }

        // Put the list back in the right order
        return messagesToSend.reversed()
    }
}

class NoCoinsException : Exception("No coins")
