package ui.screens.chat

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.coroutineScope
import com.ebfstudio.appgpt.common.ChatEntity
import com.ebfstudio.appgpt.common.ChatMessageEntity
import data.repository.ChatMessageRepository
import data.repository.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChatScreenModel(
    private val chatRepository: ChatRepository,
    private val chatMessageRepository: ChatMessageRepository,
    initialChatId: String?,
) : ScreenModel {

    private var chatId: MutableStateFlow<String?> =
        MutableStateFlow(initialChatId)

    val screenUiState: MutableStateFlow<ChatScreenUiState> =
        MutableStateFlow(ChatScreenUiState())

    val messagesUiState: StateFlow<ChatMessagesUiState> =
        chatId.flatMapLatest { id ->
            if (id == null) {
                MutableStateFlow(ChatMessagesUiState.Empty)
            } else {
                chatRepository.getChatStream(id).flatMapLatest {
                    chatMessageRepository.getMessagesStream(id)
                        .map { messages ->
                            ChatMessagesUiState.Success(messages = messages)
                        }
                }
            }
        }.stateIn(
            scope = coroutineScope,
            started = SharingStarted.Eagerly,
            initialValue = ChatMessagesUiState.Loading,
        )

    val currentChat: StateFlow<ChatEntity?> =
        chatId.flatMapLatest { id ->
            if (id == null) {
                MutableStateFlow(null)
            } else {
                chatRepository.getChatStream(id)
            }
        }.stateIn(
            scope = coroutineScope,
            started = SharingStarted.Eagerly,
            initialValue = null,
        )

    val chatsUiState: StateFlow<ChatsUiState> =
        chatRepository.getChatsStream()
            .map { chats -> ChatsUiState.Success(chats = chats) }
            .stateIn(
                scope = coroutineScope,
                started = SharingStarted.Eagerly,
                initialValue = ChatsUiState.Loading,
            )

    init {
        if (initialChatId == null) {
            coroutineScope.launch {
                val latestChat = chatRepository.getChatsStream().first().firstOrNull()
                if (latestChat != null) {
                    chatId.update { latestChat.id }
                } else {
                    onNewChat()
                }
            }
        }
    }

    fun onSendMessage() {
        val chatId = this.chatId.value ?: return

        coroutineScope.launch {
            val contentMessage = screenUiState.value.text

            // Reset text and start loading
            screenUiState.update {
                it.copy(
                    text = "",
                    isSending = true
                )
            }

            // Send message
            chatMessageRepository.sendMessage(
                chatId = chatId,
                contentMessage = contentMessage
            )

            // Stop loading
            screenUiState.update {
                it.copy(isSending = false)
            }

            val title = chatMessageRepository.generateTitleFromChat(chatId)
            chatRepository.updateChatTitle(chatId, title)
        }
    }

    fun onTextChange(text: String) {
        screenUiState.update { it.copy(text = text) }
    }

    fun onNewChat() {
        coroutineScope.launch {
            val id = chatRepository.createChat()
            chatId.update { id }
        }
    }

    fun onChatSelected(chatId: String) {
        this.chatId.update { chatId }
    }

}

sealed interface ChatsUiState {
    object Loading : ChatsUiState
    data class Success(
        val chats: List<ChatEntity> = emptyList()
    ) : ChatsUiState
}

sealed interface ChatMessagesUiState {
    object Empty : ChatMessagesUiState
    object Loading : ChatMessagesUiState
    data class Success(
        val messages: List<ChatMessageEntity> = emptyList()
    ) : ChatMessagesUiState
}

data class ChatScreenUiState(
    val text: String = "",
    val isSending: Boolean = false,
)
