package ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.aallam.openai.api.BetaOpenAI
import di.getScreenModel

internal object ChatScreen : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
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
                    // fontWeight = FontWeight.Black,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp),
                )
            }
        }
    }

    @OptIn(BetaOpenAI::class)
    @Composable
    fun DisplayChat(screenModel: ChatScreenModel) {
        LazyColumn {
            items(screenModel.messages) { chatMessage ->
                Text(chatMessage.content, modifier = Modifier.padding(16.dp))
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ChatBottomBar(
        text: String,
        onTextChange: (String) -> Unit,
        onSend: () -> Unit,
    ) {
        BottomAppBar {
            Row {
//                BasicTextField(
//                    value = text,
//                    onValueChange = onTextChange,
//                    modifier = Modifier.weight(1f)
//                )
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
