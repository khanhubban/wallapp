package wallapp.ui.content.upgrade.plus.promo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import wallapp.content.state.upgrade.plus.promo.UpgradePromoViewState
import wallapp.graphics.Colors
import wallapp.graphics.composeColor
import wallapp.image.Image
import wallapp.pixel.menu.MenuItem
import wallapp.pixel.render.Render
import wallapp.pixel.theme.AppTheme
import wallapp.pixel.theme.dynamicColorScheme

@Composable
fun UpgradePromo(
    render: Render,
    viewState: UpgradePromoViewState,
    modifier: Modifier = Modifier,
) {
    val theme = viewState.theme
    val colorScheme = dynamicColorScheme(themeColors = theme.themeColors)

    AppTheme(
        render = render,
        colorScheme = colorScheme,
    ) {
        Surface(modifier) {
            UpgradePromoContent(render, viewState)
        }
    }
}

@Composable
fun UpgradePromoContent(
    render: Render,
    viewState: UpgradePromoViewState,
    modifier: Modifier = Modifier,
) {
    val title = viewState.title
    val heroImage = viewState.heroImage
    val actionButton = viewState.actionButton
    val paddingLarge = render.defaultViewSpec.paddingLarge

    Box(
        modifier = modifier
            .background(Colors.Black.composeColor),
    ) {
        Image(
            render,
            heroImage,
            modifier = Modifier.fillMaxWidth(),
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingLarge)
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.weight(1f))
            MenuItem(render, title)
            Spacer(modifier = Modifier.height(paddingLarge))
            MenuItem(render, actionButton)
        }
    }
}