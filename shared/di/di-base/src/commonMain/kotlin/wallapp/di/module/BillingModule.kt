package wallapp.di.module

import org.koin.dsl.module
import wallapp.billing.BillingManager
import wallapp.billing.BillingManagerErrorListener
import wallapp.billing.BillingManagerErrorListenerDefault
import wallapp.billing.BillingStateManager
import wallapp.billing.BillingStateManagerDefault
import wallapp.billing.InAppPurchaseRepository
import wallapp.billing.InAppPurchaseRepositoryDefault
import wallapp.billing.error.BillingErrorListener
import wallapp.billing.error.BillingErrorListenerNoOp
import wallapp.billing.revenuecat.RevenueCatInitializerDefault
import wallapp.billing.revenuecat.RevenueCatInitializerNoOp
import wallapp.billing.revenuecat.RevenueCatManager
import wallapp.billing.revenuecat.RevenueCatManagerNoOp
import wallapp.billing.revenuecat.RevenueCatUserManager
import wallapp.billing.revenuecat.RevenueCatUserManagerNoOp
import wallapp.di.Factory
import wallapp.di.Lazy
import wallapp.di.NamedScope
import wallapp.di.getLazy
import wallapp.entitlement.EntitlementSkuSpecs
import wallapp.purchase.PurchasableMapper
import wallapp.purchase.PurchasableMapperDefault
import wallapp.purchase.PurchasableRepository
import wallapp.purchase.PurchasableRepositoryDefault
import wallapp.purchase.PurchaseManager
import wallapp.purchase.PurchaseManagerDefault

@Suppress("RemoveExplicitTypeArguments")
val BillingModule = module {
    single<BillingErrorListener> { get(BillingErrorListenerNoOp::class) }
    single<BillingErrorListenerNoOp> { BillingErrorListenerNoOp }
    single<BillingManager> { Factory.billingManager(this) }
    single<BillingManagerErrorListener> { BillingManagerErrorListenerDefault(get(), get(), get(NamedScope.CoroutineScopeMain)) }
    single<BillingStateManager> { BillingStateManagerDefault() }
    single<EntitlementSkuSpecs> { Factory.entitlementSkusSpecs(this) }
    single<InAppPurchaseRepository> { InAppPurchaseRepositoryDefault(get(), get(), get(NamedScope.CoroutineScopeMain), get(NamedScope.CoroutineScopeIo)) }
    single<Lazy<PurchasableRepository>>(NamedScope.LazyPurchasableRepository) { getLazy() }
    single<PurchasableMapper> { get<PurchasableMapperDefault>() }
    single<PurchasableMapperDefault> { PurchasableMapperDefault(get()) }
    single<PurchasableRepository> { Factory.purchasableRepository(this) }
    single<PurchasableRepositoryDefault> { PurchasableRepositoryDefault(get(), get(), get(), get(), get(NamedScope.CoroutineScopeIo)) }
    single<PurchaseManager> { PurchaseManagerDefault(get(), get(), get()) }
    single<RevenueCatInitializerDefault> { RevenueCatInitializerDefault(get(), get(), get(), get(), get(), get(NamedScope.CoroutineScopeMain)) }
    single<RevenueCatInitializerNoOp> { RevenueCatInitializerNoOp }
    single<RevenueCatManager> { Factory.revenueCatManager(this) }
    single<RevenueCatManagerNoOp> { RevenueCatManagerNoOp }
    single<RevenueCatUserManager> { Factory.revenueCatUserManager(this) }
    single<RevenueCatUserManagerNoOp> { RevenueCatUserManagerNoOp }
//    single<BillingVerifierPlayClient> { BillingVerifierPlayClient(get()) }
}
