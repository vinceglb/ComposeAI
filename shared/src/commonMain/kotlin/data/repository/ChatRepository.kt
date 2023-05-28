package data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOne
import com.benasher44.uuid.uuid4
import com.ebfstudio.appgpt.common.ChatEntity
import com.ebfstudio.appgpt.common.ChatEntityQueries
import com.ebfstudio.appgpt.common.GetAllChats
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock

class ChatRepository(
    private val chatEntityQueries: ChatEntityQueries,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default,
) {

    fun getChatsStream(): Flow<List<GetAllChats>> =
        chatEntityQueries.getAllChats()
            .asFlow()
            .mapToList(defaultDispatcher)

    fun getChatStream(chatId: String): Flow<ChatEntity> =
        chatEntityQueries.getChatById(chatId)
            .asFlow()
            .mapToOne(defaultDispatcher)

    suspend fun createChat(): String = withContext(defaultDispatcher) {
        val chatId = uuid4().toString()
        chatEntityQueries.insertChat(
            id = chatId,
            createdAt = Clock.System.now(),
            title = null,
        )
        return@withContext chatId
    }

    suspend fun updateChatTitle(
        chatId: String,
        title: String,
    ): Unit = withContext(defaultDispatcher) {
        chatEntityQueries.updateChatTitle(title, chatId)
    }

}
