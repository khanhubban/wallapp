package wallapp.data.following

import wallapp.content.model.Id

fun List<FollowState>?.findById(id: Id.ArtistId): FollowState? {
    return this?.find { it.id == id }
}

fun List<FollowState>?.contains(id: Id.ArtistId): Boolean {
    return findById(id)?.isFollowing ?: false
}
