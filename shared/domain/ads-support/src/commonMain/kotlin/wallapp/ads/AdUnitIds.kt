package wallapp.ads

import wallapp.ads.appopen.AppOpenAdUnitId
import wallapp.ads.inline.InlineAdUnitId
import wallapp.ads.interstitial.InterstitialAdUnitId
import wallapp.ads.reward.RewardAdUnitId
import wallapp.ads.reward.RewardInterstitialAdUnitId

interface AdUnitIds {

    val rewardAdUnitId: RewardAdUnitId?
        get() = null

    val rewardInterstitialAdUnitId: RewardInterstitialAdUnitId?
        get() = null

    val interstitialAdUnitId: InterstitialAdUnitId?
        get() = null

    val appOpenAdUnitId: AppOpenAdUnitId?
        get() = null

    val feedAdUnitId: InlineAdUnitId?
        get() = null
    val feedVideoAdUnitId: InlineAdUnitId?
        get() = null
}