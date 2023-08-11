package data.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import model.AppProduct

actual class BillingRepository {
    actual val isSubToUnlimited: StateFlow<Boolean> = MutableStateFlow(false)
    actual val unlimitedSubProduct: StateFlow<AppProduct?> = TODO("Not yet implemented")
}
