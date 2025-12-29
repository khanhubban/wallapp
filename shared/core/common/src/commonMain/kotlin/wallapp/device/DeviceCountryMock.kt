package wallapp.device

import wallapp.language.Iso

class DeviceCountryMock(var bestCountryIso: Iso? = null): DeviceCountry {

    override fun unreliablyResolveBestCountryIso(): Iso? {
        return bestCountryIso
    }
}