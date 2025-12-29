package wallapp.purchase

import kotlinx.coroutines.flow.Flow

interface PurchaseUiManager {

    val showToolbarButton: Flow<Boolean>

    val showInAccount: Flow<Boolean>

}