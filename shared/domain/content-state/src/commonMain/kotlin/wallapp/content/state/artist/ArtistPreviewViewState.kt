package wallapp.content.state.artist

import androidx.compose.runtime.Immutable
import wallapp.content.state.follow.FollowIndicatorViewState
import wallapp.content.state.profile.ProfileImageViewState
import wallapp.image.Image
import wallapp.pixel.image.ImageViewState
import wallapp.pixel.menu.MenuItem
import wallapp.pixel.shape.ShapeSpec
import wallapp.pixel.text.Text
import wallapp.pixel.text.Text.Companion.presetText
import wallapp.pixel.view.ViewEventHandler
import wallapp.pixel.view.ViewState

@Immutable
data class ArtistPreviewViewState(
    val name: Text,
    val profileImage: ProfileImageViewState,
    val profileShadowImage: Image?,
    val backgroundImages: List<ImageViewState>,
    val containerShapeSpec: ShapeSpec?,
    val followButton: MenuItem?,
    val eventHandler: ViewEventHandler,
    val followIndicator: FollowIndicatorViewState? = null,
) : ViewState {

    companion object {
        val Preset = ArtistPreviewViewState(
            name = "Preset".presetText,
            profileImage = ProfileImageViewState.Preset,
            profileShadowImage = null,
            backgroundImages = listOf(ImageViewState.Preset),
            containerShapeSpec = ShapeSpec.Preset,
            followButton = null,
            eventHandler = ViewEventHandler.NoOp,
        )
    }

}

