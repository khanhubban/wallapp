package wallapp.device

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import wallapp.country.Country
import wallapp.log.Log
import wallapp.telephony.TelephonyManager

class DeviceCountryIpTelephony(
    private val telephonyManager: TelephonyManager,
) : DeviceCountryIp {

    private val _countryInferred = MutableStateFlow<Country?>(null)
    override val countryInferred: Flow<Country?>
        get() = _countryInferred

    private val _countryHighConfidence = MutableStateFlow<Country?>(null)
    override val countryHighConfidence: Flow<Country?>
        get() = _countryHighConfidence

    private val simCountry: Country?
        get() = telephonyManager.resolveSimCountryIso()
            ?.let { Country.fromIso(it) }
            ?.also { Log.d("simCountry: %s", it) }

    private val networkCountry: Country?
        get() = telephonyManager.resolveNetworkCountryIso()
            ?.let { Country.fromIso(it) }
            ?.also { Log.d("networkCountry: %s", it) }

    val bestDeviceCountry: Country?
        get() = simCountry ?: networkCountry

    init {
        _countryInferred.value = bestDeviceCountry
        _countryHighConfidence.value = null
    }

}
