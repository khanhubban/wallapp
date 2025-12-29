package wallapp.ad.reward

import kotlinx.coroutines.flow.Flow
import wallapp.content.model.Id.RemixId

interface RewardAdWatchManager {
    
    fun decrementRemainingAdWatchCountAndCheckIfUnlocked(wallpaperId: RemixId): Boolean

    fun getRemainingAdWatchCount(wallpaperId: RemixId): Flow<Int>

    fun hasPlayedAdForWallpaper(wallpaperId: RemixId): Boolean
}