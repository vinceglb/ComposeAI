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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CopyAll
import androidx.compose.material.icons.rounded.IosShare
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import com.aallam.openai.api.chat.ChatRole
import com.myapplication.common.ChatMessageEntity
import com.myapplication.common.MainRes
import di.getScreenModel
import expect.platform
import expect.shareText
import io.github.skeptick.libres.compose.painterResource
import model.AppPlatform
import org.jetbrains.compose.resources.painterResource
import ui.images.AppImages

internal object ChatScreen : Screen {

    @Composable
    override fun Content() {
        val screenModel: ChatScreenModel = getScreenModel()
        val localClipboardManager = LocalClipboardManager.current

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

//                TestImages()

                DisplayChat(
                    messages = screenModel.messages,
                    onClickCopy = { localClipboardManager.setText(AnnotatedString(it)) },
                    onClickShare = { shareText(it) },
                )
            }
        }
    }

//    @Composable
//    fun TestImages() {
//        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
//            Image(
//                painter = painterResource(MainRes.image.appgpt2),
//                contentDescription = null,
//                modifier = Modifier
//                    .size(60.dp)
//                    .clip(RoundedCornerShape(4.dp))
//            )
//
//            Spacer(modifier = Modifier.width(16.dp))
//
//            Image(
//                painter = painterResource(MainRes.image.avatar2),
//                contentDescription = null,
//                modifier = Modifier
//                    .size(60.dp)
//                    .clip(RoundedCornerShape(4.dp))
//            )
//        }
//    }

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
            if (message.role == ChatRole.User) AppImages.avatar else AppImages.appgpt
        val cardAlpha = if (message.role == ChatRole.User) 0f else 0.4f
        val shareIcon =
            if (platform() == AppPlatform.ANDROID) Icons.Rounded.Share else Icons.Rounded.IosShare

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
                    SelectionContainer {
                        Text(message.content, modifier = Modifier.padding(top = 8.dp))
                    }

                    // Copy and share buttons
                    if (message.role == ChatRole.Assistant) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Row {
                            // Copy button
                            SuggestionChip(
                                onClick = { onClickCopy(message.content) },
                                label = { Text("Copy") },
                                icon = {
                                    Icon(
                                        Icons.Rounded.CopyAll,
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            // Share button
                            SuggestionChip(
                                onClick = { onClickShare(message.content) },
                                label = { Text("Share") },
                                icon = {
                                    Icon(
                                        shareIcon,
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            )
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
                        contentDescription = null
                    )
                }
            }
        }
    }
}

