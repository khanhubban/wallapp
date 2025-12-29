package wallapp.ui.content.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import wallapp.content.state.profile.ProfileCuratorViewState
import wallapp.image.Image
import wallapp.pixel.clickable.clickable
import wallapp.pixel.render.Render
import wallapp.pixel.shape.clip
import wallapp.pixel.spacer.Spacer
import wallapp.pixel.text.Text
import wallapp.pixel.theme.ThemeColorTypeMapper
import wallapp.pixel.view.onClick

@Composable
fun ProfileCurator(
    render: Render,
    viewState: ProfileCuratorViewState,
    modifier: Modifier = Modifier,
) {
    val viewSpec = viewState.viewSpec
    val shapeSpec = viewSpec.containerShapeSpec
    val height = viewSpec.height
    val width = viewSpec.width
    // The shadow image has extra whitespace. Add this spacer at the bottom to correctly
    // make the content align visually.
    val bottomSpacerHeight = render.defaultViewSpec.paddingDefault
    val onClick = viewState.eventHandler.onClick

    val backgroundColor = ThemeColorTypeMapper.map(viewState.backgroundColor)
    val profileImageShape = remember { RoundedCornerShape(50) }
    val profileImage = viewState.profileImage
    val profileImageBorderSize = profileImage.borderSize
    val profileShadowImage = viewState.profileShadowImage
    val profileShadowImageSize = viewSpec.profileShadowImageSize
    val outlineColor = Color.White
    val name = viewState.name

    Box(
        modifier = modifier
            .height(height)
            .width(width)
            .clip(shapeSpec, render.shapeClipper)
            .background(backgroundColor)
            .clickable(render, onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center),
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally),
            ) {
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
            }

            Text(
                text = name,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
            Spacer(height = bottomSpacerHeight)
        }
    }
}