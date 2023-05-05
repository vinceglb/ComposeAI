package data.repository

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
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import model.asModel

class ChatMessageRepository(
    private val openAI: OpenAI,
    private val chatMessageQueries: ChatMessageEntityQueries,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default,
) {

    fun getMessagesStream(chatId: String): Flow<List<ChatMessageEntity>> =
        chatMessageQueries.getChatMessageByChatId(chatId)
            .asFlow()
            .mapToList(defaultDispatcher)

    suspend fun sendMessage(
        chatId: String,
        contentMessage: String,
    ): Unit = withContext(defaultDispatcher) {
        // Save user message
        val userMessage = ChatMessage(role = ChatRole.User, content = contentMessage)
        chatMessageQueries.insertChatMessage(
            id = uuid4().toString(),
            role = userMessage.role,
            content = userMessage.content,
            createdAt = Clock.System.now(),
            chatId = chatId,
        )

        // Save empty assistant message
        val assistantMessageId = uuid4().toString()
        chatMessageQueries.insertChatMessage(
            id = assistantMessageId,
            role = ChatRole.Assistant,
            content = "",
            createdAt = Clock.System.now(),
            chatId = chatId,
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

        openAI.chatCompletions(request).collect { chunk ->
            chunk.choices.first().delta?.content?.let {
                assistantMessage += it
                chatMessageQueries.updateChatMessageContent(
                    id = assistantMessageId,
                    content = assistantMessage,
                )
            }
        }
    }

    suspend fun generateTitleFromChat(
        chatId: String,
    ): String = withContext(defaultDispatcher) {
        val instruction = ChatMessage(
            role = ChatRole.System,
            content = "Generate a very short title for this chat. Use the language used in the chat. Do not add any punctuation.",
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

}
