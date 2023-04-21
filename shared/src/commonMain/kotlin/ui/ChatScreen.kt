package ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import com.aallam.openai.api.chat.ChatRole
import com.myapplication.common.ChatMessageEntity
import di.getScreenModel
import org.jetbrains.compose.resources.painterResource

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
                DisplayChat(
                    messages = screenModel.messages,
                    onClickCopy = {},
                    onClickShare = {},
                )
            }
        }
    }

    @Composable
    fun DisplayChat(
        messages: List<ChatMessageEntity>,
        onClickCopy: (String) -> Unit,
        onClickShare: (String) -> Unit,
    ) {
        LazyColumn(
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            reverseLayout = true,
        ) {
            items(messages.reversed()) { chatMessage ->
                MessageLine(
                    chatMessage,
                    onClickCopy = onClickCopy,
                    onClickShare = onClickShare,
                )
            }
        }
    }

    @Composable
    fun MessageLine(
        message: ChatMessageEntity,
        onClickCopy: (String) -> Unit,
        onClickShare: (String) -> Unit,
    ) {
        val avatar =
            if (message.role == ChatRole.User) "images/avatar.jpeg" else "images/appgpt-icon.png"
        val cardAlpha = if (message.role == ChatRole.User) 0f else 0.4f

        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(
                    alpha = cardAlpha
                )
            ),
            modifier = Modifier
                .padding(horizontal = 4.dp)
                .fillMaxWidth(),
        ) {
            Row(modifier = Modifier.padding(12.dp)) {
                Image(
                    painter = painterResource(avatar),
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(MaterialTheme.shapes.small)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    // Content text
                    Text(message.content, modifier = Modifier.padding(top = 8.dp))

                    // Copy and share buttons
                    if (message.role == ChatRole.Assistant) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row {
                            // Copy button
                            IconButton(
                                onClick = { onClickCopy(message.content) },
                            ) {
                                Icon(
                                    Icons.Rounded.Add,
                                    contentDescription = null,
                                    // tint = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            // Share button
                            IconButton(
                                onClick = { onClickShare(message.content) },
                            ) {
                                Icon(
                                    Icons.Rounded.Share,
                                    contentDescription = null,
                                    // tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
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
                TextField(
                    value = text,
                    onValueChange = onTextChange,
                    modifier = Modifier.weight(1f),
                    shape = CircleShape,
                    colors = TextFieldDefaults.textFieldColors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                    ),
                    placeholder = { Text("Type your message") },
                    maxLines = 3,
                )
                Spacer(modifier = Modifier.width(8.dp))
                FilledIconButton(
                    onClick = onSend,
                    modifier = Modifier.size(56.dp),
                    enabled = text.isNotBlank(),
                ) {
                    Icon(
                        Icons.Rounded.Send,
                        contentDescription = null,

                        )
                }
            }
        }
    }

}
