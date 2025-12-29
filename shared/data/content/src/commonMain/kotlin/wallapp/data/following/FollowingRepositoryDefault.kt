package wallapp.data.following

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import wallapp.account.data.AccountDataRepository
import wallapp.content.model.Id.ArtistId

/**
 * Piggybacks on the [AccountDataRepository] to store the following state.
 */
class FollowingRepositoryDefault(
    private val accountDataRepository: AccountDataRepository,
    private val coroutineScopeMain: CoroutineScope,
    private val coroutineScopeIo: CoroutineScope,
) : FollowingRepository {

    private fun List<ArtistId>.createFollowStates(): List<FollowState>? {
        return map { FollowState(it, isFollowing = true) }
            .ifEmpty { null }
    }

    override val followStates: StateFlow<List<FollowState>?> =
        accountDataRepository.followingIds.map { favorites ->
            favorites
                ?.filterIsInstance<ArtistId>()
                ?.createFollowStates()
        }.stateIn(
            scope = coroutineScopeMain,
            started = SharingStarted.WhileSubscribed(),
            initialValue = null
        )

    override fun getFollowState(id: ArtistId): Flow<FollowState> = flow {
        suspend fun emit(isFollowing: Boolean?) {
            emit(
                FollowState(
                    id = id,
                    isFollowing = isFollowing,
                    isRefreshing = false,
                )
            )
        }

        emit(isFollowing(id))

        followStates.collect { followStates ->
            emit(isFollowing = followStates?.contains(id))
        }
    }

    override fun setFollowing(id: ArtistId, following: Boolean) {
        coroutineScopeIo.launch {
            setFollowingSuspend(id, following)
        }
    }

    suspend fun setFollowingSuspend(id: ArtistId, following: Boolean) {
        accountDataRepository.setIsFollowing(id, following)
    }

    override suspend fun deleteAll() {
        followStates.value?.forEach { followState ->
            setFollowingSuspend(followState.id, false)
        }
    }

    override fun isFollowing(id: ArtistId): Boolean {
        return followStates.value?.contains(id) ?: false
    }
}