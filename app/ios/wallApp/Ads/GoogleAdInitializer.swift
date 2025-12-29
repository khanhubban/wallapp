//
//  GoogleAdInitializer.swift
//  WallApp
//

import WallApp
import AdSupport
import GoogleMobileAds

class GoogleAdInitializer {
    
    private lazy var privacyMessagingManager = PrivacyMessagingManagerDelegate.shared
    private var interopModules: InteropModulesIos { InteropModulesIos.shared }
    private var buildConfig: BuildConfig { interopModules.buildConfig }
    
    func initialize() {
        guard let uiController = InteropModulesIos.shared.uiControllerManager.currentUiController else {
            Log.e("[Ads] [GoogleAdInitializer] initialize(): uiController is nil")
            return
        }

        Log.d("[Ads] [GoogleAdInitializer] initialize()")
        privacyMessagingManager.consentGather(uiController: uiController) { error in
            if let error {
                Log.e("[Ads] [GoogleAdInitializer] initialize(): \(error)")
            } else {
                if self.privacyMessagingManager.canRequestAds {
                    Log.d("[Ads] [GoogleAdInitializer] initialize(): Requesting permission")
                    self.initializeGADMobileAdsAndSetTestDevices()
                }
            }
        }
    }
    

    
    private func initializeGADMobileAdsAndSetTestDevices() {
        if !privacyMessagingManager.canRequestAds { return }
        
        GADMobileAds.sharedInstance().start(completionHandler: { _ in
            Log.i("[AppDelegate] [Lifecycle] [Ads] GADMobileAds instance started")
            if self.buildConfig.debug {
                Log.d("[Ads] set test devices skipped, isDebug: \(self.buildConfig.debug)")
                return
            }
            Task {
                var isDeveloper: Any?
                let totalAttempts = 4
                for _ in 0..<totalAttempts {
                    isDeveloper = self.interopModules.accountDataRepository.hasSpecialCaseIsDeveloper.value
                    Log.d("[Ads] isDeveloper: \(String(describing: isDeveloper))")
                    if isDeveloper != nil {
                        break
                    }
                    try? await Task.sleep(nanoseconds: 250_000_000) // 250ms
                }
                let setTestAdIdInBuild = isDeveloper as? Bool ?? false
                if !setTestAdIdInBuild {
                    Log.d("[Ads] set test devices skipped, setTestAdIdInBuild: \(setTestAdIdInBuild)")
                    return
                }
                
                let zeroUUID = UUID(uuidString: "00000000-0000-0000-0000-000000000000")
                
                // Set the current device as a test device
                let adId = ASIdentifierManager.shared().advertisingIdentifier
                var adIdString: String? = adId.uuidString.md5
                
                if adId == zeroUUID {
                    adIdString = nil
                }
                Log.d("[Ads] adIdString: \(adIdString ?? "nil")")
                
                let deviceIdString = await UIDevice.current.identifierForVendor?.uuidString.md5 ?? ""
                Log.d("[Ads] deviceIdString: \(deviceIdString)")
                
                let testDeviceIdentifier = adIdString ?? deviceIdString
                
                Log.d("[Ads] Current device added as test device: \(testDeviceIdentifier)")
                GADMobileAds.sharedInstance().requestConfiguration.testDeviceIdentifiers = [ testDeviceIdentifier ]
            }
        })
    }
}
