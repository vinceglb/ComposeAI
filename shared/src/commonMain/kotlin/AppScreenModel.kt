
import AppScreenUiState.Loading
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import data.repository.PreferenceRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AppScreenModel : KoinComponent {

    // Waiting koinInject for Multiplatform to be released
    // https://insert-koin.io/docs/reference/koin-compose/multiplatform#koin-features-for-your-composable-wip
    private val preferenceRepository: PreferenceRepository by inject()
    private val coroutineScope: CoroutineScope = MainScope()

    var uiState: AppScreenUiState by mutableStateOf(Loading)
        private set

    init {
        coroutineScope.launch {
            val welcomeShown = preferenceRepository.welcomeShown()
            uiState = AppScreenUiState.Success(isWelcomeShown = welcomeShown)
        }
    }

}

sealed interface AppScreenUiState {
    object Loading : AppScreenUiState
    data class Success(val isWelcomeShown: Boolean) : AppScreenUiState
}
