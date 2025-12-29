package wallapp.ui.content.artist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import me.onebone.toolbar.CollapsingToolbarScaffold
import me.onebone.toolbar.CollapsingToolbarScaffoldScope
import me.onebone.toolbar.CollapsingToolbarScaffoldState
import me.onebone.toolbar.CollapsingToolbarScope
import me.onebone.toolbar.ScrollStrategy
import me.onebone.toolbar.SnapConfig
import wallapp.content.state.artist.ArtistToolbarViewSpec
import wallapp.content.state.artist.ArtistToolbarViewState
import wallapp.content.state.artist.ArtistViewSpec
import wallapp.content.state.artist.ArtistViewState
import wallapp.pixel.animation.AnimatedViewSpec
import wallapp.pixel.render.Render
import wallapp.pixel.text.TextCentered
import wallapp.pixel.theme.ThemeColorTypeMapper
import wallapp.pixel.toolbar.Toolbar
import wallapp.ui.content.profile.ProfileImage
import wallapp.ui.content.social.SocialLinks
import wallapp.ui.content.toolbar.arbitrateToolbarContentAnimatedAlpha
import wallapp.ui.content.toolbar.rememberCollapsingToolbarScaffoldPersistableState
import wallapp.ui.widget.Separator
import wallapp.unit.Padding


@Composable
fun ArtistCollapsingToolbarScaffold(
    render: Render,
    modifier: Modifier,
    viewState: ArtistViewState.Success,
    body: @Composable CollapsingToolbarScaffoldScope.() -> Unit
) {
    val collapsingToolbarState = rememberCollapsingToolbarScaffoldPersistableState(
        viewState.artistToolbar.collapsingToolbarStateWrapper,
    )

    CollapsingToolbarScaffold(
        modifier = modifier
            .fillMaxSize(),
        state = collapsingToolbarState,
        scrollStrategy = ScrollStrategy.ExitUntilCollapsed,
        snapConfig = SnapConfig(),
        toolbar = {
            CollapsingToolbar(
                render,
                viewState.viewSpec,
                viewState.artistToolbar,
                collapsingToolbarState,
                modifier = Modifier,
            )
        }
    ) {
        body()
    }
}

@Composable
private fun CollapsingToolbarScope.Separator(
    render: Render,
    progress: Float,
    viewSpec: AnimatedViewSpec,
    artistViewSpec: ArtistViewSpec,
    modifier: Modifier = Modifier,
) {
    val height = artistViewSpec.toolbarViewSpec.separatorHeight
    if (height == 0.dp) return

    Separator(
        render,
        progress,
        viewSpec,
        thickness = artistViewSpec.toolbarViewSpec.separatorHeight,
        padding = Padding(horizontal = artistViewSpec.paddingDefault),
        modifier,
    )
}

@Composable
private fun CollapsingToolbarScope.CollapsingToolbar(
    render: Render,
    viewSpec: ArtistViewSpec,
    viewState: ArtistToolbarViewState,
    collapsingToolbarState: CollapsingToolbarScaffoldState,
    modifier: Modifier = Modifier,
) {
    val toolbarViewSpec = viewSpec.toolbarViewSpec
    val profileAnimatedViewSpec = toolbarViewSpec.profileAnimatedViewSpec
    val separatorViewSpec = toolbarViewSpec.separatorViewSpec

    val minToolbarHeight = toolbarViewSpec.minToolbarHeight
    val maxToolbarHeight = toolbarViewSpec.maxToolbarHeight

    val progress = collapsingToolbarState.toolbarState.progress
    val contentAlpha = arbitrateToolbarContentAnimatedAlpha(progress)

    val toolbar = viewState.toolbarViewState
    val containerColorOverride = ThemeColorTypeMapper.map(viewState.containerColorOverride)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(minToolbarHeight)
            .height(maxToolbarHeight)
            .parallax(0.5f)
            .background(color = containerColorOverride)
            .graphicsLayer(alpha = contentAlpha),
    ) {
        ArtistScreenHeader(render, viewSpec.toolbarViewSpec, viewState)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {

        Toolbar(
            render,
            toolbar,
            modifier = Modifier
                .fillMaxWidth()
                .height(minToolbarHeight),
        )
    }

    ProfileImage(
        render,
        progress,
        viewState.profileImage,
        profileAnimatedViewSpec,
    )
    Separator(render, progress, separatorViewSpec, viewSpec)

    // For reasons unknown, adding a stub box stops the collapsing toolbar constantly jumping
    // between expanded and collapsed.
//    Box(
//        modifier = Modifier.heightIn(min = minToolbarHeight)
//            .fillMaxWidth()
//    ) {}
}


@Composable
private fun ArtistScreenHeader(
    render: Render,
    viewSpec: ArtistToolbarViewSpec,
    artistToolbarViewState: ArtistToolbarViewState,
    modifier: Modifier = Modifier,
) {
    val title = artistToolbarViewState.name
    val socialLinks = artistToolbarViewState.socialLinks

    val titleTop = viewSpec.titleTop
    val titleHeight = viewSpec.titleHeight
    val socialLinksTop = viewSpec.socialLinksTop
    val socialLinksHeight = viewSpec.socialLinksHeight

    Box(
        modifier = modifier
            .fillMaxWidth(),
    ) {
        TextCentered(
            text = title,
            height = titleHeight,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = titleTop),
        )

        if (socialLinks != null) {
            SocialLinks(
                render,
                socialLinks,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = socialLinksTop)
                    .height(socialLinksHeight)
            )
        }
    }
}