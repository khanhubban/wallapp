package wallapp.ad.reward

import kotlinx.coroutines.flow.StateFlow

interface RewardAdConfig {

    val enabled: StateFlow<Boolean>

    val enableConsecutivePlays: StateFlow<Boolean>

    val maxAdsToUnlockASingle: StateFlow<Int>

    val unlockWallpaperOnFailure: StateFlow<Boolean>
}