package wallapp.di.module

import org.koin.core.module.Module
import org.koin.dsl.module
import wallapp.ad.reward.RewardAdConfig
import wallapp.ad.reward.RewardAdPlaybackManagerConfigDefault
import wallapp.ad.reward.RewardAdWatchManager
import wallapp.ad.reward.RewardAdWatchManagerDefault
import wallapp.ad.reward.internal.RewardAdInternalNavigatorCompat
import wallapp.ad.reward.internal.RewardAdInternalPlaybackManagerDefault
import wallapp.ads.AdSourceInitializer
import wallapp.ads.AdSourceInitializerNoOp
import wallapp.ads.AdUnitIds
import wallapp.ads.initializerstate.AdInitializerState
import wallapp.ads.initializerstate.AdInitializerStateDefault
import wallapp.ads.initializerstate.AdInitializerStateForceAds
import wallapp.ads.initializerstate.AdInitializerStateNoAds
import wallapp.ads.inline.InlineAdArbitrator
import wallapp.ads.inline.InlineAdArbitratorDefault
import wallapp.ads.inline.InlineAdCreatorNoOp
import wallapp.ads.inline.InlineAdInitDescriptors
import wallapp.ads.inline.InlineAdManagerNoOp
import wallapp.ads.inline.support.FullScreenAdShowListener
import wallapp.ads.inline.support.FullScreenAdShowListenerDefault
import wallapp.ads.inline.support.FullScreenAdShowListenerNoOp
import wallapp.ads.inline.support.GlobalFullScreenAdShowCallbacks
import wallapp.ads.inline.support.GlobalFullScreenAdShowCallbacksDefault
import wallapp.ads.inline.support.GlobalFullScreenAdShowCallbacksNoOp
import wallapp.ads.inline.support.InlineAdConfigFactories
import wallapp.ads.inline.support.InlineAdConfigFactory
import wallapp.ads.inline.support.InlineAdConfigFactoryMediator
import wallapp.ads.inline.support.InlineAdItemFactories
import wallapp.ads.inline.support.InlineAdItemFactory
import wallapp.ads.inline.support.InlineAdItemFactoryMediator
import wallapp.ads.inline.types.InlineAdInitDescriptorsDefault
import wallapp.ads.reward.RewardAdHandleManager
import wallapp.ads.reward.RewardAdManager
import wallapp.ads.reward.RewardAdManagerDefault
import wallapp.ads.reward.RewardAdPlaybackManager
import wallapp.ads.reward.RewardAdPlaybackManagerConfig
import wallapp.ads.reward.internal.RewardAdInternalNavigator
import wallapp.ads.reward.internal.RewardAdInternalPlaybackManager
import wallapp.ads.reward.internal.RewardAdInternalRepository
import wallapp.ads.reward.internal.RewardAdInternalRepositoryConfig
import wallapp.ads.reward.internal.RewardAdInternalRepositoryConfigDefault
import wallapp.ads.reward.internal.RewardAdInternalRepositoryPreset
import wallapp.di.Factory
import wallapp.di.Lazy
import wallapp.di.NamedScope
import wallapp.di.getLazy

@Suppress("RemoveExplicitTypeArguments")
val AdsModule: Module = module {
    single<AdInitializerState> { AdInitializerStateForceAds() }
    single<AdInitializerStateDefault> { AdInitializerStateDefault(get(), get(), get()) }
    single<AdInitializerStateNoAds> { AdInitializerStateNoAds() }
    single<AdSourceInitializer> { Factory.adSourceInitializer(this) }
    single<AdSourceInitializerNoOp> { AdSourceInitializerNoOp() }
    single<AdUnitIds> { Factory.adUnitIds(this) }
    single<FullScreenAdShowListener> { FullScreenAdShowListenerNoOp() }
    single<FullScreenAdShowListenerDefault> { FullScreenAdShowListenerDefault() }
    single<GlobalFullScreenAdShowCallbacks> { Factory.globalFullScreenAdShowCallbacks(this) }
    single<GlobalFullScreenAdShowCallbacksDefault> { GlobalFullScreenAdShowCallbacksDefault(get()) }
    single<GlobalFullScreenAdShowCallbacksNoOp> { GlobalFullScreenAdShowCallbacksNoOp() }
    single<InlineAdArbitrator> { InlineAdArbitratorDefault }
    single<InlineAdConfigFactories> { Factory.inlineAdConfigFactories(this) }
    single<InlineAdConfigFactory> { get(clazz = InlineAdConfigFactoryMediator::class) }
    single<InlineAdConfigFactoryMediator> { InlineAdConfigFactoryMediator(get()) }
    single<InlineAdCreatorNoOp> { InlineAdCreatorNoOp }
    single<InlineAdInitDescriptors> { InlineAdInitDescriptorsDefault }
    single<InlineAdItemFactories> { Factory.inlineAdItemFactories(this) }
    single<InlineAdItemFactory> { InlineAdItemFactoryMediator(get()) }
    single<InlineAdManagerNoOp> { InlineAdManagerNoOp() }
    single<Lazy<AdSourceInitializer>>(NamedScope.LazyAdSourceInitializer) { getLazy() }
    single<Lazy<AdSourceInitializerNoOp>>(NamedScope.LazyAdSourceInitializerNoOp) { getLazy() }
    single<Lazy<GlobalFullScreenAdShowCallbacks>>(NamedScope.LazyGlobalFullScreenAdShowCallbacks) { getLazy() }
    single<Lazy<GlobalFullScreenAdShowCallbacksDefault>>(NamedScope.LazyGlobalFullScreenAdShowCallbacksDefault) { getLazy() }
    single<Lazy<GlobalFullScreenAdShowCallbacksNoOp>>(NamedScope.LazyGlobalFullScreenAdShowCallbacksNoOp) { getLazy() }
    single<Lazy<InlineAdConfigFactory>>(NamedScope.LazyInlineAdConfigFactory) { getLazy() }
    single<Lazy<RewardAdHandleManager>>(NamedScope.LazyRewardAdHandleManager) { getLazy() }
    single<List<String>>(NamedScope.AdMobTestDeviceIds) { Factory.adMobTestDeviceIds(this) }
    single<RewardAdConfig> { Factory.rewardAdConfig(this) }
    single<RewardAdHandleManager> { RewardAdHandleManager(get()) }
    single<RewardAdInternalNavigator> { Factory.rewardAdInternalNavigator(this) }
    single<RewardAdInternalNavigatorCompat> { RewardAdInternalNavigatorCompat(get()) }
    single<RewardAdInternalPlaybackManager> { Factory.rewardAdInternalPlaybackManager(this) }
    single<RewardAdInternalPlaybackManagerDefault> { RewardAdInternalPlaybackManagerDefault(get(), get()) }
    single<RewardAdInternalRepository> { get<RewardAdInternalRepositoryPreset>() }
    single<RewardAdInternalRepositoryConfig> { get<RewardAdInternalRepositoryConfigDefault>() }
    single<RewardAdInternalRepositoryConfigDefault> { RewardAdInternalRepositoryConfigDefault(get()) }
    single<RewardAdInternalRepositoryPreset> { RewardAdInternalRepositoryPreset(get(), get()) }
    single<RewardAdManager> { get<RewardAdManagerDefault>() }
    single<RewardAdManagerDefault> { RewardAdManagerDefault(get(), get(NamedScope.CoroutineScopeMain)) }
    single<RewardAdPlaybackManager> { Factory.rewardAdPlaybackManager(this) }
    single<RewardAdPlaybackManagerConfig> { get<RewardAdPlaybackManagerConfigDefault>() }
    single<RewardAdPlaybackManagerConfigDefault> { RewardAdPlaybackManagerConfigDefault(get(), get(NamedScope.CoroutineScopeIo)) }
    single<RewardAdWatchManager> { RewardAdWatchManagerDefault(get(), get(), get(NamedScope.CoroutineScopeMain)) }
}
