package wallapp.privacymessaging

fun interface PrivacyMessagingConsentGatheringListener {

    fun consentGatheringComplete(formError: PrivacyMessagingFormError?)
}