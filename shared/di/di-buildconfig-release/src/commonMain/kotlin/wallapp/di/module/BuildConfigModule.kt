package wallapp.di.module

import org.koin.dsl.module
import wallapp.billing.BillingManager
import wallapp.billing.BillingManagerNoOp
import wallapp.content.state.debug.DebugManager
import wallapp.content.state.debug.DebugManagerNoOp
import wallapp.di.NamedScope

@Suppress("RemoveExplicitTypeArguments")
val BuildConfigModule = module {
    single<BillingManager>(NamedScope.BillingManagerFallback) { BillingManagerNoOp() }
    single<DebugManager> { DebugManagerNoOp }
}