package ui.screens.chat.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CopyAll
import androidx.compose.material.icons.rounded.IosShare
import androidx.compose.material.icons.rounded.Replay
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.aallam.openai.api.chat.ChatRole
import com.ebfstudio.appgpt.common.ChatMessageEntity
import expect.platform
import markdown.compose.Markdown
import markdown.model.markdownPadding
import model.AppPlatform
import model.isFailed
import org.jetbrains.compose.resources.painterResource
import ui.components.ImageUrl
import ui.components.appImagePath

@Composable
fun Messages(
    messages: List<ChatMessageEntity>,
    onClickCopy: (String) -> Unit,
    onClickShare: (String) -> Unit,
    onRetry: () -> Unit,
) {
    val reverseMessages = remember(messages) { messages.reversed() }
    val listState = rememberLazyListState()

    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(all = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        reverseLayout = true,
    ) {
        itemsIndexed(
            items = reverseMessages,
            key = { _, item -> item.id },
            contentType = { _, item -> item.role },
        ) { index, chatMessage ->
            MessageLine(
                chatMessage,
                isLast = index == 0,
                onClickCopy = onClickCopy,
                onClickShare = onClickShare,
                onRetry = onRetry,
            )
        }
    }

    // Scroll to the bottom whenever a new message appears
    LaunchedEffect(messages.size) {
        listState.animateScrollToItem(0)
    }
}

@Composable
fun MessageLine(
    message: ChatMessageEntity,
    isLast: Boolean,
    onClickCopy: (String) -> Unit,
    onClickShare: (String) -> Unit,
    onRetry: () -> Unit,
) {
    val assistantImage = appImagePath()

    val shareIcon = when (platform() == AppPlatform.ANDROID) {
        true -> Icons.Rounded.Share
        else -> Icons.Rounded.IosShare
    }

    val containerColor = when (message.role) {
        ChatRole.User -> MaterialTheme.colorScheme.surfaceVariant
        else -> MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)
    }

    val contentColor = when (message.role) {
        ChatRole.User -> MaterialTheme.colorScheme.onSurfaceVariant
        else -> MaterialTheme.colorScheme.onSurface
    }

    val borderColor = when (message.role) {
        ChatRole.User -> MaterialTheme.colorScheme.surfaceColorAtElevation(40.dp)
        else -> MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp)
    }

    var showOptions by remember { mutableStateOf<Boolean?>(null) }

    Card(
        colors = CardDefaults.cardColors(
            contentColor = contentColor,
            containerColor = containerColor,
        ),
        border = BorderStroke(1.dp, borderColor),
        onClick = { showOptions = showOptions?.let { !it } ?: !isLast },
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(modifier = Modifier.padding(start = 12.dp, top = 12.dp, end = 16.dp, bottom = 16.dp)) {
            if (message.role == ChatRole.Assistant) {
                Image(
                    painter = painterResource(assistantImage),
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(MaterialTheme.shapes.small)
                        .border(2.dp, borderColor, MaterialTheme.shapes.small)
                )
            } else {
                ImageUrl(
                    url = "https://api.dicebear.com/6.x/shapes/svg?seed=${message.id}",
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(MaterialTheme.shapes.small)
                        .border(2.dp, borderColor, MaterialTheme.shapes.small)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                // Content text
                if (message.isFailed.not()) {
                    Markdown(
                        content = message.content,
                        modifier = Modifier.padding(top = 9.dp),
                        padding = markdownPadding(block = 24.dp, list = 8.dp)
                    )
                } else {
                    Text(
                        "Failed to receive response",
                        modifier = Modifier.padding(top = 8.dp),
                        color = MaterialTheme.colorScheme.error
                    )
                }

                // Copy and share buttons
                if (message.role == ChatRole.Assistant && !message.isFailed) {
                    AnimatedVisibility(
                        visible = showOptions == true || (isLast && showOptions == null),
                    ) {
                        Row(modifier = Modifier.padding(top = 16.dp)) {
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
                                },
                                shape = CircleShape,
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
                                },
                                shape = CircleShape,
                            )
                        }
                    }
                }

                AnimatedVisibility(
                    visible = message.isFailed,
                ) {
                    // Retry button
                    SuggestionChip(
                        onClick = onRetry,
                        label = { Text("Retry") },
                        icon = {
                            Icon(
                                Icons.Rounded.Replay,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        shape = CircleShape,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
            }
        }
    }
}