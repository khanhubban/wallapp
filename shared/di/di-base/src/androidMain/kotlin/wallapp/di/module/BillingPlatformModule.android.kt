package wallapp.di.module

import org.koin.core.module.Module
import org.koin.dsl.module
import wallapp.billing.revenuecat.RevenueCatInitializerAndroid
import wallapp.billing.revenuecat.RevenueCatManagerAndroid
import wallapp.billing.revenuecat.RevenueCatUserManagerAndroid
import wallapp.billing.verifier.BillingVerifier
import wallapp.billing.verifier.BillingVerifierDebug
import wallapp.di.NamedScope
import wallapp.entitlement.EntitlementSkuSpecsGooglePlay


@Suppress("RemoveExplicitTypeArguments")
val BillingPlatformModule: Module = module {
    single<BillingVerifier> { BillingVerifierDebug() }
    single<EntitlementSkuSpecsGooglePlay> { EntitlementSkuSpecsGooglePlay(get()) }
    single<RevenueCatInitializerAndroid> { RevenueCatInitializerAndroid(get(), get(), get()) }
    single<RevenueCatManagerAndroid> { RevenueCatManagerAndroid(get(NamedScope.CoroutineScopeMain)) }
    single<RevenueCatUserManagerAndroid> { RevenueCatUserManagerAndroid(get(), get()) }
}