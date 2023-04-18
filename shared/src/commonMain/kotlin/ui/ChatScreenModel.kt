package ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.coroutineScope
import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import data.ChatRepository
import data.FakeRepository
import kotlinx.coroutines.launch

@OptIn(BetaOpenAI::class)
class ChatScreenModel(
    private val repo: FakeRepository,
    private val chatRepo: ChatRepository,
) : ScreenModel {

    var text by mutableStateOf("Hello, World!")

    var messages: List<ChatMessage> by mutableStateOf(emptyList())
        private set

    fun fetchText() {
        coroutineScope.launch {
            text = repo.getData()
        }
    }

    fun sendMessage(message: String) {
        // Add message
        messages = messages + ChatMessage(role = ChatRole.User, content = message)

        // Reset text
        text = ""

        // Send message
        coroutineScope.launch {
            val response = chatRepo.sendMessage(messages)
            response.choices.first().message?.let {
                messages = messages + it
            }
        }
    }

}
