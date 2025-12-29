package wallapp.di.module

import org.koin.dsl.module
import wallapp.billing.BillingManager
import wallapp.billing.data.BillingManagerDebugDataDebug
import wallapp.billing.debug.BillingDebugSkuDefinitionsDebug
import wallapp.billing.debug.BillingManagerDebug
import wallapp.billing.debug.data.BillingManagerDebugData
import wallapp.billing.debug.definitions.BillingDebugSkuDefinitions
import wallapp.content.state.debug.DebugManager
import wallapp.content.state.debug.DebugManagerDebug
import wallapp.di.NamedScope

@Suppress("RemoveExplicitTypeArguments")
val BuildConfigModule = module {
    single<BillingDebugSkuDefinitions> { BillingDebugSkuDefinitionsDebug(get()) }
    single<BillingManager>(NamedScope.BillingManagerFallback) { get<BillingManagerDebug>() }
    single<BillingManagerDebug> { BillingManagerDebug(get(), get(), get(), get(), get(NamedScope.CoroutineScopeMain)) }
    single<BillingManagerDebugData> { BillingManagerDebugDataDebug(get()) }
    single<DebugManager> { DebugManagerDebug(get(), get(), get(), licenseSettings = get(NamedScope.LicenseSettings), userSettings = get(NamedScope.UserSettings), deviceSettings = get(NamedScope.DeviceSettings), get(), get(), get(), get(), get(), get(), get(NamedScope.CoroutineScopeMain)) }
}
