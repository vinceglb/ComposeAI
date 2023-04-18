import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import ui.ChatScreen
import ui.theme.AppTheme

@Composable
fun App() {
    AppTheme {
        Navigator(ChatScreen)
    }
}
