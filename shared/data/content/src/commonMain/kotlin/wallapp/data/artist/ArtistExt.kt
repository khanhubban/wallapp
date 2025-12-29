package wallapp.data.artist

import wallapp.data.following.FollowState
import wallapp.data.following.findById

/**
 * Helper function to update the [ArtistState.followState] for each [ArtistState] in the list.
 */
fun List<ArtistState>.withUpdatedFollowStates(
    followStates: List<FollowState>?,
): List<ArtistState> = map {
    it.copy(followState = followStates?.findById(it.id))
}