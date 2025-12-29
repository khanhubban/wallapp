package wallapp.privacymessaging

import wallapp.system.ui.controller.UiController

object PrivacyMessagingManagerNoOp : PrivacyMessagingManager {

    override val canRequestAds: Boolean
        get() = true

    override fun consentGather(
        uiController: UiController,
        listener: PrivacyMessagingConsentGatheringListener
    ) {
        TODO("Not yet implemented")
    }

    override val privacyOptionsRequired: Boolean
        get() = false

    override fun showPrivacyOptions() { }
}