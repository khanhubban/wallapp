//
//  PrivacyMessagingManagerIos.swift
//  WallApp
//

import WallApp
import GoogleMobileAds
import UserMessagingPlatform

class PrivacyMessagingManagerDelegate : PrivacyMessagingManagerDelegateIos {
    
    static let shared = PrivacyMessagingManagerDelegate()
    private init() {}
    
    private var uiControllerManager: UiControllerManager {
        InteropModulesIos.shared.uiControllerManager
    }
    
    private var consentInformation: UMPConsentInformation {
        UMPConsentInformation.sharedInstance
    }
    
    private var interopModules: InteropModulesIos {
        InteropModulesIos.shared
    }
    
    func consentGather(uiController: any UiController, completionHandler: @escaping (PrivacyMessagingFormError?) -> Void) {
        let requestParameters: UMPRequestParameters? = UMPRequestParameters()
//        let debugSettings = UMPDebugSettings()
//        debugSettings.geography = .EEA
//        requestParameters?.debugSettings = debugSettings
        consentInformation.requestConsentInfoUpdate(with: requestParameters) { [weak self] requestConsentError in
            guard let self else { return }
            
            let consentStatus = self.consentInformation.consentStatus
            Log.d("[Ads] consentGather(): requestConsentError: \(requestConsentError), consentStatus: \(consentStatus), privacyOptionsRequirementStatus: \(self.consentInformation.privacyOptionsRequirementStatus)")
            
            if let requestConsentError {
                completionHandler(self.privacyMessagingFormError(for: requestConsentError))
                return
            }
            
            if consentStatus == .required && interopModules.remoteConfigData.useInternalAdsForGDPR.value == true {
                interopModules.rewardAdPlaybackManager.forceUseInternalAds()
                completionHandler(PrivacyMessagingFormError(
                    errorCode: PrivacyMessagingFormErrorCode.Unknown(errorCode: 0),
                    errorMessage: "Consent required",
                    nativeObject: nil
                ))
                return
            }
            
            UMPConsentForm.loadAndPresentIfRequired(from: uiController.uiViewController_) { [weak self] error in
                guard let self else { return }
                
                if let error {
                    completionHandler(self.privacyMessagingFormError(for: error))
                    return
                }
                
                completionHandler(nil)
            }
        }
    }
    
    func showPrivacyOptions() {
        guard let uiController = uiControllerManager.currentUiViewController else {
            Log.e("[PrivacyMessagingManagerDelegate] showPrivacyOptions(): uiController is nil")
            return
        }
        UMPConsentForm.presentPrivacyOptionsForm(from: uiController) { presentError in
            if let presentError {
                Log.e("[PrivacyMessagingManagerDelegate] showPrivacyOptions(): \(presentError)")
            } else {
                Log.i("[PrivacyMessagingManagerDelegate] showPrivacyOptions(): Success")
            }
        }
    }
    
    var canRequestAds: Bool {
        consentInformation.canRequestAds
    }
    
    var privacyOptionsRequired: Bool {
        consentInformation.privacyOptionsRequirementStatus == .required
        && interopModules.remoteConfigData.useInternalAdsForGDPR.value != true
    }
    
    private func privacyMessagingFormError(for error: Error) -> PrivacyMessagingFormError {
        PrivacyMessagingFormError(
            errorCode: PrivacyMessagingFormErrorCode.InternalError(),
            errorMessage: error.localizedDescription,
            nativeObject: error
        )
    }
}
