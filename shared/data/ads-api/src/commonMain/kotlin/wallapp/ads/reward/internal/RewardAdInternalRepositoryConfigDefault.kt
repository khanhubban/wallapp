package wallapp.ads.reward.internal

import wallapp.buildconfig.BuildConfig

class RewardAdInternalRepositoryConfigDefault(
    val buildConfig: BuildConfig,
) : RewardAdInternalRepositoryConfig {

    override val useAttributionUrls: Boolean
        get() = !buildConfig.debug
}