package wallapp.di.module

import org.koin.dsl.module
import wallapp.account.data.AccountDataManagerDefault
import wallapp.di.NamedScope

@Suppress("RemoveExplicitTypeArguments")
val AccountPlatformModule = module {
    single<AccountDataManagerDefault> { AccountDataManagerDefault(get(), get(), get(), get(), get(), get(), get(), get(NamedScope.CoroutineScopeIo)) }
}