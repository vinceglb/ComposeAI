package ui.screens.bank

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import data.repository.BillingRepository
import data.repository.CoinRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BankViewModel(
    billingRepository: BillingRepository,
    private val coinRepository: CoinRepository,
) : ScreenModel {

    val uiState: StateFlow<BankUiState> = combine(
        coinRepository.coins(),
        billingRepository.isSubToUnlimited,
    ) { coins, isSubToUnlimited ->
        BankUiState.Success(
            coins = coins,
            isSubToUnlimited = isSubToUnlimited,
        )
    }.stateIn(screenModelScope, SharingStarted.WhileSubscribed(5_000), BankUiState.Loading)

    fun onRewardEarned(coins: Int) {
        screenModelScope.launch {
            coinRepository.useCoins(add = coins)
        }
    }

}

sealed class BankUiState {
    data object Loading : BankUiState()

    data class Success(
        val coins: Int = 0,
        val isSubToUnlimited: Boolean = false,
    ) : BankUiState()
}
