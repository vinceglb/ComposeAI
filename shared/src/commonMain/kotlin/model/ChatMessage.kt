package model

import com.aallam.openai.api.chat.ChatMessage
import com.myapplication.common.ChatMessageEntity

fun ChatMessageEntity.asModel(): ChatMessage = ChatMessage(
    content = content,
    role = role,
)
