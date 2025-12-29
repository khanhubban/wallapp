package wallapp.content.state.account

import androidx.compose.runtime.Immutable
import wallapp.pixel.view.ViewEvent

@Immutable
sealed interface AccountViewEvent : ViewEvent {

    @Immutable
    data object SignOut : AccountViewEvent

    @Immutable
    data object DeleteAccount : AccountViewEvent

    @Immutable
    data object RestorePurchases : AccountViewEvent

    @Immutable
    data object PrivacySettings: AccountViewEvent

    @Immutable
    data object NotificationsTapToGrantPermission: AccountViewEvent

    @Immutable
    data object ManageSubscription: AccountViewEvent
}

typealias AccountViewEventSink = (AccountViewEvent) -> Unit