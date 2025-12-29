package wallapp.ui.content.collection

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.lerp
import me.onebone.toolbar.CollapsingToolbarScope
import wallapp.content.state.collection.CollectionActionButtonViewSpec
import wallapp.content.state.collection.CollectionActionButtonViewState
import wallapp.pixel.animation.AnimatedSnapshot
import wallapp.pixel.animation.animate
import wallapp.pixel.button.Button
import wallapp.pixel.compose.clickableNoRipple
import wallapp.pixel.compose.conditional
import wallapp.pixel.menu.MenuItem
import wallapp.pixel.menu.MenuItemProgressButton
import wallapp.pixel.render.Render
import wallapp.pixel.render.shapeMapperComposable
import wallapp.pixel.tab.toDp
import wallapp.pixel.text.Text
import wallapp.pixel.theme.ThemeColorTypeMapper
import wallapp.pixel.toolbar.CollapsingToolbarContent
import wallapp.pixel.view.onClick
import wallapp.theme.ColorToken
import wallapp.ui.content.toolbar.arbitrateToolbarContentAnimatedAlpha

@Composable
fun CollapsingToolbarScope.CollectionEntitlementButton(
    render: Render,
    progress: Float,
    collectionActionButtonViewState: CollectionActionButtonViewState,
    modifier: Modifier = Modifier,
) {
    CollapsingToolbarContent(
        render,
        modifier = modifier,
        animatedViewSpec = collectionActionButtonViewState.viewSpec.animatedViewSpec,
        progress = progress,
    ) { mod, snapshot ->
        CollectionEntitlementButton(render, collectionActionButtonViewState, snapshot, mod)
    }
}

@Composable
fun CollectionEntitlementButton(
    render: Render,
    collectionActionButtonViewState: CollectionActionButtonViewState,
    animatedSnapshot: AnimatedSnapshot,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
    ) {
        CollectionEntitlementButton(
            render,
            collectionActionButtonViewState,
            modifier = Modifier,
            animationProgress = animatedSnapshot.progress,
        )
    }
}

@Composable
fun CollectionEntitlementButton(
    render: Render,
    collectionActionButtonViewState: CollectionActionButtonViewState,
    modifier: Modifier = Modifier,
    animationProgress: Float,
) {
    val viewSpec = collectionActionButtonViewState.viewSpec
    val buttonViewState = collectionActionButtonViewState.buttonViewState

    val onClick = buttonViewState.eventHandler?.onClick
    val snapshot = buttonViewState.animatedViewSpec?.animate(render, animationProgress)

    if (buttonViewState.menuItem !is MenuItem.MenuItemProgressButton) {
        val (image, label, labelAlt) = collectionActionButtonViewState.getButtonItems()
        val shape = render.shapeMapperComposable.map(buttonViewState.shapeSpec!!)!!

        Button(
            render = render,
            onClick = onClick ?: {},
            modifier = modifier
                .conditional(
                    condition = onClick != null,
                    ifTrue = { this },
                    ifFalse = { clickableNoRipple { } },
                ),
            shape = shape,
            contentPadding = PaddingValues(),
            colors = ButtonDefaults.buttonColors()
        ) {
            CollectionEntitlementButtonContent(render, animationProgress, viewSpec, image, label, labelAlt, collectionActionButtonViewState)
        }
    } else {
        MenuItemProgressButton(
            render,
            modifier,
            buttonViewState.menuItem as MenuItem.MenuItemProgressButton,
            snapshot,
        )
    }
}

@Composable
private fun RowScope.CollectionEntitlementButtonContent(
    render: Render,
    animationProgress: Float,
    viewSpec: CollectionActionButtonViewSpec,
    image: MenuItem,
    label: Text,
    altLabel: Text,
    progressButton: CollectionActionButtonViewState? = null,
) {
    when (viewSpec) {
        is CollectionActionButtonViewSpec.BuyCollectionActionButtonViewSpec -> {
            CollectionEntitlementButtonContent(
                render,
                animationProgress,
                image,
                label,
                requireNotNull(altLabel),
            )
        }

        is CollectionActionButtonViewSpec.GetCollectionActionButtonViewSpec -> {
            CollectionEntitlementButtonContent(
                render,
                animationProgress,
                viewSpec,
                image,
                label,
                altLabel,
            )
        }

        is CollectionActionButtonViewSpec.DownloadProgressButtonViewSpec -> {
            require(progressButton is CollectionActionButtonViewState.DownloadProgressButton)
            CollectionEntitlementProgressButtonContent(
                render,
                animationProgress,
                viewSpec,
                progressButton,
            )
        }
    }
}

@Composable
private fun RowScope.CollectionEntitlementButtonContent(
    render: Render,
    animationProgress: Float,
    image: MenuItem,
    label: Text,
    priceLabel: Text,
) {
    val alpha = arbitrateToolbarContentAnimatedAlpha(animationProgress)

    val paddingDefault = render.defaultViewSpec.paddingDefault

    var labelWidth by remember { mutableStateOf(0f) }

    Row(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Spacer(modifier = Modifier.weight(1f))
        MenuItem(
            render = render,
            menuItem = image,
            modifier = Modifier,
        )

        Spacer(modifier = Modifier.size(width = paddingDefault, height = Dp.Unspecified))

        Box(
            modifier = Modifier.onGloballyPositioned {
                if (labelWidth == 0f) {
                    labelWidth = it.size.width.toFloat()
                }
            },
        ) {
            val width = (labelWidth * animationProgress).toDp()
            Text(
                label,
                modifier = Modifier
                    .graphicsLayer(alpha = alpha)
                    .scale(scaleX = animationProgress, scaleY = 1f)
                    .conditional(animationProgress < 1f) { width(width) },
            )
        }

        Spacer(modifier = Modifier.size(width = paddingDefault * animationProgress, height = Dp.Unspecified))

        Text(
            text = priceLabel,
            modifier = Modifier,
        )
        Spacer(modifier = Modifier.weight(1f))
    }
}


@Composable
private fun RowScope.CollectionEntitlementButtonContent(
    render: Render,
    animationProgress: Float,
    viewSpec: CollectionActionButtonViewSpec.GetCollectionActionButtonViewSpec,
    image: MenuItem,
    label: Text,
    altLabel: Text,
) {
    val alpha = arbitrateToolbarContentAnimatedAlpha(animationProgress)

    val iconItemPaddingStart = lerp(
        viewSpec.iconPaddingStartMin, viewSpec.iconPaddingStartMax, animationProgress)
    val labelItemPaddingStart = lerp(
        viewSpec.labelPaddingStartMin, viewSpec.labelPaddingStartMax, animationProgress)

    Box(
        modifier = Modifier.fillMaxWidth(),
    ) {
        MenuItem(
            render = render,
            menuItem = image,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = iconItemPaddingStart),
        )

        Row(
            modifier = Modifier
                .padding(start = labelItemPaddingStart)
                .align(Alignment.CenterStart),
        ) {
            Text(
                label,
                modifier = Modifier,
            )

            if (alpha > 0f) {
                Text(
                    text = altLabel,
                    modifier = Modifier
                        .graphicsLayer(alpha = alpha)
                )
            }
        }

    }
}

@Composable
private fun RowScope.CollectionEntitlementProgressButtonContent(
    render: Render,
    animationProgress: Float,
    viewSpec: CollectionActionButtonViewSpec.DownloadProgressButtonViewSpec,
    progressButton: CollectionActionButtonViewState.DownloadProgressButton,
) {
    val alpha = arbitrateToolbarContentAnimatedAlpha(animationProgress)
    val minAlpha = 1f - alpha

    val minIconPaddingStart = viewSpec.minIconPaddingStart
    val maxLabelPaddingStart = viewSpec.maxLabelPaddingStart
    val countLabelPaddingEnd = lerp(
        viewSpec.countLabelPaddingEndMin, viewSpec.countLabelPaddingEndMax, animationProgress
    )

    val minIcon = progressButton.minIcon
    val countLabel = progressButton.countLabel
    val maxLabel = progressButton.maxLabel

    val backgroundColor = ThemeColorTypeMapper.map(ColorToken.ThemeTertiary)
    val progressColor = ThemeColorTypeMapper.map(ColorToken.ThemeSecondary)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = backgroundColor),
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(progressButton.progress)
                .background(color = progressColor),
        )
        if (minAlpha > 0) {
            MenuItem(
                render = render,
                menuItem = minIcon,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = minIconPaddingStart)
                    .alpha(minAlpha),
            )
        }

        if (alpha > 0) {
            Text(
                maxLabel,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = maxLabelPaddingStart)
                    .alpha(alpha),
            )
        }

        Text(
            countLabel,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = countLabelPaddingEnd),
        )
    }
}
