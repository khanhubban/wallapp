//
//  RevenueCatMapper.swift
//  WallApp
//

import WallApp
import Foundation
import RevenueCat


struct VerifiablePurchase {
    let billingPurchase: BillingPurchase
    let isVerified: Bool
    let isActive: Bool
}

final class RevenueCatMapper {
    
    static func mapStoreTransactionToBillingPurchases(
        storeTransaction: StoreTransaction,
        customerInfo: CustomerInfo,
        paymentPending: Bool
    ) -> BillingPurchases {
        let billingPurchase = mapStoreTransactionToBillingPurchase(storeTransaction: storeTransaction, customerInfo: customerInfo, paymentPending: paymentPending)
        if billingPurchase.isPurchasePending {
            return BillingPurchases(verifiedPurchases: nil, unverifiedPurchases: [billingPurchase], inactivePurchases: nil)
        } else {
            return BillingPurchases(verifiedPurchases: [billingPurchase], unverifiedPurchases: nil, inactivePurchases: nil)
        }
    }

    static func mapStoreTransactionToBillingPurchase(
        storeTransaction: StoreTransaction,
        customerInfo: CustomerInfo,
        paymentPending: Bool
    ) -> BillingPurchase {
        let productId = storeTransaction.productIdentifier
        let billingPurchaseId = BillingPurchaseId.BillingPurchaseIdRevenueCat(
            productIdentifier: productId,
            productPlanIdentifier: nil,
            originalPurchaseDateEpoch: storeTransaction.purchaseDate.timeIntervalSince1970.toInt64() * 1000
        )
        let billingProductId = BillingProductId.Companion().from(
            productId: productId,
            entitlementId: nil,
            planId: nil
        )
        let billingSkuSpec = BillingSkuSpec(productId: billingProductId)

        return BillingPurchaseRevenueCat(
            id: billingPurchaseId,
            isPurchasePending: paymentPending,
            skuSpecs: [billingSkuSpec],
            debugString: "StoreTransaction: \(storeTransaction.transactionIdentifier)"
        )
    }

    static func mapEntitlementInfosToBillingSkus(entitlementInfos: EntitlementInfos) -> BillingPurchases? {
        return mapEntitlementInfosToBillingSkus(entitlementInfos: Array(entitlementInfos.all.values))
    }

    static func mapEntitlementInfosToBillingSkus(entitlementInfos: [EntitlementInfo]) -> BillingPurchases? {
        let inactive = "inactive"
        let verified = "verified"
        let unverified = "unverified"
        let purchaseMap = Dictionary(grouping: entitlementInfos) { info in
            if !info.isActive {
                return inactive
            } else if info.verification.isVerified {
                return verified
            } else {
                return unverified
            }
        }.mapValues { infos in
            infos.map { mapEntitlementInfoToBillingSku($0) }
        }
        
        let verifiedPurchases = purchaseMap[verified]?.isEmpty == true ? nil : purchaseMap[verified]
        let unverifiedPurchases = purchaseMap[unverified]?.isEmpty == true ? nil : purchaseMap[unverified]
        let inactivePurchases = purchaseMap[inactive]?.isEmpty == true ? nil : purchaseMap[inactive]
        
        guard verifiedPurchases != nil || unverifiedPurchases != nil || inactivePurchases != nil else {
            return nil
        }
        
        return BillingPurchases(verifiedPurchases: verifiedPurchases, unverifiedPurchases: unverifiedPurchases, inactivePurchases: inactivePurchases)
    }


    static func mapEntitlementInfoToBillingSku(_ entitlementInfo: EntitlementInfo) -> BillingPurchase {
        let purchaseId = mapEntitlementInfoToBillingPurchaseId(entitlementInfo: entitlementInfo)

        return BillingPurchaseRevenueCat(
            id: purchaseId,
            isPurchasePending: false,
            skuSpecs: mapEntitlementInfoToBillingSkuSpecs(entitlementInfo: entitlementInfo),
            debugString: "EntitlementInfo: \(entitlementInfo.identifier)"
        )
    }

    static func mapEntitlementInfoToBillingPurchaseId(entitlementInfo: EntitlementInfo) -> BillingPurchaseId.BillingPurchaseIdRevenueCat {
        return BillingPurchaseId.BillingPurchaseIdRevenueCat(
            productIdentifier: entitlementInfo.productIdentifier,
            productPlanIdentifier: entitlementInfo.productPlanIdentifier,
            originalPurchaseDateEpoch: (entitlementInfo.originalPurchaseDate?.timeIntervalSince1970 ?? 0).toInt64() * 1000
        )
    }

    static func mapEntitlementInfoToBillingSkuSpecs(entitlementInfo: EntitlementInfo) -> [BillingSkuSpec] {
        let billingProductId = BillingProductId.Companion().from(
            productId: entitlementInfo.productIdentifier,
            entitlementId: entitlementInfo.identifier,
            planId: nil
        )
        return [BillingSkuSpec(productId: billingProductId)]
    }

    
    static func mapToBillingSkus(offerings: Offerings) -> RevenueCatBillingSkus.Success {
        return RevenueCatBillingSkus.Success(allBillingSkus: mapOfferingsToBillingSkus(offerings: offerings))
    }
    
    static func mapOfferingsToBillingSkus(offerings: Offerings) -> [BillingSku] {
        return mapOfferingsToBillingSkus(offerings: offerings.all.values.map { $0 })
    }

    static func mapOfferingsToBillingSkus(offerings: [Offering]) -> [BillingSku] {
        return offerings
            .flatMap { mapOfferingToBillingSkus(offering: $0) }
            .distinctUnordered()
    }

    static func mapOfferingToBillingSkus(offering: Offering) -> [BillingSku] {
        return offering.availablePackages.map { mapPackageToBillingSku(revenueCatPackage: $0) }
    }

    static func mapPackageToBillingSku(revenueCatPackage: Package) -> BillingSku {
        return BillingSku(
            productType: mapProductType(productType: revenueCatPackage.storeProduct.productType),
            productId: mapProductId(product: revenueCatPackage.storeProduct),
            title: revenueCatPackage.storeProduct.localizedTitle,
            description: revenueCatPackage.storeProduct.localizedDescription,
            priceLocalized: revenueCatPackage.storeProduct.localizedPriceString,
            priceNumerical: NSDecimalNumber(decimal: revenueCatPackage.storeProduct.price).floatValue,
            priceCurrencyCode: revenueCatPackage.storeProduct.currencyCode ?? "",
            nativeSku: revenueCatPackage.storeProduct
        )
    }

    static func mapStoreProductToBillingSku(storeProduct: StoreProduct) -> BillingSku {
        let billingProductId = BillingProductId.Companion().from(
            productId: storeProduct.productIdentifier,
            entitlementId: nil,
            planId: nil
        )

        let productType = mapProductType(productType: storeProduct.productType)

        return BillingSku(
            productType: productType,
            productId: billingProductId,
            title: storeProduct.localizedTitle,
            description: storeProduct.localizedDescription,
            priceLocalized: storeProduct.localizedPriceString,
            priceNumerical: NSDecimalNumber(decimal: storeProduct.price).floatValue,
            priceCurrencyCode: storeProduct.currencyCode ?? "",
            nativeSku: storeProduct
        )
    }

    static func mapProductId(product: StoreProduct) -> BillingProductId {
        return BillingProductId.Companion().from(
            productId: product.productIdentifier,
            entitlementId: nil,
            planId: nil
        )
    }

    static func mapProductType(productType: StoreProduct.ProductType) -> BillingProductType {
        switch productType {
        case .consumable, .nonConsumable:
            return .inApp
        case .nonRenewableSubscription, .autoRenewableSubscription:
            return .subscription
        }
    }
}
