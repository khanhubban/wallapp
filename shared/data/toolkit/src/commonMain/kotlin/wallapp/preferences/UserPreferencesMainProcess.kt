package wallapp.preferences

import kotlinx.coroutines.flow.MutableStateFlow
import wallapp.appicon.AppIcon
import wallapp.preference.MutableObservableValue
import wallapp.prefs.PreferenceStorage
import wallapp.theme.ThemeType


class UserPreferencesMainProcess(
    private val preferenceStorage: PreferenceStorage,
): UserPreferences {

    override val themeType: MutableStateFlow<ThemeType>
        get() = preferenceStorage.themeType
    override val appIcon: MutableStateFlow<AppIcon>
        get() = preferenceStorage.appIcon
    override val currentRemixId: MutableObservableValue<String>
        get() = preferenceStorage.currentRemixId
    override val currentDesignId: MutableObservableValue<String>
        get() = preferenceStorage.currentDesignId
    override val nextRemixId: MutableObservableValue<String>
        get() = preferenceStorage.nextRemixId
    override val currentPreviewRemixId: MutableObservableValue<String>
        get() = preferenceStorage.currentPreviewRemixId
    override val firebaseCloudMessagingToken: MutableStateFlow<String>
        get() = preferenceStorage.firebaseCloudMessagingToken
    override val favorites: MutableStateFlow<String>
        get() = preferenceStorage.favorites
    override val followings: MutableStateFlow<String>
        get() = preferenceStorage.followings
    override val purchases: MutableStateFlow<String>
        get() = preferenceStorage.purchases
    override val deviceInfo: MutableStateFlow<String>
        get() = preferenceStorage.deviceInfo
    override val nextWallpaperUseFavorites: MutableStateFlow<Boolean>
        get() = preferenceStorage.nextWallpaperUseFavorites
    override val wallpaperDownloadEvents: MutableStateFlow<String>
        get() = preferenceStorage.wallpaperDownloadEvents
    override val acceptedTerms: MutableStateFlow<Boolean>
        get() = preferenceStorage.acceptedTerms
    override val joinNewsletter: MutableStateFlow<Boolean>
        get() = preferenceStorage.joinNewsletter
    override val reportUsageStats: MutableStateFlow<Boolean>
        get() = preferenceStorage.reportUsageStats
    override val receiveNotifications: MutableStateFlow<Boolean>
        get() = preferenceStorage.receiveNotifications
}
