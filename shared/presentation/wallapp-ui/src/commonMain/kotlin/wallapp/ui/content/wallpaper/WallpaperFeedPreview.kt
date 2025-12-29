package wallapp.ui.content.wallpaper

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import wallapp.content.state.favorite.FavoriteViewState
import wallapp.content.state.upgrade.plus.indicator.PlusIndicatorViewState
import wallapp.content.state.wallpaper.WallpaperPreviewViewSpec
import wallapp.content.state.wallpaper.WallpaperPreviewViewState
import wallapp.graphics.Color
import wallapp.graphics.composeColor
import wallapp.image.Image
import wallapp.pixel.clickable.clickable
import wallapp.pixel.compose.paddingAx
import wallapp.pixel.image.ImageViewState
import wallapp.pixel.render.Render
import wallapp.pixel.shape.ShapeSpec
import wallapp.pixel.shape.clip
import wallapp.pixel.text.Text
import wallapp.pixel.view.ViewEventHandler
import wallapp.ui.content.favorite.FavoriteButton
import wallapp.ui.content.upgrade.plus.indicator.PlusIndicator
import wallapp.unit.Padding


@Composable
fun WallpaperFeedPreview(
    render: Render,
    wallpaperPreview: WallpaperPreviewViewState,
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.Center,
) {
    val viewSpec = wallpaperPreview.viewSpec
    val shapeSpec = viewSpec.shapeSpec
    val imageViewState = wallpaperPreview.imageViewState
    val onClick = wallpaperPreview.onClick

    when (viewSpec) {
        is WallpaperPreviewViewSpec.WithFooter -> {
            val title = wallpaperPreview.title
            val favorite = wallpaperPreview.favorite
            val scrimImage = wallpaperPreview.scrimImage
            val contentColor = Color.White.composeColor

            WallpaperFeedPreview(
                render,
                modifier,
                viewSpec,
                shapeSpec,
                scrimImage,
                contentColor,
                imageViewState,
                title,
                alignment,
                favorite,
                onClick,
            )
        }

        is WallpaperPreviewViewSpec.Default -> {
            val plusIndicator = requireNotNull(wallpaperPreview.plusIndicator) { "plusIndicator is required and cannot be null." }
            WallpaperFeedPreview(
                render,
                modifier,
                viewSpec,
                shapeSpec,
                imageViewState,
                alignment,
                plusIndicator,
                onClick,
            )
        }
    }
}

@Composable
private fun WallpaperFeedPreview(
    render: Render,
    modifier: Modifier,
    viewSpec: WallpaperPreviewViewSpec.Default,
    shapeSpec: ShapeSpec?,
    imageViewState: ImageViewState,
    alignment: Alignment,
    plusIndicator: PlusIndicatorViewState,
    onClick: ViewEventHandler,
) {
    val width = viewSpec.width
    val height = viewSpec.height

    val paddingSmall = render.defaultViewSpec.paddingSmall

    Box(
        modifier = modifier
            .width(width)
            .height(height)
            .clip(shapeSpec, render.shapeClipper)
            .clickable(render) { onClick.invoke() },
    ) {
        Image(
            render,
            viewState = imageViewState,
            modifier = Modifier
                .fillMaxSize(),
            alignment = alignment,
        )

        PlusIndicator(
            render,
            viewState = plusIndicator,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = paddingSmall, bottom = paddingSmall),
        )
    }
}


@Composable
private fun WallpaperFeedPreview(
    render: Render,
    modifier: Modifier,
    viewSpec: WallpaperPreviewViewSpec.WithFooter,
    shapeSpec: ShapeSpec?,
    scrim: Image,
    contentColor: androidx.compose.ui.graphics.Color,
    imageViewState: ImageViewState,
    title: Text?,
    alignment: Alignment,
    favorite: FavoriteViewState,
    onClick: ViewEventHandler,
) {
    val titleHeight = 26.dp
    val width = viewSpec.width
    val height = viewSpec.height
    val footerHeight = viewSpec.footerHeight
    val footerContentPadding = viewSpec.footerContentPadding

    Box(
        modifier = modifier
            .width(width)
            .height(height)
            .clip(shapeSpec, render.shapeClipper)
//            .ifNonNull(shape) { contentContainer(it) }
            .clickable(render) { onClick.invoke() },
    ) {
        require(imageViewState.viewSpec.shapeSpec == null) {
            "ImageViewState.shapeSpec must be null, but ${imageViewState.viewSpec.shapeSpec} was specified. Any clipping required should be handled via `WallpaperPreviewViewSpec.WithFooter.shapeSpec`.\n  ${imageViewState.image.id}"
        }
        Image(
            render,
            viewState = imageViewState,
            modifier = Modifier
                .fillMaxSize(),
            alignment = alignment,
        )

        WallpaperPreviewFooter(
            render,
            footerHeight,
            footerContentPadding,
            scrim,
            title,
            contentColor,
            titleHeight,
            favorite,
        )
    }
}

@Composable
private fun BoxScope.WallpaperPreviewFooter(
    render: Render,
    footerHeight: Dp,
    footerContentPadding: Padding,
    scrimImage: Image,
    title: Text?,
    contentColor: androidx.compose.ui.graphics.Color,
    titleHeight: Dp,
    favorite: FavoriteViewState,
) {
    Box(
        modifier = Modifier
            .height(footerHeight)
            .fillMaxWidth()
            .align(Alignment.BottomCenter),
    ) {
        Image(
            render,
            scrimImage,
            modifier = Modifier
                .fillMaxSize(),
            contentScale = ContentScale.FillBounds,
        )

        Row(
            modifier = Modifier
                .fillMaxSize()
                .paddingAx(footerContentPadding),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (title != null) {
                Text(
                    text = title,
                    modifier = Modifier
                        .height(titleHeight)
                        .weight(1f),
                    colorOverride = contentColor,
                )
            }

            FavoriteButton(
                render = render,
                favorite,
                tintColor = contentColor,
                modifier = Modifier.semantics {
                    contentDescription = favorite.favoriteContentDescription
                }
            )
        }
    }
}