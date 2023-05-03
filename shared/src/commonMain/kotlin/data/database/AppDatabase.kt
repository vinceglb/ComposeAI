package data.database

import app.cash.sqldelight.ColumnAdapter
import app.cash.sqldelight.db.SqlDriver
import com.aallam.openai.api.chat.ChatRole
import com.ebfstudio.appgpt.common.ChatMessageEntity
import com.ebfstudio.appgpt.common.Database
import kotlinx.datetime.Instant

object AppDatabase {
    fun getDatabase(driver: SqlDriver): Database {
        return Database(
            driver = driver,
            ChatMessageEntityAdapter = ChatMessageEntity.Adapter(
                roleAdapter = ChatRoleAdapter,
                createdAtAdapter = InstantAdapter,
            )
        )
    }

    private val ChatRoleAdapter = object : ColumnAdapter<ChatRole, String> {
        override fun decode(databaseValue: String): ChatRole {
            return ChatRole(databaseValue)
        }

        override fun encode(value: ChatRole): String {
            return value.role
        }
    }

    private val InstantAdapter = object : ColumnAdapter<Instant, String> {
        override fun decode(databaseValue: String): Instant {
            return Instant.parse(databaseValue)
        }

        override fun encode(value: Instant): String {
            return value.toString()
        }
    }
}

expect class DriverFactory {
    fun createDriver(): SqlDriver
}
