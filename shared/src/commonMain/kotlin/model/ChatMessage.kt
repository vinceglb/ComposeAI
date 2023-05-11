package model

import com.aallam.openai.api.chat.ChatMessage
import com.ebfstudio.appgpt.common.ChatMessageEntity

enum class ChatMessageStatus {
    LOADING,
    SENT,
    FAILED,
}

fun ChatMessageEntity.asModel(): ChatMessage = ChatMessage(
    content = content,
    role = role,
)

val ChatMessageEntity.isFailed: Boolean
    get() = status == ChatMessageStatus.FAILED
