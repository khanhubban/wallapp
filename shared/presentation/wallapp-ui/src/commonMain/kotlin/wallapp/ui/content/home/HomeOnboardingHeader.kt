package wallapp.ui.content.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import wallapp.content.state.home.HomeOnboardingHeaderViewSpec
import wallapp.content.state.home.HomeOnboardingHeaderViewState
import wallapp.pixel.render.Render
import wallapp.pixel.text.Text
import wallapp.pixel.theme.AppTheme
import wallapp.pixel.theme.dynamicColorScheme

@Composable
fun HomeOnboardingHeader(
    render: Render,
    viewState: HomeOnboardingHeaderViewState,
    modifier: Modifier = Modifier,
) {
    val colorScheme = dynamicColorScheme(themeColors = viewState.theme.themeColors)

    val title = viewState.title
    val summary = viewState.summary

    val viewSpec = viewState.viewSpec

    AppTheme(
        render = render,
        colorScheme = colorScheme,
    ) {
        HomeOnboardingHeader(render, modifier, viewSpec, title, summary)
    }
}

@Composable
private fun HomeOnboardingHeader(
    render: Render,
    modifier: Modifier,
    viewSpec: HomeOnboardingHeaderViewSpec,
    title: Text,
    summary: Text,
) {
    val height = viewSpec.height

    val background = MaterialTheme.colorScheme.background
    val onBackground = MaterialTheme.colorScheme.onBackground

    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .background(background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(
            modifier = Modifier.height(render.windowFrame.statusBarHeight)
        )
        Spacer(modifier = Modifier.height(viewSpec.verticalPadding))
        Text(
            text = title,
            colorOverride = onBackground,
        )
        Spacer(modifier = Modifier.height(viewSpec.itemSpacing))
        Text(
            text = summary,
            colorOverride = onBackground,
        )
        Spacer(modifier = Modifier.height(viewSpec.verticalPadding).fillMaxWidth())
    }
}
