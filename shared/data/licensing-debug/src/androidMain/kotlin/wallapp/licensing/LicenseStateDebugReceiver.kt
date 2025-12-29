package wallapp.licensing

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class LicenseStateDebugReceiver : BroadcastReceiver(), KoinComponent {

    private val licenseRepository: LicenseRepository by inject()

    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let { NotificationManagerCompat.from(it).cancel(1) }

        intent?.let {
            licenseRepository.setLicenseInfoState(LicenseInfo(it.getIntExtra("debug_license_state", LICENSE_STATE_NOT_ALLOWED)))
        }
    }
}