
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import ui.screens.chat.ChatScreen
import ui.theme.AppTheme

@Composable
fun App(setup: @Composable () -> Unit = {}) {
    AppTheme {
        setup()

        Surface {
            Navigator(ChatScreen)
        }
    }
}
