package wallapp.ui.content.firstrun

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import wallapp.content.state.firstrun.FirstRunViewState
import wallapp.pixel.render.Render
import wallapp.pixel.theme.AppTheme
import wallapp.pixel.theme.dynamicColorScheme
import wallapp.ui.AppScreen
import wallapp.ui.content.carousel.ImageCarouselBackground

@Composable
fun FirstRunScreen(
    render: Render,
    viewState: FirstRunViewState,
    modifier: Modifier = Modifier,
) {
    when (viewState) {
        is FirstRunViewState.Loading -> { }
        is FirstRunViewState.Success -> {
            FirstRunScreen(render, viewState, modifier)
        }
    }
}

@Composable
fun FirstRunScreen(
    render: Render,
    viewState: FirstRunViewState.Success,
    modifier: Modifier = Modifier,
) {
    val themeColors = viewState.theme.themeColors
    val colorScheme = dynamicColorScheme(themeColors = themeColors)

    AppTheme(
        render = render,
        colorScheme = colorScheme,
    ) {
        CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onBackground) {
            FirstRunContent(
                render = render,
                viewState = viewState,
                modifier = modifier,
            )
        }
    }
}

@Composable
fun FirstRunContent(
    render: Render,
    viewState: FirstRunViewState.Success,
    modifier: Modifier = Modifier,
) {
    val backgroundCarousel = viewState.backgroundCarousel
    val screen = viewState.screen

    Box(
        modifier = modifier.fillMaxHeight(),
    ) {
        ImageCarouselBackground(backgroundCarousel, render)

        if (screen != null) {
            AppScreen(render, screen, Modifier.fillMaxSize())
        }
    }
}