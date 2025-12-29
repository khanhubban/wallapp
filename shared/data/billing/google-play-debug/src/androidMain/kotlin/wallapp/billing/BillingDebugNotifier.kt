package wallapp.billing

import wallapp.billing.BillingDebugReceiver.Companion.BILLING_DEBUG_RESPONSE_BUNDLE
import wallapp.billing.BillingDebugReceiver.Companion.BILLING_DEBUG_RESPONSE_CODE
import wallapp.billing.play.debug.R
import wallapp.billing.purchase.BILLING_PURCHASE_STATE_PURCHASED
import wallapp.intent.PendingIntentCompat
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.android.billingclient.api.Purchase
import java.util.UUID


class BillingDebugNotifier(
    private val context: Context,
) {
    private fun buildPurchasedPendingIntent(purchase: Purchase): PendingIntent {
        val updatedPurchase = toPurchase(
            purchase.skus.first(),
            purchase.signature,
            purchase.packageName,
            BILLING_PURCHASE_STATE_PURCHASED,
            purchase.purchaseTime,
            purchase.isAcknowledged,
            purchase.orderId,
            purchase.purchaseToken,
        ).asResultBundle()

        return PendingIntentCompat.getBroadcast(
            context,
            UUID.randomUUID().hashCode(),
            Intent(context, BillingDebugReceiver::class.java).apply {
                putExtra(BILLING_DEBUG_RESPONSE_CODE, BillingResponseCode.OK)
                putExtra(BILLING_DEBUG_RESPONSE_BUNDLE, updatedPurchase)
            },
            PendingIntent.FLAG_UPDATE_CURRENT,
        )!!
    }

    private fun buildCancelPurchasePendingIntent(purchase: Purchase): PendingIntent {
        return PendingIntentCompat.getBroadcast(
            context,
            UUID.randomUUID().hashCode(),
            Intent(context, BillingDebugReceiver::class.java).apply {
                putExtra(BILLING_DEBUG_RESPONSE_CODE, BillingResponseCode.ITEM_NOT_OWNED)
                putExtra(BILLING_DEBUG_RESPONSE_BUNDLE, purchase.asResultBundle())
            },
            PendingIntent.FLAG_UPDATE_CURRENT,
        )!!
    }

    fun showPendingPurchaseNotification(purchase: Purchase) {
        val purchasedPendingIntent = buildPurchasedPendingIntent(purchase)
        val cancelPurchasePendingIntent  = buildCancelPurchasePendingIntent(purchase)
        val builder = NotificationCompat.Builder(context, "debug_channel_id")
            .setSmallIcon(R.drawable.ic_outline_shopping_basket_24)
            .setContentTitle("Billing Purchase State")
            .setContentText("Update purchase state to:")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .addAction(0, "Purchased", purchasedPendingIntent)
            .addAction(0, "Cancel Purchase", cancelPurchasePendingIntent)
            .setAutoCancel(true)
        NotificationManagerCompat
            .from(context)
            .notify(1, builder.build())
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Debug channel"
            val descriptionText = "Debug channel"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("debug_channel_id", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    init {
        createNotificationChannel()
    }

}