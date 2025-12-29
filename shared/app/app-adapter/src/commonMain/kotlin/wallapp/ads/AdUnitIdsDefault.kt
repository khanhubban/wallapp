package wallapp.ads

import wallapp.ads.appopen.AppOpenAdUnitId
import wallapp.ads.inline.InlineAdUnitId
import wallapp.ads.interstitial.InterstitialAdUnitId
import wallapp.ads.reward.RewardAdUnitId
import wallapp.ads.reward.RewardInterstitialAdUnitId


object AdUnitIdsDefault : AdUnitIds {

    override val rewardAdUnitId: RewardAdUnitId by lazy {
        RewardAdUnitId("ca-app-pub-3940256099942544/5224354917")
    }

    override val rewardInterstitialAdUnitId: RewardInterstitialAdUnitId by lazy {
        RewardInterstitialAdUnitId("")
    }

    override val interstitialAdUnitId: InterstitialAdUnitId by lazy {
        InterstitialAdUnitId("")
    }

    override val appOpenAdUnitId: AppOpenAdUnitId by lazy {
        AppOpenAdUnitId("")
    }

    override val feedAdUnitId: InlineAdUnitId by lazy {
        InlineAdUnitId("ca-app-pub-3940256099942544/2247696110")
    }
    override val feedVideoAdUnitId: InlineAdUnitId by lazy {
        InlineAdUnitId("ca-app-pub-3940256099942544/1044960115")
    }
}