package data.repository

import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import data.local.ChatMessageLocalDataSource
import kotlinx.coroutines.flow.Flow

class ChatMessageRepository(
    private val openAI: OpenAI,
    private val chatMessageLocalDataSource: ChatMessageLocalDataSource,
) {

    fun getMessages(): Flow<List<ChatMessage>> =
        chatMessageLocalDataSource.getMessages()

    suspend fun sendMessage(contentMessage: String) {
        // Save user message
        val userMessage = ChatMessage(role = ChatRole.User, content = contentMessage)
        val messages = chatMessageLocalDataSource.addMessage(userMessage)

        // Create request to OpenAI
        val request = ChatCompletionRequest(
            model = ModelId("gpt-3.5-turbo"),
            messages = messages,
        )

        // Add empty assistant message
        val assistantMessage = ChatMessage(role = ChatRole.Assistant, content = "")
        chatMessageLocalDataSource.addMessage(assistantMessage)

        // Get assistant response
        openAI.chatCompletions(request).collect { chunk ->
            chunk.choices.first().delta?.content?.let {
                chatMessageLocalDataSource.updateLastMessage(it)
            }
        }
    }

}
