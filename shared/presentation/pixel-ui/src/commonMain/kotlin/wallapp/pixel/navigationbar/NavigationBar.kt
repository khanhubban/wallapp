package wallapp.pixel.navigationbar

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import wallapp.graphics.composeColor
import wallapp.image.Image
import wallapp.pixel.compose.clickableNoRipple
import wallapp.pixel.compose.conditional
import wallapp.pixel.render.Render
import wallapp.pixel.render.shapeMapperComposable
import wallapp.pixel.shape.ShapeSpec
import wallapp.pixel.text.Text


@Composable
fun RowScope.NavigationBarItem(
    render: Render,
    navigationBarItem: NavigationBarItem,
    modifier: Modifier = Modifier,
) {
    NavigationBarItem(
        render,
        modifier = modifier,
        selected = navigationBarItem.isSelected,
        onClick = navigationBarItem.onClick,
        selectedIcon = navigationBarItem.selectedImage,
        unselectedIcon = navigationBarItem.unselectedImage,
        imageSize = navigationBarItem.imageSize,
        imageShapeSpec = navigationBarItem.imageShapeSpec,
        text = navigationBarItem.text,
        alwaysShowLabel = navigationBarItem.alwaysShowLabel,
    )
}

@Composable
fun RowScope.NavigationBarItem(
    render: Render,
    selected: Boolean,
    onClick: () -> Unit,
    selectedIcon: Image,
    unselectedIcon: Image,
    modifier: Modifier = Modifier,
    imageSize: Dp = 24.dp,
    imageShapeSpec: ShapeSpec? = null,
    text: Text? = null,
    enabled: Boolean = true,
    alwaysShowLabel: Boolean = true,
) {
    val unselectedIconScale = 1.0f
    val selectedIconScale = 1f//1.3f
    val selectedColor = MaterialTheme.colorScheme.onSurface
    val unselectedColor = NavigationBarDefaults.navigationContentColor()
    val iconScale by animateFloatAsState(if (selected) selectedIconScale else unselectedIconScale)
    val contentColor by animateColorAsState(if (selected) selectedColor else unselectedColor)
    val image = if (selected) selectedIcon else unselectedIcon

//    val delta = unselectedIconScale - iconScale
//    val baseDelta = unselectedIconScale - selectedIconScale
//    val scaleProgress = if (baseDelta == 0f) 0f else delta / baseDelta
//    val alpha = lerp(start = .6f, stop = 1f, amount = scaleProgress)
    val alpha = 1f

    val shape = imageShapeSpec?.let { render.shapeMapperComposable.map(it) }

    val icon: @Composable (() -> Unit) = {
        NavBarItem(
            render,
            modifier = Modifier
                .alpha(alpha)
                .clickableNoRipple(onClick),
            text = text,
            icon = image,
            iconScale = iconScale,
            shape = shape,
            imageSize = imageSize,
            contentColor = contentColor,
        )
    }

    // Set to false to get rid of the uncustomizable pill background
    val itemSelected = false //selected

    NavigationBarItem(
        selected = itemSelected,
        onClick = onClick,
        icon = icon,
        modifier = modifier,
        enabled = enabled,
        label = null,
        alwaysShowLabel = alwaysShowLabel,
//        colors = NavigationBarItemDefaults.colors(
//            selectedIconColor = NavigationBarDefaults.navigationSelectedItemColor(),
//            unselectedIconColor = NavigationBarDefaults.navigationContentColor(),
//            selectedTextColor = NavigationBarDefaults.navigationSelectedItemColor(),
//            unselectedTextColor = NavigationBarDefaults.navigationContentColor(),
//            indicatorColor = NavigationBarDefaults.navigationIndicatorColor(),
//        ),
    )
}

@Composable
private fun NavBarItem(
    render: Render,
    modifier: Modifier,
    text: Text?,
    icon: Image,
    iconScale: Float,
    shape: Shape?,
    imageSize: Dp,
    contentColor: Color,
) {
    val iconAlpha = 1f
    val colorFilter = ColorFilter.tint(contentColor)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth(),
    ) {
        Spacer(modifier = Modifier.weight(1f))
        NavBarItemIcon(
            render,
            Modifier.alpha(iconAlpha),
            icon,
            iconScale,
            shape,
            imageSize,
            colorFilter,
        )
        if (text != null) {
            NavBarItemLabel(text = text, color = contentColor)
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
private fun NavBarItemLabel(
    text: Text,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        modifier = modifier
            .padding(top = 4.dp),
        colorOverride = color,
    )
}

@Composable
private fun NavBarItemIcon(
    render: Render,
    modifier: Modifier,
    icon: Image,
    iconScale: Float,
    shape: Shape?,
    imageSize: Dp,
    colorFilter: ColorFilter?,
) {
    Image(
        render,
        image = icon,
//            tint = LocalContentColor.current,
        modifier = modifier
            .scale(iconScale)
            .conditional(
                shape != null,
                ifTrue = {
                    this
                        .size(imageSize)
                        .conditional(shape != null) { clip(shape!!) }
                },
                ifFalse = {
                    this.size(imageSize)
                },
            ),
        colorFilter = colorFilter,
    )
}

@Composable
fun NavigationBar(
    render: Render,
    viewState: NavigationBarViewState,
    systemNavigationBarHeightOffset: Dp,
    modifier: Modifier = Modifier,
) {
//    if (PlatformFeature.ComposeRendersSystemBars) {
        NavigationBarCompat(
            render,
            viewState = viewState,
            systemNavigationBarHeightOffset = systemNavigationBarHeightOffset,
            modifier = modifier,
        )
//    } else {
//        NavigationBarDefaultNavBar(
//            render,
//            viewState = viewState,
//            modifier = modifier,
//        )
//    }
}

/**
 * Renders navigation bar using a custom Row-based solution rather than using
 * [androidx.compose.material3.NavigationBar], which has height issues on iOS.
 */
@Composable
fun NavigationBarCompat(
    render: Render,
    viewState: NavigationBarViewState,
    systemNavigationBarHeightOffset: Dp,
    modifier: Modifier = Modifier,
) {
    // Magic number alert! This works for now, but needs to be revisited.
    val navBarItemsHeight = viewState.viewSpec.navBarItemsHeight
    val edgePadding = viewState.viewSpec.edgePadding

    val containerColor = viewState.containerColor?.color?.composeColor
        ?: MaterialTheme.colorScheme.surfaceVariant

    Column(
        modifier
            .background(containerColor)
    ) {
        Row(
            modifier = Modifier
                .height(navBarItemsHeight)
                .fillMaxWidth(),
        ) {
            Spacer(modifier = Modifier.width(edgePadding))
            NavigationBarItems(render, viewState)
            Spacer(modifier = Modifier.width(edgePadding))
        }

        Box(
            modifier = Modifier
                .height(systemNavigationBarHeightOffset)
                .fillMaxWidth(),
        )
    }
}

@Composable
fun NavigationBarDefaultNavBar(
    render: Render,
    viewState: NavigationBarViewState,
    modifier: Modifier = Modifier,
) {
    val edgePadding = viewState.viewSpec.edgePadding
    NavigationBar(
        modifier,
        contentColor = NavigationBarDefaults.navigationContentColor(),
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 0.dp,
    ) {
        Spacer(modifier = Modifier.width(edgePadding))
        NavigationBarItems(render, viewState)
        Spacer(modifier = Modifier.width(edgePadding))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RowScope.NavigationBarItems(
    render: Render,
    viewState: NavigationBarViewState,
) {
    val items = viewState.items
    val showRipple = viewState.itemsDisplayRipple

    if (showRipple) {
        NavigationBarItems(items, render)
    } else {
        CompositionLocalProvider(LocalRippleConfiguration provides null) {
            NavigationBarItems(items, render)
        }
    }
}

@Composable
private fun RowScope.NavigationBarItems(
    items: List<NavigationBarItem>,
    render: Render,
) {
    items.forEach {
        NavigationBarItem(
            render,
            navigationBarItem = it,
        )
    }
}
