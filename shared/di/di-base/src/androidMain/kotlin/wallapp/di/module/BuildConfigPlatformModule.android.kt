package wallapp.di.module

import org.koin.core.module.Module
import org.koin.dsl.module
import wallapp.billing.BillingManagerRevenueCatAndroid
import wallapp.di.NamedScope

@Suppress("RemoveExplicitTypeArguments")
val BuildConfigPlatformModule: Module = module {
    single<BillingManagerRevenueCatAndroid> { BillingManagerRevenueCatAndroid(get(), get(), get(), get(), get(), get(), get(), get(), get(NamedScope.CoroutineScopeMain), get(NamedScope.CoroutineScopeIo)) }
}