package data.local

import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.russhwolf.settings.coroutines.FlowSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class ChatMessageLocalDataSource(
    private val settings: FlowSettings
) {

    fun getMessages(): Flow<List<ChatMessage>> {
        return settings.getStringFlow(MESSAGES, "")
            .map { str ->
                if (str.isEmpty()) {
                    emptyList()
                } else {
                    str.split(DELIMITER1)
                        .map { it.toChatMessage() }
                }
            }
    }

    suspend fun addMessage(message: ChatMessage): List<ChatMessage> {
        // Add the message to the list of messages
        val messages = getMessages().first().toMutableList()
        messages.add(message)

        // Save the list of messages
        settings.putString(
            "messages",
            messages.joinToString(DELIMITER1) { it.toSettingsString() }
        )

        return messages
    }

    suspend fun updateLastMessage(delta: String) {
        val messages = getMessages().first().toMutableList()
        val lastMessage = messages.last()
        messages.removeLast()

        clearMessages()

        val newMessage = lastMessage.copy(content = lastMessage.content + delta)
        messages.add(newMessage)

        messages.forEach {
            addMessage(it)
        }
    }

    suspend fun clearMessages() {
        settings.putString(MESSAGES, "")
    }

    private fun ChatMessage.toSettingsString(): String =
        "${role.role}$DELIMITER2${content}"

    private fun String.toChatMessage(): ChatMessage {
        val (role, content) = split(DELIMITER2)
        return ChatMessage(role = ChatRole(role), content = content)
    }

    companion object {
        private const val MESSAGES = "messages"
        private const val DELIMITER1 = "/@\\"
        private const val DELIMITER2 = ":-:-:"
    }

}


