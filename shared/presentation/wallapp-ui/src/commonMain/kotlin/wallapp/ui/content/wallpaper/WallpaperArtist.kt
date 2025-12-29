package wallapp.ui.content.wallpaper

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import wallapp.content.state.profile.ProfileImageViewState
import wallapp.content.state.wallpaper.WallpaperArtistViewState
import wallapp.pixel.clickable.clickable
import wallapp.pixel.menu.MenuItem
import wallapp.pixel.render.Render
import wallapp.pixel.text.Text
import wallapp.pixel.view.ViewEventHandler

@Composable
fun WallpaperArtist(
    render: Render,
    viewState: WallpaperArtistViewState,
    modifier: Modifier = Modifier,
) {
    when (viewState) {
        is WallpaperArtistViewState.Loading -> {
            Spacer(
                modifier = modifier
                    .height(viewState.height)
                    .fillMaxWidth(),
            )
        }
        is WallpaperArtistViewState.Data -> {
            WallpaperArtist(
                render,
                viewState,
                profileImageOutlineColor = MaterialTheme.colorScheme.primary,
                modifier = modifier,
            )
        }
    }
}

@Composable
fun WallpaperArtist(
    render: Render,
    viewState: WallpaperArtistViewState.Data,
    profileImageOutlineColor: Color,
    modifier: Modifier = Modifier,
) {
    val paddingSmall = render.defaultViewSpec.paddingSmall
    val profileImage = viewState.profileImage
    val profileImageEventHandler = profileImage.eventHandler!!
    val imageSize = profileImage.imageViewSpec.size
    val actionButtons = viewState.actionButtons
    val name = viewState.name
    val nameEventHandler = viewState.nameEventHandler

    Row(
        modifier = modifier
            .height(imageSize),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        WallpaperArtistProfileImage(
            render,
            profileImage,
            onClick = profileImageEventHandler,
            outlineColor = profileImageOutlineColor,
        )

        Box(
            modifier = Modifier
                .fillMaxHeight()
                .clickable(render) { nameEventHandler.invoke() }
                .padding(horizontal = paddingSmall),
        ) {
            Text(
                text = name,
                modifier = Modifier
                    .align(Alignment.CenterStart),
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        actionButtons?.also {
            Spacer(modifier = Modifier.width(paddingSmall))
            actionButtons.forEach {
                MenuItem(
                    render, menuItem = it,
                )
            }
        }
    }
}

@Composable
expect fun WallpaperArtistProfileImage(
    render: Render,
    profileImage: ProfileImageViewState,
    onClick: ViewEventHandler,
    outlineColor: Color,
    modifier: Modifier = Modifier,
)
