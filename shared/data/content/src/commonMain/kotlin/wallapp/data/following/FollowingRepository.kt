package wallapp.data.following

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import wallapp.content.model.Id.ArtistId
import wallapp.log.Log

interface FollowingRepository {

    val followStates: StateFlow<List<FollowState>?>

    fun getFollowState(id: ArtistId): Flow<FollowState>

    fun isFollowing(id: ArtistId): Boolean

    fun setFollowing(id: ArtistId, following: Boolean)

    suspend fun deleteAll()
}

fun FollowingRepository.setFollowing(id: ArtistId, followState: FollowState?) {
    val followingFlag = !(followState?.isFollowing ?: false)
    Log.d("Clickable - setFollowing() - followState: $followState, followingFlag: $followingFlag")
    setFollowing(id, followingFlag)
}