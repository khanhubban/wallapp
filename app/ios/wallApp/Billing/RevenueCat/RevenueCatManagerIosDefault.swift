//
//  RevenueCatManagerIosDefault.swift
//  WallApp
//

import WallApp
import Foundation
import RevenueCat

final class RevenueCatManagerIosDefault : RevenueCatManagerIos {
    
    var enabled: Bool = true
    
    private var listeners = [RevenueCatBillingPurchasesListener]()
    
    private var currentCustomerInfo: CustomerInfo? {
        Purchases.shared.cachedCustomerInfo
    }
    
    private var interopModules: InteropModulesIos {
        InteropModulesIos.shared
    }
    private var accountManager: AccountManager {
        interopModules.accountManager
    }
    
    init() {
        Task {
            for try await customerInfo in Purchases.shared.customerInfoStream {
                let billingPurchases = mapEntitlementInfosToBillingSkus(customerInfo)
                listeners.forEach { $0.onBillingPurchasesUpdated(purchases: billingPurchases) }
            }
        }
    }
    
    var appUserId: String {
        Purchases.shared.appUserID
    }
    
    func addUpdatedBillingPurchasesListener(listener: RevenueCatBillingPurchasesListener) {
        listeners.append(listener)
        if let currentCustomerInfo {
            let billingPurchases = mapEntitlementInfosToBillingSkus(currentCustomerInfo)
            listener.onBillingPurchasesUpdated(purchases: billingPurchases)
        } else {
            listener.onBillingPurchasesUpdated(purchases: nil)
        }
    }
    
    func removeUpdatedBillingPurchasesListener(listener: RevenueCatBillingPurchasesListener) {
        listeners.removeAll { $0 === listener }
    }

    func getProductBillingSkus(productIds: [String], onResult: @escaping (RevenueCatBillingSkus) -> Void) {
        Purchases.shared.getProducts(productIds) { products in
            let billingSkus = products.map { product -> BillingSku in
                return RevenueCatMapper.mapStoreProductToBillingSku(storeProduct: product)
            }
//            Log.d("[Billing] All mapped BillingSkus: \(billingSkus.map { $0.productId })")
            onResult(RevenueCatBillingSkus.Success(allBillingSkus: billingSkus))
        }
    }

    func getOfferingsBillingSkus(onResult: @escaping (RevenueCatBillingSkus) -> Void) {
        Purchases.shared.getOfferings { (offerings, error) in
            if let offerings {
                onResult(RevenueCatMapper.mapToBillingSkus(offerings: offerings))
            } else if let error = error as? RevenueCat.ErrorCode {
                switch error {
                default: break
                }
                onResult(RevenueCatBillingSkus.RevenueCatError(error: error.description))
            } else {
                onResult(RevenueCatBillingSkus.UnknownError())
            }
        }
    }
    
    func getBillingPurchases(onResult: @escaping (RevenueCatBillingPurchases) -> Void) {
        Purchases.shared.getCustomerInfo { (customerInfo, error) in
            if let customerInfo {
                let billingPurchases = RevenueCatMapper.mapEntitlementInfosToBillingSkus(entitlementInfos: customerInfo.entitlements)
                onResult(RevenueCatBillingPurchases.Success(billingPurchases: billingPurchases))
            } else if let error = error as? RevenueCat.ErrorCode {
                switch error {
                default: break
                }
                onResult(RevenueCatBillingPurchases.RevenueCatError(code: Int32(error.errorCode), errorMessage: error.description))
            } else {
                onResult(RevenueCatBillingPurchases.UnknownError())
            }
        }
    }
    
    func purchase(billingSku: BillingSku, onResult: @escaping (RevenueCatPurchase) -> Void) {
        guard let storeProduct = billingSku.nativeSku as? StoreProduct else {
            onResult(RevenueCatPurchase.UnknownError())
            return
        }
        Purchases.shared.purchase(product: storeProduct) { transaction, info, error, userCancelled in
            if let error = error as? RevenueCat.ErrorCode {
                var handled = false
                switch error {
                case .paymentPendingError: 
                    if let transaction, let info {
                        onResult(RevenueCatPurchase.Success(purchases: RevenueCatMapper.mapStoreTransactionToBillingPurchases(storeTransaction: transaction, customerInfo: info, paymentPending: true)))
                        handled = true
                    }
                default: break
                }
                if !handled {
                    onResult(RevenueCatPurchase.RevenueCatError(message: error.description, userCancelled: userCancelled))
                }
            } else if let transaction, let info {
                
                onResult(RevenueCatPurchase.Success(purchases: RevenueCatMapper.mapStoreTransactionToBillingPurchases(storeTransaction: transaction, customerInfo: info, paymentPending: false)))
            } else {
                onResult(RevenueCatPurchase.UnknownError())
            }
        }
    }
    
    func restorePurchases(onResult: @escaping (RevenueCatBillingPurchases) -> Void) {
        Purchases.shared.restorePurchases { customerInfo, error in
            if let customerInfo {
                let billingPurchases = RevenueCatMapper.mapEntitlementInfosToBillingSkus(entitlementInfos: customerInfo.entitlements)
                onResult(RevenueCatBillingPurchases.Success(billingPurchases: billingPurchases))
            } else if let error = error as? RevenueCat.ErrorCode {
                if (error == .missingReceiptFileError) { // this can happen in sandbox env - https://www.revenuecat.com/docs/test-and-launch/errors#-error_fetching_receipts
                    onResult(RevenueCatBillingPurchases.Success(billingPurchases: nil))
                } else {
                    onResult(RevenueCatBillingPurchases.RevenueCatError(code: Int32(error.errorCode), errorMessage: error.description))
                }
            } else {
                onResult(RevenueCatBillingPurchases.UnknownError())
            }
        }
    }
    
    func syncPurchases(onResult: @escaping (RevenueCatBillingPurchases) -> Void) {
        Purchases.shared.syncPurchases { customerInfo, error in
            if let customerInfo {
                let billingPurchases = RevenueCatMapper.mapEntitlementInfosToBillingSkus(entitlementInfos: customerInfo.entitlements)
                onResult(RevenueCatBillingPurchases.Success(billingPurchases: billingPurchases))
            } else if let error = error as? RevenueCat.ErrorCode {
                if (error == .missingReceiptFileError) { // this can happen in sandbox env - https://www.revenuecat.com/docs/test-and-launch/errors#-error_fetching_receipts
                    onResult(RevenueCatBillingPurchases.Success(billingPurchases: nil))
                } else {
                    onResult(RevenueCatBillingPurchases.RevenueCatError(code: Int32(error.errorCode), errorMessage: error.description))
                }
            } else {
                onResult(RevenueCatBillingPurchases.UnknownError())
            }
        }
    }
    
    private func mapEntitlementInfosToBillingSkus(_ customerInfo: CustomerInfo?) -> BillingPurchases? {
        Log.d("[Billing] [RevenueCatManagerIos] currentBillingPurchases, userId: \(String(describing: customerInfo?.originalAppUserId)), appUserId: \(appUserId)")
        
        guard let customerInfo else {
            return nil
        }
        
        let currentSignedInUserId = accountManager.signedInUserId.value
        if currentSignedInUserId != nil && currentSignedInUserId != appUserId {
            Log.w("[Billing] [RevenueCatManagerIos] customerInfo does not belong to the current signed in user, currentSignedInUserId: \(String(describing: currentSignedInUserId)), appUserId: \(appUserId)")
            return nil
        }
        
        // This needs to happen here instead of Kotlin because the Kotlin code is not aware of the CustomerInfo object
        interopModules.billingStateManager.onCustomerInfoUpdated()
        
        return RevenueCatMapper.mapEntitlementInfosToBillingSkus(entitlementInfos: customerInfo.entitlements)
    }
}
