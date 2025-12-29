package wallapp.di.module

import org.koin.dsl.module
import wallapp.ads.AdSourceInitializerAdMob
import wallapp.ads.appopen.AppOpenAdManager
import wallapp.ads.appopen.AppOpenAdManagerNoOp
import wallapp.ads.image.AdImageLoader
import wallapp.ads.image.AdImageLoaderCoil
import wallapp.ads.inline.InlineAdCreator
import wallapp.ads.inline.InlineAdCreatorDefault
import wallapp.ads.inline.InlineAdInitDescriptorsAdMob
import wallapp.ads.inline.InlineAdInitFactory
import wallapp.ads.inline.InlineAdInitFactoryAndroid
import wallapp.ads.inline.InlineAdManager
import wallapp.ads.inline.InlineAdManagerDefault
import wallapp.ads.inline.support.InlineAdItemFactoryAdMob
import wallapp.ads.inline.types.InlineAdInitDescriptorsInternal
import wallapp.ads.interstitial.InterstitialAdManager
import wallapp.ads.interstitial.InterstitialAdManagerNoOp
import wallapp.ads.reward.internal.RewardAdInternalNavigatorAndroid
import wallapp.di.FactoryAndroid
import wallapp.di.Lazy
import wallapp.di.NamedScope
import wallapp.di.getLazy

@Suppress("RemoveExplicitTypeArguments")
val AdsPlatformModule = module {
    single<AdImageLoader> { AdImageLoaderCoil() }
    single<AdSourceInitializerAdMob> { AdSourceInitializerAdMob(get(), get(), get(NamedScope.AdMobTestDeviceIds), get(), get(), get(NamedScope.CoroutineScopeIo)) }
    single<AppOpenAdManager> { AppOpenAdManagerNoOp() }
    single<InlineAdCreator> { InlineAdCreatorDefault(get(), get(), get()) }
    single<InlineAdInitFactory> { InlineAdInitFactoryAndroid(get(), get(), get(NamedScope.CoroutineScopeMain), get(NamedScope.CoroutineScopeIo), get()) }
    single<InlineAdInitDescriptorsAdMob> { InlineAdInitDescriptorsAdMob(get()) }
    single<InlineAdInitDescriptorsInternal> { InlineAdInitDescriptorsInternal() }
    single<InlineAdItemFactoryAdMob> { InlineAdItemFactoryAdMob(get(), get(), get(), get(NamedScope.LazyAdSourceInitializer), get(NamedScope.CoroutineScopeMain)) }
    single<InlineAdManager> { FactoryAndroid.inlineAdManager(this) }
    single<InlineAdManagerDefault> { InlineAdManagerDefault(get(), get(), get()) }
    single<InterstitialAdManager> { InterstitialAdManagerNoOp() }
    single<Lazy<AdSourceInitializerAdMob>>(NamedScope.LazyAdSourceInitializerAdMob) { getLazy() }
    single<RewardAdInternalNavigatorAndroid> { RewardAdInternalNavigatorAndroid(get()) }
}