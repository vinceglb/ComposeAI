
import AppScreenUiState.Loading
import AppScreenUiState.Success
import analytics.AnalyticsHelper
import analytics.AnalyticsInjected
import analytics.LocalAnalyticsHelper
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.Navigator
import ui.screens.chat.ChatScreen
import ui.screens.welcome.WelcomeScreen
import ui.theme.AppTheme

@Composable
fun App(
    analyticsHelper: AnalyticsHelper = AnalyticsInjected().analyticsHelper,
    setup: @Composable () -> Unit = {},
) {
    // Waiting koinInject for Multiplatform to be released
    // https://insert-koin.io/docs/reference/koin-compose/multiplatform#koin-features-for-your-composable-wip
    val appScreenModel = remember { AppScreenModel() }

    val screen = appScreenModel.uiState.let { uiState ->
        when (uiState) {
            Loading -> null
            is Success -> when (uiState.isWelcomeShown) {
                true -> ChatScreen
                false -> WelcomeScreen
            }
        }
    }

    CompositionLocalProvider(LocalAnalyticsHelper provides analyticsHelper) {
        AppTheme {
            setup()

            Surface {
                Crossfade (screen) { screen ->
                    when (screen) {
                        null -> Box(modifier = Modifier.fillMaxSize())
                        else -> Navigator(screen = screen)
                    }
                }
            }
        }
    }
}
