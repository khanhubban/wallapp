package wallapp.privacymessaging

import wallapp.system.ui.controller.UiController

interface PrivacyMessagingManagerDelegateIos {
    val canRequestAds: Boolean
    
    fun consentGather(
        uiController: UiController,
        completionHandler: (PrivacyMessagingFormError?) -> Unit,
    )
    
    val privacyOptionsRequired: Boolean
    
    fun showPrivacyOptions()
}