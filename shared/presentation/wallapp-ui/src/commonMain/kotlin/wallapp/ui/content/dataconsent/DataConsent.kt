package wallapp.ui.content.dataconsent

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import wallapp.content.state.dataconsent.DataConsentViewState
import wallapp.pixel.menu.MenuItem
import wallapp.pixel.render.Render
import wallapp.pixel.text.Text
import wallapp.pixel.theme.AppTheme
import wallapp.ui.content.settings.SettingsFeed

@Composable
fun DataConsent(
    render: Render,
    viewState: DataConsentViewState,
    modifier: Modifier = Modifier,
) {

    AppTheme(
        render = render,
    ) {
        CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onBackground) {
            DataConsentContent(
                render = render,
                viewState = viewState,
                modifier = modifier,
            )
        }
    }
}

@Composable
fun DataConsentContent(
    render: Render,
    viewState: DataConsentViewState,
    modifier: Modifier = Modifier,
) {
    val title = viewState.title
    val settings = viewState.settings
    val continueButton = viewState.continueButton
    val footer = viewState.footer

    val buttonWidth = 300.dp
    val verticalPadding = 24.dp
    val paddingSmall = render.defaultViewSpec.paddingSmall

    Box(
        modifier = modifier.fillMaxHeight(),
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(top = render.windowFrame.statusBarHeight + verticalPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = title)
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = render.windowFrame.navigationBarHeight + verticalPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            SettingsFeed(render, settings)

            Spacer(modifier = Modifier.height(verticalPadding))

            MenuItem(render, continueButton, modifier = Modifier.width(buttonWidth))

            Spacer(modifier = Modifier.height(paddingSmall))

            MenuItem(render, footer, modifier = Modifier.fillMaxWidth())
        }
    }
}
