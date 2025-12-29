package wallapp.billing

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import wallapp.app.AppStateManager
import wallapp.content.state.upgrade.SubscriptionPlan
import wallapp.coroutine.collectIn
import wallapp.log.Logger
import wallapp.prefs.DevicePreferenceStorage

class BillingSubscriptionExpiredHandlerDefault(
    private val devicePreferenceStorage: DevicePreferenceStorage,
    private val billingManager: BillingManager,
    private val appStateManager: AppStateManager,
    private val coroutineScopeIo: CoroutineScope,
) : BillingSubscriptionExpiredHandler {

    companion object {
        val Log = Logger("[BillingSubscriptionExpiredHandler]")
    }

    private val maxExpiredSubscriptionsNotificationCount = 3
    private var expiredSubNotificationShown = false

    private var lastExpirationEpochTimeForWhichUserNotified: Long = 0
        set(value) {
            field = value
            devicePreferenceStorage.lastExpirationEpochTimeForWhichUserNotified.value = value
        }
        get() = devicePreferenceStorage.lastExpirationEpochTimeForWhichUserNotified.value
    private var lastExpiredSubscriptionNotifyCount: Int = 0
        set(value) {
            field = value
            devicePreferenceStorage.lastExpiredSubscriptionNotifyCount.value = value
        }
        get() = devicePreferenceStorage.lastExpiredSubscriptionNotifyCount.value

    override fun handleSubscriptionExpiredAndShowError() {
        coroutineScopeIo.launch {
            val expiredSubsResult = billingManager.queryExpiredSubscriptions()
            Log.d("expiredSubs : $expiredSubsResult")
            if (expiredSubsResult !is BillingSubscriptionsExpiredResult.Expired) return@launch
            if (expiredSubsResult.hasActiveSubscriptions) return@launch

            val lastExpiredSub = expiredSubsResult.expiredSubscriptions.lastOrNull()
            val lastExpiredEpochTime = lastExpiredSub?.expirationEpochTime
            when {
                lastExpiredEpochTime == null -> {
                    Log.d("lastExpiredEpochTime is null")
                }
                expiredSubNotificationShown -> {
                    Log.d("already notified in this session")
                }
                lastExpirationEpochTimeForWhichUserNotified == lastExpiredEpochTime &&
                        lastExpiredSubscriptionNotifyCount >= maxExpiredSubscriptionsNotificationCount -> {
                    Log.d("already notified for this expiration time")
                }
                else -> {
                    expiredSubNotificationShown = true
                    if (lastExpirationEpochTimeForWhichUserNotified != lastExpiredEpochTime) {
                        lastExpiredSubscriptionNotifyCount = 0
                    }
                    lastExpirationEpochTimeForWhichUserNotified = lastExpiredEpochTime
                    lastExpiredSubscriptionNotifyCount += 1
                    Log.d("show error for expired subscription - $lastExpiredSub")
                    appStateManager.navigateToPaywall(subscriptionExpired = true, subscriptionPlan = SubscriptionPlan.PlusUnlimited)
                }
            }
        }
    }

    init {
        billingManager.currentBillingPurchases.collectIn(coroutineScopeIo) {
            Log.d("currentBillingPurchases changed")
            handleSubscriptionExpiredAndShowError()
        }
    }
}