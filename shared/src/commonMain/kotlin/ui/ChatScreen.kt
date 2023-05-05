package ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ChatBubble
import androidx.compose.material.icons.rounded.ChatBubbleOutline
import androidx.compose.material.icons.rounded.CopyAll
import androidx.compose.material.icons.rounded.Forum
import androidx.compose.material.icons.rounded.IosShare
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import com.aallam.openai.api.chat.ChatRole
import com.ebfstudio.appgpt.common.ChatEntity
import com.ebfstudio.appgpt.common.ChatMessageEntity
import di.getScreenModel
import expect.platform
import expect.shareText
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import model.AppPlatform
import org.jetbrains.compose.resources.painterResource
import org.koin.core.parameter.parametersOf
import ui.images.AppImages

internal object ChatScreen : Screen {

    @Composable
    override fun Content() {
        val screenModel: ChatScreenModel = getScreenModel { parametersOf(null as String?) }
        val messagesUiState by screenModel.messagesUiState.collectAsState()
        val screenUiState by screenModel.screenUiState.collectAsState()
        val chatsUiState by screenModel.chatsUiState.collectAsState()
        val currentChat by screenModel.currentChat.collectAsState()

        ContentScreen(
            onSend = screenModel::onSendMessage,
            onNewChat = screenModel::onNewChat,
            onChatSelected = screenModel::onChatSelected,
            onTextChange = screenModel::onTextChange,
            screenUiState = screenUiState,
            messagesUiState = messagesUiState,
            chatsUiState = chatsUiState,
            currentChat = currentChat,
        )
    }

    @Composable
    fun ContentScreen(
        onSend: () -> Unit,
        onNewChat: () -> Unit,
        onChatSelected: (String) -> Unit,
        onTextChange: (String) -> Unit,
        screenUiState: ChatScreenUiState,
        messagesUiState: ChatMessagesUiState,
        chatsUiState: ChatsUiState,
        currentChat: ChatEntity?,
    ) {
        val localClipboardManager = LocalClipboardManager.current
        val drawerState = rememberDrawerState(DrawerValue.Closed)
        val scope = rememberCoroutineScope()
        val focusRequester = remember { FocusRequester() }

        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet {
                    Spacer(Modifier.height(12.dp))
                    when (chatsUiState) {
                        ChatsUiState.Loading -> Unit
                        is ChatsUiState.Success -> {
                            chatsUiState.chats.forEach { chat ->
                                val isSelected = chat.id == currentChat?.id
                                NavigationDrawerItem(
                                    label = { Text(chat.title ?: "Empty chat") },
                                    icon = {
                                        Icon(
                                            if (isSelected) Icons.Rounded.ChatBubble else Icons.Rounded.ChatBubbleOutline,
                                            contentDescription = null,
                                        )
                                    },
                                    selected = isSelected,
                                    onClick = {
                                        onChatSelected(chat.id)
                                        scope.launch { drawerState.close() }
                                    },
                                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                                )
                            }
                        }
                    }
                }
            },
        ) {
            Scaffold(
                topBar = {
                    ChatTopBar(
                        chatTitle = currentChat?.title,
                        onNewChat = {
                            onNewChat()
                            focusRequester.requestFocus()
                        },
                        onMenuClick = { scope.launch { drawerState.open() } },
                    )
                },
                bottomBar = {
                    ChatBottomBar(
                        text = screenUiState.text,
                        isLoading = screenUiState.isSending,
                        focusRequester = focusRequester,
                        onTextChange = onTextChange,
                        onSend = onSend,
                    )
                },
            ) { contentPadding ->
                Column(modifier = Modifier.padding(contentPadding)) {
                    when (messagesUiState) {
                        ChatMessagesUiState.Empty,
                        ChatMessagesUiState.Loading -> Unit

                        is ChatMessagesUiState.Success -> {
                            DisplayChat(
                                messages = messagesUiState.messages,
                                onClickCopy = { localClipboardManager.setText(AnnotatedString(it)) },
                                onClickShare = { shareText(it) },
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun DisplayChat(
        messages: List<ChatMessageEntity>,
        onClickCopy: (String) -> Unit,
        onClickShare: (String) -> Unit,
    ) {
        val reverseMessages = remember(messages) { messages.reversed() }

        LazyColumn(
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            reverseLayout = true,
        ) {
            items(reverseMessages) { chatMessage ->
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
        val cardAlpha = if (message.role == ChatRole.User) 0.5f else 0.25f
        val shareIcon =
            if (platform() == AppPlatform.ANDROID) Icons.Rounded.Share else Icons.Rounded.IosShare

        var showOptions by remember { mutableStateOf(false) }

        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(
                    alpha = cardAlpha
                )
            ),
            onClick = { showOptions = !showOptions },
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
                        AnimatedVisibility(
                            visible = showOptions,
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
                }
            }
        }
    }

    @Composable
    fun ChatTopBar(
        chatTitle: String?,
        onNewChat: () -> Unit,
        onMenuClick: () -> Unit,
    ) {
        CenterAlignedTopAppBar(
            title = {
                TypewriterText(
                    text = chatTitle ?: "Compose AI",
                    overflow = TextOverflow.Ellipsis,
                    softWrap = false,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            },
            navigationIcon = {
                IconButton(
                    onClick = onMenuClick,
                ) {
                    Icon(
                        Icons.Rounded.Forum,
                        contentDescription = null,
                    )
                }
            },
            actions = {
                IconButton(
                    onClick = onNewChat,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        Icons.Rounded.Add,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        )
    }

    @Composable
    fun ChatBottomBar(
        text: String,
        isLoading: Boolean,
        focusRequester: FocusRequester,
        onTextChange: (String) -> Unit,
        onSend: () -> Unit,
    ) {
        val enableSend = text.isNotBlank() && !isLoading
        val transition = updateTransition(targetState = enableSend)
        val sendContainerColor by transition.animateColor { state ->
            when (state) {
                true -> MaterialTheme.colorScheme.primary
                false -> Color.Transparent
            }
        }
        val sendContentColor by transition.animateColor { state ->
            when (state) {
                true -> MaterialTheme.colorScheme.onPrimary
                false -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
            }
        }
        val sendIconRotation by transition.animateFloat { state ->
            when (state) {
                true -> -45f
                false -> -0f
            }
        }

        Surface {
            Column {
                AnimatedVisibility(
                    visible = isLoading,
                    enter = expandVertically(),
                    exit = shrinkVertically()
                ) {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp)
                    )
                }
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.Bottom,
                ) {
                    Surface(
                        shape = MaterialTheme.shapes.large,
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .defaultMinSize(minHeight = 48.dp)
                            .weight(1f)
                    ) {
                        Box(
                            contentAlignment = Alignment.CenterStart,
                            modifier = Modifier.weight(1f)
                        ) {
                            BasicTextField(
                                value = text,
                                onValueChange = onTextChange,
                                maxLines = 3,
                                textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface),
                                cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
                                decorationBox = { innerTextField ->
                                    if (text.isBlank()) {
                                        Text(
                                            text = "Ask me anything...",
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.alpha(0.6f)
                                        )
                                    }
                                    innerTextField()
                                },
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Text,
                                    capitalization = KeyboardCapitalization.Sentences,
                                ),
                                modifier = Modifier
                                    .padding(horizontal = 16.dp, vertical = 12.dp)
                                    .fillMaxWidth(1f)
                                    .focusRequester(focusRequester)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    FilledIconButton(
                        onClick = onSend,
                        enabled = enableSend,
                        shape = MaterialTheme.shapes.large,
                        modifier = Modifier.size(48.dp),
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = sendContainerColor,
                            disabledContainerColor = sendContainerColor,
                            contentColor = sendContentColor,
                            disabledContentColor = sendContentColor,
                        )
                    ) {
                        Icon(
                            Icons.Rounded.Send,
                            contentDescription = null,
                            modifier = Modifier.rotate(sendIconRotation)
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun TypewriterText(
        text: String,
        modifier: Modifier = Modifier,
        overflow: TextOverflow = TextOverflow.Clip,
        softWrap: Boolean = true,
    ) {
        var targetText by remember { mutableStateOf(text) }
        var currentText by remember { mutableStateOf("") }

        LaunchedEffect(text) {
            if (targetText != text) {
                for (i in currentText.length - 1 downTo 0) {
                    delay(50)
                    currentText = currentText.substring(0, i)
                }
                targetText = text
            }

            for (i in currentText.length until text.length) {
                delay(50)
                currentText += text[i]
            }
        }

        Text(
            currentText,
            overflow = overflow,
            softWrap = softWrap,
            modifier = modifier,
        )
    }
}

