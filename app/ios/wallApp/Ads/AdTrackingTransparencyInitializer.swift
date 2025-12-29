//
//  AdTrackingInitializer.swift
//  WallApp
//

import Foundation
import AdSupport
import AppTrackingTransparency

class AdTrackingTransparencyInitializer {
    
    static let shared = AdTrackingTransparencyInitializer()
    
    private var isInitialized = false
    
    func initialize() {
        requestTrackingPermission()
    }
    
    func requestTrackingPermission() {
        print("[ATT] requestTrackingAuthorization()")
        if isInitialized {
            return
        }
        ATTrackingManager.requestTrackingAuthorization { status in
            switch status {
            case .authorized:
                // Tracking authorization dialog was shown
                // and we are authorized
                print("[ATT] requestPermission(): Authorized")
            case .denied:
                // Tracking authorization dialog was
                // shown and permission is denied
                print("[ATT] requestPermission(): Denied")
            case .notDetermined:
                // Tracking authorization dialog has not been shown
                print("[ATT] requestPermission(): Not Determined")
            case .restricted:
                print("[ATT] requestPermission(): Restricted")
            @unknown default:
                print("[ATT] requestPermission(): Unknown")
            }

//            print("[ATT] advertisingIdentifier: \(ASIdentifierManager.shared().advertisingIdentifier)")
            
            if status != .notDetermined {
                self.isInitialized = true
                print("[ATT] isInitialized: true")
            }
        }
    }
}
