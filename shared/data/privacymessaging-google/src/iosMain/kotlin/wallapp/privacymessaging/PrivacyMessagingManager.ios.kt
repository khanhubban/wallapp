package wallapp.privacymessaging

import wallapp.system.ui.controller.UiController

class PrivacyMessagingManagerIos(
    private val privacyMessagingManagerDelegateIos: PrivacyMessagingManagerDelegateIos,
) : PrivacyMessagingManager {
    override val canRequestAds: Boolean
        get() = privacyMessagingManagerDelegateIos.canRequestAds

    override fun consentGather(
        uiController: UiController,
        listener: PrivacyMessagingConsentGatheringListener
    ) {
        privacyMessagingManagerDelegateIos.consentGather(uiController) { formError ->
            listener.consentGatheringComplete(formError)
        }
    }

    override val privacyOptionsRequired: Boolean
        get() = privacyMessagingManagerDelegateIos.privacyOptionsRequired

    override fun showPrivacyOptions() {
        privacyMessagingManagerDelegateIos.showPrivacyOptions()
    }
}