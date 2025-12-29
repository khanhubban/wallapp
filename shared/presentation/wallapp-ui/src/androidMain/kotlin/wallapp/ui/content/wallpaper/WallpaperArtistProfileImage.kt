package wallapp.ui.content.wallpaper

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import wallapp.content.state.profile.ProfileImageViewState
import wallapp.pixel.render.Render
import wallapp.pixel.view.ViewEventHandler
import wallapp.ui.content.profile.ProfileImage

@Composable
actual fun WallpaperArtistProfileImage(
    render: Render,
    profileImage: ProfileImageViewState,
    onClick: ViewEventHandler,
    outlineColor: Color,
    modifier: Modifier,
) {
    ProfileImage(
        render = render,
        profileImage = profileImage,
        eventHandler = onClick,
        outlineColor = outlineColor,
    )
}