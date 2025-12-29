package wallapp.content.state.artist

import wallapp.content.model.Id
import wallapp.data.following.FollowState
import wallapp.pixel.view.ViewEvent

data class ToggleArtistFollowEvent(
    val artistId: Id.ArtistId,
    val followState: FollowState?
): ViewEvent
