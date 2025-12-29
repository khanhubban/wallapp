package wallapp.license.cache

import wallapp.prefs.PreferenceDefaults


fun LicenseCache.resetAll(defaults: PreferenceDefaults) {
    this.cachedLicenseState.value = defaults.cachedLicenseState.default()
    this.licenseUserId.update(defaults.licenseUserId.default())
    this.rewardUnlockedWallpapers.value = defaults.rewardUnlockedWallpapers.default()
}