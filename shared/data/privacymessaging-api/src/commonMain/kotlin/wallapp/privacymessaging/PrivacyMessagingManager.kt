package wallapp.privacymessaging

import wallapp.system.ui.controller.UiController

interface PrivacyMessagingManager {

    // Will be true after the user has given ad consent
    val canRequestAds: Boolean

    fun consentGather(
        uiController: UiController,
        listener: PrivacyMessagingConsentGatheringListener,
    )

    // Does this region require privacy options to be shown?
    val privacyOptionsRequired: Boolean

    fun showPrivacyOptions()
}