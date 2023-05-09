
import AppScreenUiState.Loading
import AppScreenUiState.Success
import data.repository.PreferenceRepository
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AppScreenModel : KoinComponent {

    // Waiting koinInject for Multiplatform to be released
    // https://insert-koin.io/docs/reference/koin-compose/multiplatform#koin-features-for-your-composable-wip
    private val preferenceRepository: PreferenceRepository by inject()

    val uiState: StateFlow<AppScreenUiState> =
        preferenceRepository.welcomeShown()
            .map { Success(it) }
            .stateIn(
                scope = MainScope(),
                initialValue = Loading,
                started = SharingStarted.WhileSubscribed(5_000),
            )

}

sealed interface AppScreenUiState {
    object Loading : AppScreenUiState
    data class Success(val isWelcomeShown: Boolean) : AppScreenUiState
}
