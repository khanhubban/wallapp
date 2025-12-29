package wallapp.device

import kotlinx.coroutines.flow.Flow
import wallapp.country.Country

interface DeviceCountryIp {

    val countryHighConfidence: Flow<Country?>

    val countryInferred: Flow<Country?>

    // Property representing the arbitrated country, returns null by default
    val arbitratedCountry: Country?
        get() = null // Since Flow doesn't have an immediate value, you may need to adjust how you access this property.
}
