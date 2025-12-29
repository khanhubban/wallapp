package wallapp.di.module

import org.koin.core.module.Module
import org.koin.dsl.module
import wallapp.billing.revenuecat.RevenueCatManagerIos
import wallapp.di.FactoryIos
import wallapp.entitlement.EntitlementSkuSpecsAppStore

@Suppress("RemoveExplicitTypeArguments")
val BillingPlatformModule: Module = module {
    single<EntitlementSkuSpecsAppStore> { EntitlementSkuSpecsAppStore(get()) }
    single<RevenueCatManagerIos> { FactoryIos.revenueCatManagerIos() }
}
