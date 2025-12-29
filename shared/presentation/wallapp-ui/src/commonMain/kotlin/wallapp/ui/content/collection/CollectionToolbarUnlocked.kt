package wallapp.ui.content.collection

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import me.onebone.toolbar.CollapsingToolbarScaffoldState
import me.onebone.toolbar.CollapsingToolbarScope
import wallapp.content.state.collection.CollectionToolbarViewState
import wallapp.pixel.render.Render
import wallapp.pixel.text.TextCentered
import wallapp.pixel.theme.ThemeColorTypeMapper
import wallapp.pixel.toolbar.Toolbar
import wallapp.ui.content.profile.ProfileImage
import wallapp.ui.content.toolbar.arbitrateToolbarContentAnimatedAlpha

@Composable
fun CollapsingToolbarScope.CollapsingToolbarUnlocked(
    render: Render,
    toolbar: CollectionToolbarViewState.Unlocked,
    collapsingToolbarState: CollapsingToolbarScaffoldState,
) {
    val viewSpec = toolbar.viewSpec
    val minToolbarHeight: Dp = viewSpec.minToolbarHeight
    val maxToolbarHeight: Dp = viewSpec.maxToolbarHeight

    val toolbarViewSpec = toolbar.viewSpec
    val profileAnimatedViewSpec = toolbarViewSpec.profileAnimatedViewSpec
    val collectionActionButtonViewState = toolbar.collectionActionButtonViewState

    val progress = collapsingToolbarState.toolbarState.progress
    val contentAlpha = arbitrateToolbarContentAnimatedAlpha(progress)

    val containerColorOverride = ThemeColorTypeMapper.map(toolbar.containerColorOverride)
    val profileImage = toolbar.artistProfileImage

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(minToolbarHeight)
            .height(maxToolbarHeight)
            .parallax(0.5f)
            .background(color = containerColorOverride)
            .graphicsLayer(alpha = contentAlpha),
    ) {
        ToolbarContent(
            render,
            toolbar,
        )
    }

    Toolbar(
        render,
        toolbar = toolbar.toolbar,
        modifier = Modifier
            .fillMaxWidth()
            .height(minToolbarHeight),
    )

    ProfileImage(render, progress, profileImage, profileAnimatedViewSpec)

    CollectionEntitlementButton(render, progress, collectionActionButtonViewState)
}

@Composable
private fun ToolbarContent(
    render: Render,
    viewState: CollectionToolbarViewState.Unlocked,
    modifier: Modifier = Modifier,
) {
    val viewSpec = viewState.viewSpec

    val artistName = viewState.artistName
    val artistNameTop = viewSpec.artistNamePaddingTop
    val artistNameHeight = viewSpec.artistNameHeight

    val collectionName = viewState.title
    val collectionNameTop = viewSpec.collectionNamePaddingTop
    val collectionNameHeight = viewSpec.collectionNameHeight

    Box(
        modifier = modifier
            .fillMaxWidth(),
    ) {
        if (artistName != null && artistNameHeight != null && artistNameTop != null) {
            TextCentered(
                text = artistName,
                height = artistNameHeight,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = artistNameTop),
            )
        }

        TextCentered(
            text = collectionName,
            height = collectionNameHeight,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = collectionNameTop),
        )
    }
}
