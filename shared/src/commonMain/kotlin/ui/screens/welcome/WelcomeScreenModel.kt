package ui.screens.welcome

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import data.repository.PreferenceRepository
import kotlinx.coroutines.launch

class WelcomeScreenModel(
    private val preferenceRepository: PreferenceRepository,
) : ScreenModel {

    var uiState: WelcomeScreenUiState by mutableStateOf(WelcomeScreenUiState(false))
        private set

    fun setWelcomeShown() {
        screenModelScope.launch {
            preferenceRepository.setWelcomeShown()
            uiState = uiState.copy(doNavigateToChat = true)
        }
    }

}

data class WelcomeScreenUiState(
    val doNavigateToChat: Boolean,
)
