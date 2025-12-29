package wallapp.privacymessaging

import android.content.Context
import com.google.android.ump.ConsentDebugSettings
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.FormError
import com.google.android.ump.UserMessagingPlatform
import wallapp.system.ui.controller.UiController
import wallapp.system.ui.controller.UiControllerManager
import wallapp.system.ui.controller.activity
import wallapp.system.ui.controller.currentActivity

class PrivacyMessagingManagerGoogleAndroid(
    private val context: Context,
    private val uiControllerManager: UiControllerManager,
) : PrivacyMessagingManager {

    companion object {
        val Log = PrivacyMessagingLogger
    }

    private val consentInformation: ConsentInformation by lazy {
        UserMessagingPlatform.getConsentInformation(context)
    }

    override val canRequestAds: Boolean
        get() = consentInformation.canRequestAds()

    override fun consentGather(
        uiController: UiController,
        listener: PrivacyMessagingConsentGatheringListener,
    ) {
        val activity = uiController.activity

        val debugSettings: ConsentDebugSettings? = null
//        val debugSettings = ConsentDebugSettings.Builder(activity)
//            .setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA)
//            .build()

        val params = ConsentRequestParameters.Builder()
            .setConsentDebugSettings(debugSettings)
            .build()

        // Requesting an update to consent information should be called on every app launch.
        val onSuccess = {
            UserMessagingPlatform.loadAndShowConsentFormIfRequired(activity) { formError: FormError? ->
                Log.i("onSuccess: consent form shown ${formError?.toPrivacyMessagingFormError?.let { "error: $it" }}")
                // Consent has been gathered.
                listener.consentGatheringComplete(formError?.toPrivacyMessagingFormError)
            }
        }

        val onFailure = { requestConsentError: FormError? ->
            Log.i("onFailure: consent form shown ${requestConsentError?.toPrivacyMessagingFormError?.let { "error: $it" }}")
            listener.consentGatheringComplete(requestConsentError?.toPrivacyMessagingFormError)
        }

        consentInformation.requestConsentInfoUpdate(
            activity,
            params,
            onSuccess,
            onFailure,
        )
    }

    override val privacyOptionsRequired: Boolean
        get() = consentInformation.privacyOptionsRequirementStatus ==
                ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED

    override fun showPrivacyOptions() {
        val activity = uiControllerManager.currentActivity ?: return
        UserMessagingPlatform.showPrivacyOptionsForm(activity) { formError: FormError? ->
            Log.d("Privacy options form shown, error: ${formError?.toPrivacyMessagingFormError}")
        }
    }
}
