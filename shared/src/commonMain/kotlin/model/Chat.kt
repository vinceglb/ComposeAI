package model

import com.ebfstudio.appgpt.common.ChatEntity
import com.ebfstudio.appgpt.common.GetAllChats
import kotlinx.datetime.Instant

fun GetAllChats.toChats(): ChatEntity = ChatEntity(
    id = id,
    title = title,
    createdAt = createdAt,
)

val GetAllChats.updatedAt: Instant
    get() = updatedAtText?.let { Instant.parse(updatedAtText) } ?: createdAt
