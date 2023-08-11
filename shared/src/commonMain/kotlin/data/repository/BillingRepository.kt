package data.repository

import kotlinx.coroutines.flow.StateFlow
import model.AppProduct

expect class BillingRepository {
    val isSubToUnlimited: StateFlow<Boolean>
    val unlimitedSubProduct: StateFlow<AppProduct?>
}
