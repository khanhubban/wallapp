package wallapp.billing.revenuecat

import android.content.Context
import com.revenuecat.purchases.CustomerInfo
import com.revenuecat.purchases.EntitlementVerificationMode
import com.revenuecat.purchases.LogLevel
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesConfiguration
import com.revenuecat.purchases.PurchasesError
import com.revenuecat.purchases.interfaces.LogInCallback
import com.revenuecat.purchases.interfaces.ReceiveCustomerInfoCallback
import wallapp.buildconfig.BuildConfig

class RevenueCatUserManagerAndroid(
    private val context: Context,
    private val buildConfig: BuildConfig,
) : RevenueCatUserManager {

    private val revenueCat by lazy { Purchases.sharedInstance }

    private val appUserIdListeners = mutableListOf<CurrentAppUserIdListener>()
    private var currentAppUserId: String? = null
        set(value) {
            field = value
            appUserIdListeners.forEach { it.onCurrentAppUserIdChanged(value ?: "") }
        }

    override fun configure(projectApiKey: String, enableSuperwall: Boolean, userId: String?) {
        Log.i("Initializing RevenueCat - start")

        if (buildConfig.debug) {
            Purchases.logLevel = LogLevel.VERBOSE
        }
        val builder = PurchasesConfiguration.Builder(context, projectApiKey).apply {
            if (userId != null) {
                this.appUserID(userId)
            }
            this.entitlementVerificationMode(EntitlementVerificationMode.INFORMATIONAL)
        }
        val purchasesInstance = Purchases.configure(
            configuration = builder.build(),
        )
        currentAppUserId = purchasesInstance.appUserID
        Log.i("Initializing RevenueCat - complete")
    }

    override fun login(userId: String, completion: (RevenueCatErrors?) -> Unit) {
        revenueCat.logIn(userId, callback = object : LogInCallback {
            override fun onError(error: PurchasesError) {
                completion(RevenueCatErrors(error.code.code, error.code.description))
            }

            override fun onReceived(customerInfo: CustomerInfo, created: Boolean) {
                currentAppUserId = userId
            }
        })
    }

    override fun logout(completion: (RevenueCatErrors?) -> Unit) {
        revenueCat.logOut(callback = object : ReceiveCustomerInfoCallback {
            override fun onError(error: PurchasesError) {
                completion(RevenueCatErrors(error.code.code, error.code.description))
            }

            override fun onReceived(customerInfo: CustomerInfo) {
                currentAppUserId = customerInfo.originalAppUserId
            }
        })
    }

    override fun addCurrentAppUserIdListener(listener: CurrentAppUserIdListener) {
        appUserIdListeners.add(listener)
        currentAppUserId?.let { listener.onCurrentAppUserIdChanged(it) }
    }

    override fun removeCurrentAppUserIdListener(listener: CurrentAppUserIdListener) {
        appUserIdListeners.remove(listener)
    }

    override fun isCurrentUserAnonymous(): Boolean {
        return revenueCat.isAnonymous
    }
}