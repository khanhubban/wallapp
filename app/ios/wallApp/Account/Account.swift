//
//  Account.swift
//  WallApp
//

import Foundation

// Useful for debugging to ensure to app cleanly reinstalls.
func resetAppDataUseWithCaution() {
    Log.w("resetAppDataUseWithCaution()!!")
    // reset UserDefaults
    if let bundleID = Bundle.main.bundleIdentifier {
        UserDefaults.standard.removePersistentDomain(forName: bundleID)
    }
    
    // reset Keychain data
    let secItemClasses =  [
        kSecClassGenericPassword,
        kSecClassInternetPassword,
        kSecClassCertificate,
        kSecClassKey,
        kSecClassIdentity
    ]

    for itemClass in secItemClasses {
        let spec: NSDictionary = [kSecClass: itemClass]
        SecItemDelete(spec)
    }
}
