package wallapp.billing

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.pixite.android.billingx.DebugBillingClient
import timber.log.Timber


class BillingDebugReceiver : BroadcastReceiver() {

    companion object {
        internal const val BILLING_DEBUG_RESPONSE_CODE = "response_code_key"
        internal const val BILLING_DEBUG_RESPONSE_BUNDLE = "response_bundle_key"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        Timber.d("[Billing] onReceive()")
        context?.let { NotificationManagerCompat.from(it).cancel(1) }

        val responseCode = intent?.getIntExtra(BILLING_DEBUG_RESPONSE_CODE, BillingResponseCode.ERROR)
            ?: BillingResponseCode.ERROR
        val responseBundle = intent?.getBundleExtra(BILLING_DEBUG_RESPONSE_BUNDLE)

        DebugBillingClient.getInstance()?.onBroadcastReceive(responseCode, responseBundle)
    }
}