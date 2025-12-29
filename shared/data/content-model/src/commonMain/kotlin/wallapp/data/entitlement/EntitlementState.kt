package wallapp.data.entitlement

sealed interface EntitlementState {

    val isReady: Boolean
        get() = true

    data object Uninitialized : EntitlementState {
        override val isReady: Boolean = false
    }

    data object Locked : EntitlementState

    data object UnlockedReward : EntitlementState

    data object UnlockedFree : EntitlementState

    data object UnlockedFreeHd : EntitlementState

    data object SubscriberPlus : EntitlementState

    data object SubscriberAdFree : EntitlementState

    data object Purchased : EntitlementState
}

/**
 * SD content is always unlocked.
 * Note: Collections are not available in SD.
 */
val EntitlementState.isUnlockedSd: Boolean
    get() = true

val EntitlementState.isUnlockedHd: Boolean
    get() = this is EntitlementState.UnlockedReward
            || this is EntitlementState.UnlockedFreeHd
            || this is EntitlementState.SubscriberPlus
            || this is EntitlementState.SubscriberAdFree
            || this is EntitlementState.Purchased

val EntitlementState.isUnlockedCollection: Boolean
    get() = this is EntitlementState.SubscriberPlus
            || this is EntitlementState.Purchased

val EntitlementState.isUnlockedSdOrHd: Boolean
    get() = isUnlockedSd || isUnlockedHd

val EntitlementState.isUnlockedAny: Boolean
    get() = isUnlockedSd || isUnlockedHd || isUnlockedCollection

val EntitlementState.isSubscriberAny: Boolean
    get() = this is EntitlementState.SubscriberPlus || this is EntitlementState.SubscriberAdFree