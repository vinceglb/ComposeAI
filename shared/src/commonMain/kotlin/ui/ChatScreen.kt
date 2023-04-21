package ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import com.myapplication.common.ChatMessageEntity
import di.getScreenModel

internal object ChatScreen : Screen {

    @Composable
    override fun Content() {
        val screenModel: ChatScreenModel = getScreenModel()

        Scaffold(
            bottomBar = {
                ChatBottomBar(
                    text = screenModel.text,
                    onTextChange = { screenModel.text = it },
                    onSend = {
                        screenModel.sendMessage(screenModel.text)
                    }
                )
            }
        ) { contentPadding ->
            Column(modifier = Modifier.padding(contentPadding)) {
                Text(
                    "Chat Screen",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp),
                )
                DisplayChat(screenModel.messages)
            }
        }
    }

    @Composable
    fun DisplayChat(messages: List<ChatMessageEntity>) {
        LazyColumn {
            items(messages) { chatMessage ->
                Text(chatMessage.content, modifier = Modifier.padding(16.dp))
            }
        }
    }

    @Composable
    fun ChatBottomBar(
        text: String,
        onTextChange: (String) -> Unit,
        onSend: () -> Unit,
    ) {
        BottomAppBar {
            Row {
                OutlinedTextField(
                    value = text,
                    onValueChange = onTextChange,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onSend) {
                    Icon(Icons.Default.Send, contentDescription = null)
                }
            }
        }
    }

}
