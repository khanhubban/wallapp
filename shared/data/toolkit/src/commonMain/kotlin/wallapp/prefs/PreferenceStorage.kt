package wallapp.prefs

import kotlinx.coroutines.flow.MutableStateFlow
import wallapp.appicon.AppIcon
import wallapp.preference.MutableObservableValue
import wallapp.theme.ThemeType


interface PreferenceStorage {
    val themeType: MutableStateFlow<ThemeType>
    val appIcon: MutableStateFlow<AppIcon>

    val currentRemixId: MutableObservableValue<String>
    val currentDesignId: MutableObservableValue<String>
    val nextRemixId: MutableObservableValue<String>
    val currentPreviewRemixId: MutableObservableValue<String>

    val firebaseCloudMessagingToken: MutableStateFlow<String>

    val favorites: MutableStateFlow<String>
    val followings: MutableStateFlow<String>
    val purchases: MutableStateFlow<String>
    val deviceInfo: MutableStateFlow<String>
    val nextWallpaperUseFavorites: MutableStateFlow<Boolean>
    val wallpaperDownloadEvents: MutableStateFlow<String>

    val acceptedTerms: MutableStateFlow<Boolean>
    val joinNewsletter: MutableStateFlow<Boolean>
    val reportUsageStats: MutableStateFlow<Boolean>
    val receiveNotifications: MutableStateFlow<Boolean>
}
