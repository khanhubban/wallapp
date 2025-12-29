package wallapp.data.following

import wallapp.content.model.Id.ArtistId

data class FollowState(
    val id: ArtistId,
    val isFollowing: Boolean?,
    val isRefreshing: Boolean = false,
)