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
import com.ebfstudio.appgpt.common.ChatMessageQueries
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import model.asModel

class ChatMessageRepository(
    private val openAI: OpenAI,
    private val chatMessageQueries: ChatMessageQueries,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default,
) {

    fun getMessages(): Flow<List<ChatMessageEntity>> = chatMessageQueries.getAllChatMessages()
            .asFlow()
            .mapToList(defaultDispatcher)

    suspend fun sendMessage(contentMessage: String): Unit = withContext(defaultDispatcher) {
        // Save user message
        val userMessage = ChatMessage(role = ChatRole.User, content = contentMessage)
        chatMessageQueries.upsertChatMessage(
            id = uuid4().toString(),
            role = userMessage.role,
            content = userMessage.content,
            createdAt = Clock.System.now()
        )

        val messages = chatMessageQueries.getAllChatMessages()
            .executeAsList()
            .map(ChatMessageEntity::asModel)

        // Create request to OpenAI
        val request = ChatCompletionRequest(
            model = ModelId("gpt-3.5-turbo"),
            messages = messages,
        )

        // Get assistant response
        val assistantMessageId = uuid4().toString()
        val assistantMessageCreatedAt = Clock.System.now()
        var assistantMessage = ""

        openAI.chatCompletions(request).collect { chunk ->
            chunk.choices.first().delta?.content?.let {
                assistantMessage += it
                chatMessageQueries.upsertChatMessage(
                    id = assistantMessageId,
                    content = assistantMessage,
                    role = ChatRole.Assistant,
                    createdAt = assistantMessageCreatedAt
                )
            }
        }
    }

    suspend fun deleteAllMessages(): Unit = withContext(defaultDispatcher){
        chatMessageQueries.deleteAllMessages()
    }

}
