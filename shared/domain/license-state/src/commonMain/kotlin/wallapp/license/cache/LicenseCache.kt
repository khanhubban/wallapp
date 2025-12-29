package wallapp.license.cache

import kotlinx.coroutines.flow.MutableStateFlow
import wallapp.preference.MutableObservableValue

interface LicenseCache {

    val cachedLicenseState: MutableStateFlow<String>

    val licenseUserId: MutableObservableValue<String>

    val rewardUnlockedWallpapers: MutableStateFlow<String>

    fun resetAllToDefault()
}