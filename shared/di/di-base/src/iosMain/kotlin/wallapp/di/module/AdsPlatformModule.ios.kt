package wallapp.di.module

import org.koin.dsl.module
import wallapp.ads.inline.InlineAdManager
import wallapp.ads.inline.InlineAdManagerIos

@Suppress("RemoveExplicitTypeArguments")
val AdsPlatformModule = module {
    single<InlineAdManager> { InlineAdManagerIos }
}