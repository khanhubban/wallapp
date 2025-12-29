package wallapp.billing.revenuecat

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import wallapp.account.AccountManager
import wallapp.billing.BillingStateManager
import wallapp.buildconfig.BuildConfig
import wallapp.process.Process

class RevenueCatInitializerDefault(
    private val accountManager: AccountManager,
    private val revenueCatUserManager: RevenueCatUserManager,
    private val billingStateManager: BillingStateManager,
    private val process: Process,
    private val buildConfig: BuildConfig,
    private val coroutineScopeMain: CoroutineScope,
) : RevenueCatInitializer {

    private var isInitialized = false

    override fun initialize() {
        Log.d("[RevenueCatUser] Initializing RevenueCat")
        if (isInitialized) {
            return
        }
        require(process.isDefaultProcess) { "RevenueCat should only be initialized in the default process" }

        val enableSuperwall = false

        val projectApiKey = requireNotNull(RevenueCatPublicKeys.get(buildConfig.packageName)) {
            "RevenueCat public key not found for package name: ${buildConfig.packageName}"
        }
        val userId = accountManager.signedInUserId.value
        Log.d("[RevenueCatUser] Initializing RevenueCat - userId: $userId")
        billingStateManager.onUserUpdating()
        revenueCatUserManager.configure(
            projectApiKey = projectApiKey,
            enableSuperwall = enableSuperwall,
            userId = userId,
        )
        isInitialized = true

        coroutineScopeMain.launch {
            // Ignore first value to avoid unnecessary login/logout as configure takes care of the first state
            var ignoreFirst = true
            accountManager.signedInUserId.collect { userId ->
                if (ignoreFirst) {
                    ignoreFirst = false
                    return@collect
                }
//                delay(10000) // uncomment to test login/logout error cases on Android (by turning off network during delay)
                Log.d("[RevenueCatUser] Updating RevenueCat user - userId: $userId")
                if (userId != null) {
                    billingStateManager.onUserUpdating()
                    login(userId)
                } else if (!revenueCatUserManager.isCurrentUserAnonymous()) {
                    // The if check is needed because login could have failed
                    billingStateManager.onUserUpdating()
                    logout()
                }
            }
        }
    }

//    var count = 0
    fun login(userId: String) {
        // for testing login failure on iOS
//        if (count == 0) {
//            count++
//            billingStateManager.onLoginError(0, "test login error")
//            return
//        }
        revenueCatUserManager.login(userId) { error ->
            if (error != null) {
                billingStateManager.onLoginError(error.code, error.description)
            }
        }
    }

    fun logout() {
        revenueCatUserManager.logout { error ->
            if (error != null) {
                billingStateManager.onLogoutError(error.code, error.description)
            }
        }
    }
}