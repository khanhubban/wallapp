package wallapp.telephony

import android.content.Context
import wallapp.language.Iso

/**
 *
 */
class TelephonyManagerAndroid(context: Context) : TelephonyManager {

    private val telephonyManager by lazy {
        context.getSystemService(Context.TELEPHONY_SERVICE) as android.telephony.TelephonyManager
    }

    override fun resolveSimCountryIso(): Iso? {
        return try {
            telephonyManager.simCountryIso
        } catch (e: Exception) {
            null
        }
    }

    override fun resolveNetworkCountryIso(): Iso? {
        return try {
            telephonyManager.networkCountryIso
        } catch (e: Exception) {
            null
        }
    }
}