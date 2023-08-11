package ui.screens.chat

import analytics.AnalyticsHelper
import analytics.logConversationSelected
import analytics.logCreateNewConversation
import analytics.logMessageCopied
import analytics.logMessageShared
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.coroutineScope
import com.ebfstudio.appgpt.common.ChatEntity
import com.ebfstudio.appgpt.common.ChatMessageEntity
import com.ebfstudio.appgpt.common.GetAllChats
import data.repository.BillingRepository
import data.repository.ChatMessageRepository
import data.repository.ChatRepository
import data.repository.CoinRepository
import expect.shareText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import model.toChats
import model.updatedAt
import kotlin.time.Duration.Companion.minutes

class ChatScreenModel(
    private val chatRepository: ChatRepository,
    private val chatMessageRepository: ChatMessageRepository,
    private val coinRepository: CoinRepository,
    private val billingRepository: BillingRepository,
    private val analyticsHelper: AnalyticsHelper,
    initialChatId: String?,
) : ScreenModel {

    private var chatId: MutableStateFlow<String?> =
        MutableStateFlow(initialChatId)

    val screenUiState: MutableStateFlow<ChatScreenUiState> =
        MutableStateFlow(ChatScreenUiState())

    val currentChatUiState: StateFlow<ChatMessagesUiState> =
        chatId.flatMapLatest { id ->
            if (id == null) {
                MutableStateFlow(ChatMessagesUiState.Empty)
            } else {
                combine(
                    chatRepository.getChatStream(id),
                    chatMessageRepository.getMessagesStream(id),
                ) { chat, messages ->
                    ChatMessagesUiState.Success(
                        chat = chat,
                        messages = messages,
                    )
                }
            }
        }.stateIn(
            scope = coroutineScope,
            started = SharingStarted.Eagerly,
            initialValue = ChatMessagesUiState.Loading,
        )

    val chatsUiState: StateFlow<ChatsUiState> =
        chatRepository.getChatsStream()
            .map { chats -> chats.map(GetAllChats::toChats) }
            .map { chats -> ChatsUiState.Success(chats = chats) }
            .stateIn(
                scope = coroutineScope,
                started = SharingStarted.Eagerly,
                initialValue = ChatsUiState.Loading,
            )

    init {
        // If no chat id is provided, get the latest chat id
        if (initialChatId == null) {
            coroutineScope.launch {
                val latestChat = chatRepository.getChatsStream().first().firstOrNull()
                val fiveMinutesAgo = Clock.System.now().minus(5.minutes)

                if ((latestChat != null) && (latestChat.updatedAt > fiveMinutesAgo)) {
                    chatId.update { latestChat.id }
                }
            }
        }

        coroutineScope.launch {
            coinRepository.coins().collect { coins ->
                screenUiState.update { it.copy(coins = coins) }
            }
        }

        coroutineScope.launch {
            billingRepository.isSubToUnlimited.collect { isSubToUnlimited ->
                screenUiState.update { it.copy(isSubToUnlimited = isSubToUnlimited) }
            }
        }
    }

    fun onSendMessage() {
        coroutineScope.launch {
            // Get chat id, or create a new one
            val chatId = when {
                chatId.value != null -> chatId.value
                else -> {
                    val newId = chatRepository.createChat()
                    chatId.updateAndGet { newId }
                }
            } ?: return@launch

            // Get message text (before reset)
            val contentText = screenUiState.value.text

            // Reset text and start loading
            screenUiState.update {
                it.copy(
                    text = "",
                    isSending = true
                )
            }

            // Send message
            val sendMessageResult = chatMessageRepository.sendMessage(
                chatId = chatId,
                contentMessage = contentText
            )

            // Stop loading
            screenUiState.update {
                it.copy(isSending = false)
            }

            // Update chat title for the first message
            if (sendMessageResult.isSuccess && sendMessageResult.getOrThrow() == 1) {
                chatMessageRepository.generateTitleFromChat(chatId).onSuccess {
                    chatRepository.updateChatTitle(chatId, it)
                }
            }
        }
    }

    fun onRetrySendMessage() {
        val chatId = chatId.value ?: return

        coroutineScope.launch {
            // Start loading
            screenUiState.update {
                it.copy(isSending = true)
            }

            // Retry send message
            chatMessageRepository.retrySendMessage(chatId = chatId)

            // Stop loading
            screenUiState.update {
                it.copy(isSending = false)
            }
        }
    }

    fun onTextChange(text: String) {
        screenUiState.update { it.copy(text = text) }
    }

    fun onNewChat() {
        chatId.update { null }
        analyticsHelper.logCreateNewConversation()
    }

    fun onChatSelected(chatId: String) {
        this.chatId.update { chatId }
        analyticsHelper.logConversationSelected()
    }

    fun onMessageCopied() {
        analyticsHelper.logMessageCopied()
    }

    fun onMessageShared(text: String) {
        shareText(text)
        analyticsHelper.logMessageShared()
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
        val chat: ChatEntity? = null,
        val messages: List<ChatMessageEntity> = emptyList(),
    ) : ChatMessagesUiState

    val chatOrNull: ChatEntity?
        get() = when (this) {
            is Success -> chat
            else -> null
        }
}

data class ChatScreenUiState(
    val text: String = "",
    val isSending: Boolean = false,
    val coins: Int = 0,
    val isSubToUnlimited: Boolean = false,
)
