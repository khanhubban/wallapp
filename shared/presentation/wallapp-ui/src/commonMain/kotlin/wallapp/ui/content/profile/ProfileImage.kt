package wallapp.ui.content.profile

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import me.onebone.toolbar.CollapsingToolbarScope
import wallapp.content.state.profile.ProfileImageIndicatorViewState
import wallapp.content.state.profile.ProfileImageViewState
import wallapp.image.Image
import wallapp.pixel.animation.AnimatedSnapshot
import wallapp.pixel.animation.AnimatedViewSpec
import wallapp.pixel.clickable.clickable
import wallapp.pixel.compose.clickableIfNotNull
import wallapp.pixel.compose.conditional
import wallapp.pixel.menu.MenuItem
import wallapp.pixel.render.Render
import wallapp.pixel.theme.ThemeColorTypeMapper
import wallapp.pixel.toolbar.CollapsingToolbarContent
import wallapp.pixel.view.ViewEventHandler
import wallapp.pixel.view.onClick

@Composable
fun ProfileImage(
    render: Render,
    profileImage: ProfileImageViewState,
    eventHandler: ViewEventHandler?,
    modifier: Modifier = Modifier,
    profileImageSizeOverride: Dp? = null,
    shape: Shape = RoundedCornerShape(50),
    outlineColor: Color = MaterialTheme.colorScheme.tertiary,
) {
    val indicator = profileImage.indicator
    val imageSize = profileImageSizeOverride ?: profileImage.imageViewSpec.size

    if (indicator != null && eventHandler != null) {
        Box(
            modifier = modifier,
        ) {
            ProfileImageContainer(
                render,
                profileImage,
                imageSize,
                shape,
                eventHandler,
                outlineColor,
            )

            IndicatorImage(
                render,
                indicator,
                shape,
                eventHandler = eventHandler,
                modifier = Modifier
                    .align(Alignment.BottomEnd),
            )
        }
    } else {
        ProfileImageContainer(
            render,
            profileImage,
            imageSize,
            shape,
            eventHandler,
            outlineColor,
            modifier,
        )
    }
}

@Composable
fun ProfileImageContainer(
    render: Render,
    profileImage: ProfileImageViewState,
    imageSize: Dp,
    shape: Shape,
    eventHandler: ViewEventHandler?,
    outlineColor: Color,
    modifier: Modifier = Modifier,
) {
    val image = profileImage.image
    val borderSize = profileImage.borderSize

    Box(
        modifier = modifier
            .size(imageSize)
            .clip(shape)
            .conditional(eventHandler != null) {
                clickableIfNotNull(render, eventHandler!!.onClick)
            },
    ) {
        ProfileImage(
            render,
            image,
            modifier = Modifier.size(imageSize),
            shape,
            outlineColor,
            borderSize,
        )
    }
}

@Composable
private fun IndicatorImage(
    render: Render,
    indicator: ProfileImageIndicatorViewState,
    shape: Shape,
    eventHandler: ViewEventHandler,
    modifier: Modifier = Modifier,
) {
    val viewSpec = indicator.viewSpec
    val size = viewSpec.size
    val borderSize = viewSpec.borderSize

    val indicatorImage = indicator.image
    val indicatorBorderColor = ThemeColorTypeMapper.map(indicator.colorToken)

    AnimatedContent(
        indicatorImage,
        modifier = modifier
            .offset(x = borderSize, y = borderSize)
            .clip(shape)
            .clickable(render) { eventHandler.onClick.invoke() },
        contentKey = { it.id }
    ) { image ->
        Image(
            render,
            image,
            modifier = Modifier
                .size(size)
                .padding(borderSize),
        )
        Box(
            modifier = Modifier
                .size(size)
                .border(borderSize, indicatorBorderColor, shape),
        )
    }
}

@Composable
private fun BoxScope.ProfileImage(
    render: Render,
    image: MenuItem,
    modifier: Modifier,
    shape: Shape,
    outlineColor: Color,
    borderSize: Dp,
) {
    MenuItem(
        render,
        menuItem = image,
        modifier = modifier
            .conditional(borderSize > 0.dp) { padding(borderSize) }
            .clip(shape),
//        contentScale = ContentScale.Crop,
    )

    if (borderSize > 0.dp) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .border(borderSize, outlineColor, shape),
        )
    }
}

@Composable
fun CollapsingToolbarScope.ProfileImage(
    render: Render,
    progress: Float,
    profileImage: ProfileImageViewState,
    animatedViewSpec: AnimatedViewSpec,
    modifier: Modifier = Modifier,
) {
    CollapsingToolbarContent(
        render,
        modifier = modifier,
        animatedViewSpec = animatedViewSpec,
        progress = progress,
    ) { mod, snapshot ->
        ToolbarProfileImage(render, profileImage, snapshot, mod)
    }
}

@Composable
private fun ToolbarProfileImage(
    render: Render,
    profileImage: ProfileImageViewState,
    snapshot: AnimatedSnapshot,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
    ) {
        ToolbarProfileImage(
            render,
            profileImage,
            profileImageEventHandler = profileImage.eventHandler,
            profileImageSizeOverride = snapshot.width!!,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
fun ToolbarProfileImage(
    render: Render,
    profileImage: ProfileImageViewState,
    profileImageEventHandler: ViewEventHandler?,
    profileImageSizeOverride: Dp?,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(50)
    ProfileImage(
        render,
        profileImage = profileImage,
        eventHandler = profileImageEventHandler,
        modifier = modifier,
        profileImageSizeOverride = profileImageSizeOverride,
        shape = shape,
    )
}
