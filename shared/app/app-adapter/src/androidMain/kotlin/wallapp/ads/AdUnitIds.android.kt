package wallapp.ads

import wallapp.ads.appopen.AppOpenAdUnitId
import wallapp.ads.inline.InlineAdUnitId
import wallapp.ads.interstitial.InterstitialAdUnitId
import wallapp.ads.reward.RewardAdUnitId
import wallapp.ads.reward.RewardInterstitialAdUnitId
import wallapp.buildconfig.BuildConfig

class AdUnitIdsAndroid(
    private val buildConfig: BuildConfig
) : AdUnitIds {

    private val useDebugAdUnitIds: Boolean
        get() = buildConfig.debug

    override val rewardAdUnitId: RewardAdUnitId by lazy {
        if (useDebugAdUnitIds) {
            RewardAdUnitId("ca-app-pub-3940256099942544/5224354917")
        } else {
            TODO("Replace with your production Rewarded Ad Unit ID")
        }
    }

    override val rewardInterstitialAdUnitId: RewardInterstitialAdUnitId by lazy {
        RewardInterstitialAdUnitId("")
    }

    override val interstitialAdUnitId: InterstitialAdUnitId by lazy {
        InterstitialAdUnitId("")
    }

    override val appOpenAdUnitId: AppOpenAdUnitId by lazy {
        if (useDebugAdUnitIds) {
            AppOpenAdUnitId("ca-app-pub-3940256099942544~3347511713")
        } else {
            TODO("Replace with your production Rewarded Ad Unit ID")
        }
    }

    override val feedAdUnitId: InlineAdUnitId by lazy {
        if (useDebugAdUnitIds) {
            InlineAdUnitId("ca-app-pub-3940256099942544/2247696110")
        } else {
            TODO("Replace with your production Rewarded Ad Unit ID")
        }
    }
    override val feedVideoAdUnitId: InlineAdUnitId by lazy {
        if (useDebugAdUnitIds) {
            InlineAdUnitId("ca-app-pub-3940256099942544/1044960115")
        } else {
            TODO("Replace with your production Rewarded Ad Unit ID")
        }
    }
}