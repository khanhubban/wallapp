package wallapp.ad.reward

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object RewardAdConfigNoOp : RewardAdConfig {

    override val enabled: StateFlow<Boolean> = MutableStateFlow(false)

    override val enableConsecutivePlays: StateFlow<Boolean> = MutableStateFlow(true)

    override val maxAdsToUnlockASingle: StateFlow<Int> = MutableStateFlow(-1)

    override val unlockWallpaperOnFailure: StateFlow<Boolean> = MutableStateFlow(false)
}