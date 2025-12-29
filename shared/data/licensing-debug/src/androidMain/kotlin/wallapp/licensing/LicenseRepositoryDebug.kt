package wallapp.licensing

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import wallapp.appstate.AppState
import wallapp.intent.PendingIntentCompat
import wallapp.toolkit.R

class LicenseRepositoryDebug(
    private val context: Context,
    private val appState: AppState,
//    private val notificationIcons: NotificationIcons,
) : LicenseRepository {

    companion object {
        const val TAG = "LicenseRepoDebug: "
    }

    private val _licenseInfo: MutableStateFlow<LicenseInfo> =
        MutableStateFlow(LicenseInfo(-1))       // CheckMe: -1 is not a valid license state
    override val licenseInfo: StateFlow<LicenseInfo> get() = _licenseInfo

    init {
        createNotificationChannel()

        appState.debugLicenseState.subscribe(skipFirst = false) { debugState ->
            _licenseInfo.value = LicenseInfo(debugState)
        }
    }

    override fun checkLicenseState(forceUpdate: Boolean): CheckLicenseStateResult {
//        Log.d("%s checkLicenseState(forceUpdate=%b)", TAG, forceUpdate)

        _licenseInfo.value = LicenseInfo(LICENSE_STATE_CHECKING)
        val licensedPendingIntent: PendingIntent =
            PendingIntentCompat.getBroadcast(context, 0, Intent(context, LicenseStateDebugReceiver::class.java).apply {
                putExtra("debug_license_state", LICENSE_STATE_ALLOWED_PLUS_UNLIMITED)
            }, 0)!!
        val unlicensedPendingIntent: PendingIntent =
            PendingIntentCompat.getBroadcast(context, 1, Intent(context, LicenseStateDebugReceiver::class.java).apply {
                putExtra("debug_license_state", LICENSE_STATE_NOT_ALLOWED)
            }, 0)!!
        val builder = NotificationCompat.Builder(context, "debug_channel_id")
//            .setSmallIcon(notificationIcons.defaultIconResId)
            .setContentTitle("License State Debug")
            .setContentText("Choose license state")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .addAction(R.drawable.ic_launcher_missing_background, "Licensed",
                licensedPendingIntent)
            .addAction(R.drawable.ic_launcher_missing_background, "Unlicensed",
                unlicensedPendingIntent)
            .setAutoCancel(true)
        NotificationManagerCompat.from(context)
            .notify(1, builder.build())
        return CheckLicenseStateResult.CHECKING
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

    override fun setLicenseInfoState(licenseInfo: LicenseInfo) {
        appState.debugLicenseState.update(licenseInfo.licenseState)
        _licenseInfo.value = licenseInfo
    }
}