package wallapp.ui.content.upgrade.plus.button

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import wallapp.content.state.upgrade.plus.button.UpgradeButtonViewState
import wallapp.pixel.button.Button
import wallapp.pixel.button.LogoAndIconButton
import wallapp.pixel.menu.MenuItem
import wallapp.pixel.render.Render
import wallapp.pixel.render.shapeMapperComposable
import wallapp.pixel.view.onClick
import wallapp.ui.content.upgrade.plus.indicator.PlusIndicator

@Composable
fun UpgradeButton(
    render: Render,
    viewState: UpgradeButtonViewState,
    modifier: Modifier = Modifier,
) {
    val heroSize = viewState.heroSize

    if (heroSize) {
        UpgradeButtonHeroSize(
            render = render,
            viewState = viewState,
            modifier = modifier,
        )
    } else {
        UpgradeButtonDefault(
            render = render,
            viewState = viewState,
            modifier = modifier,
        )
    }
}

@Composable
private fun UpgradeButtonDefault(
    render: Render,
    viewState: UpgradeButtonViewState,
    modifier: Modifier = Modifier,
) {
    val onClick = viewState.viewEventHandler.onClick
    val plusIndicator = viewState.indicator
    val label = viewState.label
    val shape = render.shapeMapperComposable.map(viewState.shapeSpec)!!

    Button(
        render = render,
        onClick = onClick,
        modifier = modifier,
        shape = shape,
        contentPadding = PaddingValues(),
    ) {

        Spacer(
            modifier = Modifier.width(8.dp),
        )

        PlusIndicator(
            render = render,
            viewState = plusIndicator,
        )

        MenuItem(
            render = render,
            menuItem = label,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        )
    }

}

@Composable
private fun UpgradeButtonHeroSize(
    render: Render,
    viewState: UpgradeButtonViewState,
    modifier: Modifier = Modifier,
) {
    val logoSize: Dp = 44.dp
    val imageSize = logoSize / 1.5f
    val plusIndicator = viewState.indicator
    val viewEventHandler = viewState.viewEventHandler
    val backgroundColor = MaterialTheme.colorScheme.secondary
    val foregroundColor = MaterialTheme.colorScheme.onSecondary
    val label = viewState.label
    val shape = render.shapeMapperComposable.map(viewState.shapeSpec)!!

    LogoAndIconButton(
        render = render,
        image = {
            PlusIndicator(
                render = render,
                viewState = plusIndicator,
                modifier = Modifier
                    .size(imageSize)
                    .align(Alignment.Center),
            )
        },
        label = {
            MenuItem(
                render = render,
                menuItem = label,
                modifier = Modifier.align(Alignment.Center),
//                color = foregroundColor,
            )
        },
        viewEventHandler = viewEventHandler,
        backgroundColor = backgroundColor,
        foregroundColor = foregroundColor,
        shape = shape,
        modifier = modifier,
    )
}