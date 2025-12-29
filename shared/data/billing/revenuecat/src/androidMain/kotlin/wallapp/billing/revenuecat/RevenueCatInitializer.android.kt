package wallapp.billing.revenuecat

import android.content.Context
import com.revenuecat.purchases.LogLevel
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesConfiguration
import wallapp.buildconfig.BuildConfig
import wallapp.process.Process


class RevenueCatInitializerAndroid(
    private val context: Context,
    private val buildConfig: BuildConfig,
    private val process: Process,
) : RevenueCatInitializer {

    override fun initialize() {
        Log.i("Initializing RevenueCat - start")

        require(process.isDefaultProcess) { "RevenueCat should only be initialized in the default process" }
        val projectApiKey = requireNotNull(RevenueCatPublicKeys.get(buildConfig.packageName)) {
            "RevenueCat public key not found for package name: ${buildConfig.packageName}"
        }

        Purchases.logLevel = LogLevel.VERBOSE
        Purchases.configure(
            configuration = PurchasesConfiguration.Builder(context, projectApiKey)
//                .appUserID()
                .build(),
        )
        Log.i("Initializing RevenueCat - complete")
    }
}