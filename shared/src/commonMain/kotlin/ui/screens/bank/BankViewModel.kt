package ui.screens.bank

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.coroutineScope
import data.repository.BillingRepository
import data.repository.CoinRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import model.AppProduct

class BankViewModel(
    billingRepository: BillingRepository,
    private val coinRepository: CoinRepository,
) : ScreenModel {

    val uiState: StateFlow<BankUiState> = combine(
        coinRepository.coins(),
        billingRepository.unlimitedSubProduct,
        billingRepository.isSubToUnlimited,
    ) { coins, unlimitedSub, isSubToUnlimited ->
        BankUiState.Success(
            coins = coins,
            unlimitedSub = unlimitedSub,
            isSubToUnlimited = isSubToUnlimited,
        )
    }.stateIn(coroutineScope, SharingStarted.WhileSubscribed(5_000), BankUiState.Loading)

    fun onRewardEarned(coins: Int) {
        coroutineScope.launch {
            coinRepository.useCoins(add = coins)
        }
    }

}

sealed class BankUiState {
    object Loading : BankUiState()

    data class Success(
        val coins: Int = 0,
        val unlimitedSub: AppProduct? = null,
        val isSubToUnlimited: Boolean = false,
    ) : BankUiState()
}
