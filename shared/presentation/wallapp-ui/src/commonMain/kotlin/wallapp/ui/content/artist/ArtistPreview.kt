package wallapp.ui.content.artist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import wallapp.content.state.artist.ArtistPreviewViewSpec
import wallapp.content.state.artist.ArtistPreviewViewSpecOnboarding
import wallapp.content.state.artist.ArtistPreviewViewState
import wallapp.content.state.feed.FeedContentPreviewViewSpec
import wallapp.content.state.follow.FollowIndicatorViewState
import wallapp.content.state.profile.ProfileImageViewState
import wallapp.image.Image
import wallapp.pixel.clickable.clickable
import wallapp.pixel.feed.FeedViewViewSpec
import wallapp.pixel.menu.MenuItem
import wallapp.pixel.render.Render
import wallapp.pixel.shape.clip
import wallapp.pixel.text.Text
import wallapp.pixel.view.ViewEventHandler
import wallapp.pixel.view.onClick
import wallapp.ui.content.image.ImageGrid
import wallapp.ui.content.image.mapToImageGridItems
import wallapp.ui.content.profile.ProfileImage

@Composable
fun ArtistPreview(
    render: Render,
    viewState: ArtistPreviewViewState,
    viewSpec: FeedViewViewSpec,
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.Center,
) {
    when (viewSpec) {
        is ArtistPreviewViewSpec -> ArtistPreviewDetailed(
            render,
            viewState,
            viewSpec,
            modifier,
            alignment,
        )

        is FeedContentPreviewViewSpec ->
            throw IllegalArgumentException("Unsupported viewSpec: $viewSpec")

        is ArtistPreviewViewSpecOnboarding -> ArtistPreviewOnboarding(
            render,
            viewState,
            viewSpec,
            modifier,
        )

        else -> error("Unsupported viewSpec: $viewSpec")
    }
}

@Composable
fun ArtistPreviewOnboarding(
    render: Render,
    viewState: ArtistPreviewViewState,
    viewSpec: ArtistPreviewViewSpecOnboarding,
    modifier: Modifier = Modifier,
) {
    val shapeSpec = viewState.containerShapeSpec
    val onClick = viewState.eventHandler.onClick
    val height = viewSpec.height
    val width = viewSpec.width
    val previewImages = if (viewState.backgroundImages.size >= 2) {
        viewState.backgroundImages.subList(0, 2).mapToImageGridItems()
    } else {
        viewState.backgroundImages.mapToImageGridItems()
    }
    val profileImageShape = remember { RoundedCornerShape(50) }
    val profileImage = viewState.profileImage
    val profileImageBorderSize = profileImage.borderSize
    val profileShadowImage = viewState.profileShadowImage
    val profileShadowImageSize = viewSpec.profileShadowImageSize
    val followIndicator = viewState.followIndicator
    val followIndicatorSize = viewSpec.followIconSize
    val outlineColor = Color.White

    Box(
        modifier = modifier
            .height(height)
            .width(width)
            .clip(shapeSpec, render.shapeClipper)
            .clickable(render, onClick = onClick)
    ) {
        if (previewImages != null) {
            ImageGrid(
                render = render,
                items = previewImages,
            )
        }
        if (profileShadowImage != null) {
            Image(
                render,
                profileShadowImage,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .size(profileShadowImageSize)
                    .align(Alignment.Center),
            )
        }
        ProfileImage(
            render = render,
            profileImage = profileImage,
            eventHandler = null,
            shape = profileImageShape,
            outlineColor = outlineColor,
            modifier = Modifier
                .align(Alignment.Center),
        )
        followIndicator?.let {
            FollowIndicator(
                render = render,
                followIndicator = followIndicator,
                followIconSize = followIndicatorSize,
                shape = profileImageShape,
                outlineColor = outlineColor,
                borderSize = profileImageBorderSize,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Composable
private fun FollowIndicator(
    render: Render,
    followIndicator: FollowIndicatorViewState,
    followIconSize: Dp,
    shape: Shape,
    outlineColor: Color,
    borderSize: Dp,
    modifier: Modifier = Modifier,
) {
    val background = followIndicator.background
    val icon = followIndicator.icon
    val animSpec = remember { tween<Float>(100) }

    Box(modifier = modifier) {
        AnimatedVisibility(
            visible = followIndicator.visible,
            enter = fadeIn(animSpec),
            exit = fadeOut(animSpec),
            modifier = Modifier.fillMaxSize(),
        ) {
            Image(
                render,
                background,
                contentScale = ContentScale.FillBounds,
            )
        }
        AnimatedVisibility(
            visible = followIndicator.visible,
            enter = scaleIn(animSpec),
            exit = scaleOut(animSpec),
            modifier = Modifier
                .size(followIconSize)
                .align(Alignment.Center)
        ) {
            Image(
                render,
                icon,
                modifier = Modifier
                    .clip(CircleShape)
                    .border(width = borderSize, shape = shape, color = outlineColor)
            )
        }
    }
}

@Composable
fun ArtistPreviewDetailed(
    render: Render,
    viewState: ArtistPreviewViewState,
    viewSpec: ArtistPreviewViewSpec,
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.Center,
) {
    val shape = CutCornerShape(topStart = 16.dp)
    val onClick = viewState.eventHandler
    val height = viewSpec.height
    val paddingDefault = viewSpec.paddingDefault
    val imagePreviewHeight = viewSpec.imagePreviewHeight
    val backgroundImages = viewState.backgroundImages.mapToImageGridItems()

    Column(
        modifier = modifier
            .height(height)
            .clip(shape)
            .background(MaterialTheme.colorScheme.surface)
            .clickable(render) { onClick.invoke() }
            .padding(start = paddingDefault, end = paddingDefault, bottom = paddingDefault),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ArtistPreviewHeader(render, viewState, viewSpec)
        if (backgroundImages != null) {
            ImageGrid(
                render = render,
                items = backgroundImages,
                modifier = Modifier
                    .height(imagePreviewHeight)
                    .fillMaxWidth(),
                alignment = alignment,
            )
        }
    }
}

@Composable
private fun ArtistPreviewHeader(
    render: Render,
    viewState: ArtistPreviewViewState,
    viewSpec: ArtistPreviewViewSpec,
) {
    val profileImage = viewState.profileImage
    val profileImageOnClick = profileImage.eventHandler
    val title = viewState.name
    val button = viewState.followButton

    val tintColor = MaterialTheme.colorScheme.onSurface
    val profileRowHeight = viewSpec.profileRowHeight
    val profileImageShape = RoundedCornerShape(50)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(profileRowHeight),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ProfileImage(
            render,
            profileImage,
            eventHandler = profileImageOnClick,
            shape = profileImageShape,
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
//                    .height(viewSpec.imageHeight),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = title,
                colorOverride = tintColor,
                modifier = Modifier
                    .fillMaxWidth(),
            )
        }
        if (button != null) {
            MenuItem(
                render,
                menuItem = button,
            )
        }
    }
}

@Composable
fun ArtistPreviewRow(
    render: Render,
    profileImage: ProfileImageViewState,
    title: Text,
    summary: Text?,
    onClick: ViewEventHandler,
    onClickArtist: ViewEventHandler,
    modifier: Modifier = Modifier,
) {
    val rowHeight = 48.dp

    Row(
        modifier = modifier
            .height(rowHeight)
            .clickable(render) { onClick.invoke() },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ProfileImage(
            render,
            profileImage,
            onClickArtist,
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(text = title)

            if (summary != null) {
                Text(text = summary)
            }
        }

        Spacer(modifier = Modifier.width(16.dp))
    }
}