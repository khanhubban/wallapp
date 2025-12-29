package wallapp.device

import wallapp.language.Iso
import wallapp.string.isEmpty
import wallapp.telephony.TelephonyManager

class DeviceCountryTelephony(
    private val telephonyManager: TelephonyManager,
) : DeviceCountry {

    override fun unreliablyResolveBestCountryIso(): Iso? {
        val simCountryIso = telephonyManager.resolveSimCountryIso()
        if (!isEmpty(simCountryIso)) return simCountryIso

        val networkCountryIso = telephonyManager.resolveNetworkCountryIso()
        if (!isEmpty(networkCountryIso)) return networkCountryIso

        return null
    }
}