package wallapp.inappreview

import kotlinx.coroutines.flow.MutableStateFlow
import wallapp.prefs.DevicePreferenceStorage

class InAppReviewRequestManagerConfigDefault(
    private val devicePreferenceStorage: DevicePreferenceStorage,
): InAppReviewRequestManagerConfig {

    override val lastRequestTime: MutableStateFlow<Long>
        get() = devicePreferenceStorage.lastRequestReviewEpochTime
}