//
//  RevenueCatUserManagerIos.swift
//  WallApp
//

import WallApp
import Foundation
import RevenueCat


final class RevenueCatUserManagerIos : RevenueCatUserManager {
    
    
    private var currentAppUserIdListeners = [CurrentAppUserIdListener]()
    
    private var appUserId: String? {
        didSet {
            currentAppUserIdListeners.forEach { $0.onCurrentAppUserIdChanged(appUserId: appUserId ?? "") }
        }
    }
    
    func configure(projectApiKey: String, enableSuperwall: Bool, userId: String?) {
        // MARK: Configure RevenueCat
        Purchases.logLevel = .debug
        var purchasesInstance: Purchases
        var configurationBuilder = Configuration.Builder(withAPIKey: projectApiKey)
            .with(entitlementVerificationMode: .informational)
        if let userId {
            configurationBuilder = configurationBuilder.with(appUserID: userId)
        }
        purchasesInstance = Purchases.configure(with: configurationBuilder.build())
        self.appUserId = purchasesInstance.appUserID
    }
    
    func login(userId: String, completion: @escaping (RevenueCatErrors?) -> Void) {
        Purchases.shared.logIn(userId) { customerInfo, newUser, error in
            Log.d("[Billing] [RevenueCatUserManagerIos] login: \(userId) isNewUser: \(newUser)")
            if customerInfo != nil {
                self.appUserId = userId
            } else if let error = error as? RevenueCat.ErrorCode {
                completion(
                    RevenueCatErrors(code: Int32(error.errorCode), description: error.description)
                )
            }
        }
    }
    
    func logout(completion: @escaping (RevenueCatErrors?) -> Void) {
        Purchases.shared.logOut() { customerInfo, error in
            Log.d("[Billing] [RevenueCatUserManagerIos] logout, customerInfoId: \(String(describing: customerInfo?.id))")
            if let customerInfo {
                self.appUserId = customerInfo.originalAppUserId
            } else if let error = error as? RevenueCat.ErrorCode {
                completion(
                    RevenueCatErrors(code: Int32(error.errorCode), description: error.description)
                )
            }
        }
    }
    
    func addCurrentAppUserIdListener(listener: CurrentAppUserIdListener) {
        currentAppUserIdListeners.append(listener)
        if let appUserId {
            listener.onCurrentAppUserIdChanged(appUserId: appUserId)
        }
    }
    
    func removeCurrentAppUserIdListener(listener: CurrentAppUserIdListener) {
        currentAppUserIdListeners.removeAll { $0 === listener }
    }
    
    func isCurrentUserAnonymous() -> Bool {
        return Purchases.shared.isAnonymous
    }
}
