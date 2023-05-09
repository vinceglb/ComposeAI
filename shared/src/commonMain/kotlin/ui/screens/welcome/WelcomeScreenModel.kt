package ui.screens.welcome

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.coroutineScope
import data.repository.PreferenceRepository
import kotlinx.coroutines.launch

class WelcomeScreenModel(
    private val preferenceRepository: PreferenceRepository,
) : ScreenModel {

    fun setWelcomeShown() {
        coroutineScope.launch {
            preferenceRepository.setWelcomeShown()
        }
    }

}
