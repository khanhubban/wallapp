package wallapp.data.following

import androidx.compose.runtime.mutableStateListOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import wallapp.content.model.Id.ArtistId

class FollowingRepositoryNoOp : FollowingRepository {
    override val followStates: MutableStateFlow<List<FollowState>?> = MutableStateFlow(null)

    override fun getFollowState(id: ArtistId): Flow<FollowState> = flowOf(
        FollowState(
            id = id,
            isFollowing = false,
            isRefreshing = true,
        ),
    )

    override fun isFollowing(id: ArtistId): Boolean {
        return followStates.value?.any { it.id == id } ?: false
    }

    override fun setFollowing(id: ArtistId, following: Boolean) {
        val followStates = followStates.value ?: emptyList()
        val newFollowStates = mutableStateListOf<FollowState>()
        newFollowStates.addAll(followStates)
        val index = newFollowStates.indexOfFirst { it.id == id }
        if (index != -1) {
            newFollowStates[index] = newFollowStates[index].copy(isFollowing = following)
        } else {
            newFollowStates.add(FollowState(id, following))
        }
        this.followStates.value = newFollowStates
    }

    override suspend fun deleteAll() {
        followStates.value = emptyList()
    }
}