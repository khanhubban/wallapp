package wallapp.telephony

import wallapp.language.Iso

object TelephonyManagerNoOp : TelephonyManager {
    override fun resolveSimCountryIso(): Iso? = null

    override fun resolveNetworkCountryIso(): Iso? = null
}