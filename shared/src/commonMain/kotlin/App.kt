import AppScreenUiState.Loading
import AppScreenUiState.Success
import analytics.AnalyticsHelper
import analytics.AnalyticsInjected
import analytics.LocalAnalyticsHelper
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.bottomSheet.BottomSheetNavigator
import ui.screens.chat.ChatScreen
import ui.screens.welcome.WelcomeScreen
import ui.theme.AppTheme

@OptIn(ExperimentalMaterialApi::class)
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
                Crossfade(screen) { screen ->
                    when (screen) {
                        null -> Box(modifier = Modifier.fillMaxSize())
                        else -> BottomSheetNavigator(
                            scrimColor = MaterialTheme.colorScheme.scrim.copy(alpha = 0.33f),
                            sheetShape = MaterialTheme.shapes.large.copy(
                                bottomStart = CornerSize(0.dp),
                                bottomEnd = CornerSize(0.dp)
                            ),
                            sheetBackgroundColor = MaterialTheme.colorScheme.surface,
                            sheetContentColor = MaterialTheme.colorScheme.onSurface,
                        ) {
                            Navigator(screen = screen)
                        }
                    }
                }
            }
        }
    }
}
