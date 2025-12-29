package wallapp.ui.content.wallpaper

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.interop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import wallapp.content.state.profile.ProfileImageViewState
import wallapp.content.view.UIKitFactoryIos
import wallapp.pixel.clickable.clickable
import wallapp.pixel.compose.size
import wallapp.pixel.render.Render
import wallapp.pixel.view.ViewEventHandler

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun WallpaperArtistProfileImage(
    render: Render,
    profileImage: ProfileImageViewState,
    onClick: ViewEventHandler,
    outlineColor: Color,
    modifier: Modifier,
) {
    val uiKitFactory = render.uiKitFactory as UIKitFactoryIos
    UIKitView(
        factory = {
            uiKitFactory.createProfileImage(
                profileViewState = profileImage,
            )
        },
        update = {
            uiKitFactory.updateProfileImage(
                profileViewState = profileImage,
            )
        },
        onRelease = {
            uiKitFactory.releaseProfileImage(
                profileViewState = profileImage,
            )
        },
        modifier = modifier
            .clickable(render) {
                onClick.invoke()
            }
            .size(profileImage.imageViewSpec),
        interactive = false
    )
}