
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import ui.ChatScreen
import ui.theme.AppTheme

@Composable
fun App(setup: @Composable () -> Unit = {}) {
    AppTheme {
        setup()
        Navigator(ChatScreen)
    }
}
