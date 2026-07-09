package wallapp.di.module

import kotlinx.coroutines.flow.StateFlow
import org.koin.dsl.module
import wallapp.billing.BillingManager
import wallapp.billing.BillingManagerNoOp
import wallapp.content.state.debug.DebugManager
import wallapp.content.state.debug.DebugManagerNoOp
import wallapp.di.NamedScope
import wallapp.remoteconfig.data.RemoteConfigData
import wallapp.remoteendpoint.ContentDeliveryConfig

@Suppress("RemoveExplicitTypeArguments")
val BuildConfigModule = module {
    single<BillingManager>(NamedScope.BillingManagerFallback) { BillingManagerNoOp() }
    single<ContentDeliveryConfig> { ContentDeliveryConfig(baseUrl = "https://media.stillscenes.app") }
    single<DebugManager> { DebugManagerNoOp }
    single<StateFlow<String>>(NamedScope.CatalogVersion) { get<RemoteConfigData>().catalogVersion }
}
