package wallapp.pixel.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.invisibleToUser
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import wallapp.image.Image
import wallapp.pixel.animation.AnimatedSnapshot
import wallapp.pixel.button.Button
import wallapp.pixel.clickable.clickable
import wallapp.pixel.compose.clickableIfNotNull
import wallapp.pixel.compose.clickableNoRipple
import wallapp.pixel.compose.clickableUnboundRipple
import wallapp.pixel.compose.conditional
import wallapp.pixel.compose.ifNonNull
import wallapp.pixel.compose.paddingAx
import wallapp.pixel.font.TextStyleMapper
import wallapp.pixel.menu.MenuItem.MenuItemButton
import wallapp.pixel.menu.MenuItem.MenuItemContainer
import wallapp.pixel.menu.MenuItem.MenuItemDivider
import wallapp.pixel.menu.MenuItem.MenuItemGroup
import wallapp.pixel.menu.MenuItem.MenuItemGroupHorizontal
import wallapp.pixel.menu.MenuItem.MenuItemGroupVertical
import wallapp.pixel.menu.MenuItem.MenuItemIcon
import wallapp.pixel.menu.MenuItem.MenuItemImage
import wallapp.pixel.menu.MenuItem.MenuItemLabel
import wallapp.pixel.menu.MenuItem.MenuItemPopup
import wallapp.pixel.menu.MenuItem.MenuItemProgressButton
import wallapp.pixel.menu.MenuItem.MenuItemSpacer
import wallapp.pixel.render.Render
import wallapp.pixel.render.shapeMapperComposable
import wallapp.pixel.text.Text
import wallapp.pixel.theme.ThemeColorTypeMapper
import wallapp.pixel.theme.shape.AppShapes
import wallapp.pixel.view.View
import wallapp.pixel.view.ViewEventHandler
import wallapp.pixel.view.onClick
import wallapp.theme.ColorToken
import wallapp.unit.composeAlignment
import wallapp.unit.heightAx
import wallapp.unit.widthAx


@Composable
fun MenuItem(
    render: Render,
    menuItem: MenuItem,
    modifier: Modifier = Modifier,
) {
    when (menuItem) {
        is MenuItemButton -> {
            MenuItemButton(render, modifier, menuItem)
        }

        is MenuItemContainer -> {
            MenuItemContainer(render, modifier, menuItem)
        }

        is MenuItemIcon -> {
            MenuItemIcon(render, modifier, menuItem)
        }

        is MenuItemImage -> {
            MenuItemImage(render, modifier, menuItem)
        }

        is MenuItemLabel -> {
            MenuItemLabel(render, modifier, menuItem)
        }

        is MenuItemPopup -> {
            MenuItemPopup(render, modifier, menuItem)
        }

        is MenuItemDivider -> {
            MenuItemDivider(modifier, menuItem)
        }

        is MenuItemSpacer -> {
            MenuItemSpacer(modifier, menuItem)
        }

//        is MenuItemCustom -> {
//            MenuItemCustom(modifier, menuItem)
//        }

        is MenuItemGroup -> {
            MenuItemGroup(render, menuItemGroup = menuItem, modifier)
        }

        is MenuItemProgressButton -> {
            MenuItemProgressButton(render, modifier, menuItem)
        }

        is MenuItem.MenuItemViewStateWrapper<*> -> {
            MenuItemViewStateWrapper(render, modifier, menuItem)
        }
    }
}

@Composable
fun MenuItemGroup(
    render: Render,
    menuItemGroup: MenuItemGroup,
    modifier: Modifier = Modifier,
) {
    when (menuItemGroup) {
        is MenuItemGroupHorizontal -> {
            MenuItemGroupHorizontal(render, modifier, menuItemGroup)
        }

        is MenuItemGroupVertical -> {
            MenuItemGroupVertical(render, modifier, menuItemGroup)
        }
    }
}

@Composable
fun MenuItemContainer(
    render: Render,
    modifier: Modifier,
    menuItemContainer: MenuItemContainer,
) {
    val menuItem = menuItemContainer.menuItem
    val padding = menuItemContainer.padding
    val width = menuItemContainer.width
    val height = menuItemContainer.height
    val menuItemAlignment = menuItemContainer.menuItemAlignment?.composeAlignment
    val onClick = menuItemContainer.onClick?.onClick

    Box(
        modifier = modifier
            .ifNonNull(width) { this.width(it.dp) }
            .ifNonNull(height) { this.height(it.dp) }
//            .ifNonNull(onClick) { this.clickableUnboundRipple(it) }
            .ifNonNull(onClick) { this.clickable(render) { it() } }
            .ifNonNull(padding) { this.paddingAx(it) },
    ) {
        MenuItem(
            render,
            menuItem,
            modifier = Modifier
                .ifNonNull(menuItemAlignment) { this.align(it) },
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MenuItemButton(
    render: Render,
    modifier: Modifier,
    menuItemButton: MenuItemButton,
) {
    val button = menuItemButton.button
    val width = menuItemButton.width
    val height = menuItemButton.height

    Button(
        render,
        button,
        modifier = modifier
            .conditional(menuItemButton.button.eventHandler == null) {
                semantics {
                    disabled()
                    invisibleToUser()
                }
            }
            .ifNonNull(width) { this.width(it.dp) }
            .ifNonNull(height) { this.height(it.dp) },
        contentPadding = PaddingValues(0.dp),
    )
}

@Composable
fun MenuItemIcon(
    render: Render,
    modifier: Modifier,
    menuItemIcon: MenuItemIcon,
) {
    val onClick = menuItemIcon.onClick

    if (onClick != null) {
        MenuItemIconClickable(render, modifier, menuItemIcon, onClick)
    } else {
        MenuItemIconNonClickable(render, modifier, menuItemIcon)
    }
}

@Composable
fun MenuItemIconClickable(
    render: Render,
    modifier: Modifier,
    menuItemIcon: MenuItemIcon,
    eventHandler: ViewEventHandler,
) {
    val iconButtonLayerSize = render.defaultViewSpec.iconButtonLayerSize
    Box(
        modifier
            .minimumInteractiveComponentSize()
            .size(iconButtonLayerSize)
            .clickableUnboundRipple { eventHandler.invoke() },
    ) {
        MenuItemIconImage(
            render,
            menuItemIcon,
            modifier = Modifier
                .align(Alignment.Center),
        )
    }
}

@Composable
private fun MenuItemIconNonClickable(
    render: Render,
    modifier: Modifier,
    menuItemIcon: MenuItemIcon,
) {
    MenuItemIconImage(render, menuItemIcon, modifier)
}


@Composable
private fun MenuItemIconImage(
    render: Render,
    menuItemIcon: MenuItemIcon,
    modifier: Modifier = Modifier,
) {
    val icon = menuItemIcon.icon
    val height = menuItemIcon.height
    val width = menuItemIcon.width

    val tintColor = menuItemIcon.tintColor?.let { ThemeColorTypeMapper.map(it) }
    val colorFilter = if (tintColor != null) {
        ColorFilter.tint(color = tintColor)
    } else {
        null
    }

    Image(
        render = render,
        image = icon,
        modifier = modifier
            .conditional(height != null) { height(height!!.dp) }
            .conditional(width != null) { width(width!!.dp) },
        colorFilter = colorFilter,
    )
}

@Composable
fun MenuItemImage(
    render: Render,
    modifier: Modifier,
    menuItemImage: MenuItemImage,
) {
    val imageViewState = menuItemImage.imageViewState
    val onClick = menuItemImage.onClick
    val shape = menuItemImage.imageViewState.viewSpec.shapeSpec?.let {
        render.shapeMapperComposable.map(shapeSpec = it)
    }

    Image(
        render,
        viewState = imageViewState,
        modifier = modifier
            .conditional(shape != null) { clip(shape!!) }
            .conditional(onClick != null) { clickable(render) { onClick?.invoke() } },
    )
}

@Composable
fun MenuItemLabel(
    render: Render,
    modifier: Modifier,
    menuItemLabel: MenuItemLabel,
) {
    val text = menuItemLabel.text
    val width = menuItemLabel.width
    val height = menuItemLabel.height
    val onClick = menuItemLabel.onClick?.onClick
    val contentAlignment = menuItemLabel.contentAlignment.composeAlignment
    val minStyle = menuItemLabel.minTextStyle?.let { TextStyleMapper.map(it) }
    val autoResize = menuItemLabel.autoResize
    val fontSizeRange = if (autoResize) {
        requireNotNull(minStyle)
        throw NotImplementedError("Auto resize not supported with Text")
//        FontSizeRange(minStyle, style)
    } else {
        null
    }

    Box(
        modifier = modifier
            .clickableIfNotNull(render,onClick)
            .widthAx(width)
            .heightAx(height),
        contentAlignment = contentAlignment,
    ) {
//        if (autoResize) {
//            requireNotNull(fontSizeRange)
//            require(useMarquee.not()) { "Marquee not supported with auto resize" }
//            require(textAlign == null) { "Text align not supported with auto resize" }
//            AutoResizeText(
//                render,
//                text = label,
//                fontSizeRange = fontSizeRange,
//                style = style,
//                color = color,
//                textAlign = TextAlign.Center,
//                fontWeight = fontWeight,
//                maxLines = maxLines,
//                modifier = Modifier.widthAx(width),
//            )
//        } else {
        Text(text)
//        }
    }
}

@Composable
fun MenuItemDivider(
    modifier: Modifier,
    menuItemDivider: MenuItemDivider,
) {
    Divider(
        modifier,
    )
}

@Composable
fun MenuItemSpacer(
    modifier: Modifier,
    menuItemSpacer: MenuItemSpacer,
) {
    val width = menuItemSpacer.width
    val height = menuItemSpacer.height

    Spacer(
        modifier = modifier
            .conditional(width != null) { width(width!!.dp) }
            .conditional(height != null) { height(height!!.dp) },
    )
}

@Composable
fun MenuItemPopup(
    render: Render,
    modifier: Modifier,
    menuItemPopup: MenuItemPopup,
) {
    var showPopup = menuItemPopup.showPopup

    Box {
        MenuItem(render, modifier = modifier, menuItem = menuItemPopup.menuItem)

        DropdownMenu(
            expanded = showPopup,
            onDismissRequest = { showPopup = false },
        ) {
            MenuItemGroup(render, menuItemGroup = menuItemPopup.menuItemGroup)
        }
    }
}

//@Composable
//fun MenuItemCustom(
//    modifier: Modifier,
//    menuItemCustom: MenuItemCustom,
//) {
//    menuItemCustom.content(modifier, menuItemCustom)
//}

@Composable
fun MenuItemGroupHorizontal(
    render: Render,
    modifier: Modifier,
    menuItemGroupHorizontal: MenuItemGroupHorizontal,
) {
    val items = menuItemGroupHorizontal.menuItems
    val height = menuItemGroupHorizontal.height
    val width = menuItemGroupHorizontal.width?.dp

    Box(
        modifier = modifier
            .semantics(mergeDescendants = true) {}
            .conditional(width != null) {
                width(width!!)
            }
            .height(height),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState()),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            items.forEach {
                MenuItem(render, menuItem = it)
            }
        }
    }
}

@Composable
fun MenuItemGroupVertical(
    render: Render,
    modifier: Modifier,
    menuItemGroupVertical: MenuItemGroupVertical,
) {
    val items = menuItemGroupVertical.menuItems
    val width = menuItemGroupVertical.width
    val height = menuItemGroupVertical.height

    Box(
        modifier = modifier
            .ifNonNull(width) { this.width(width!!.dp) }
            .ifNonNull(height) { this.height(height!!.dp) },
        contentAlignment = Alignment.Center,
    ) {
        Column {
            items.forEach {
                MenuItem(render, menuItem = it)
            }
        }
    }
}

@Composable
fun MenuItem(
    render: Render,
    menuItemButton: MenuItemViewState,
    modifier: Modifier,
) {
    val menuItems = menuItemButton.menuItems
    val itemPadding = menuItemButton.itemPadding
    val centerItems = menuItemButton.centerItems
    if (menuItems.isEmpty()) return

    Box(
        modifier = modifier
            .padding(itemPadding),
        contentAlignment = if (centerItems) {
            Alignment.Center
        } else {
            Alignment.TopStart
        },
    ) {
        if (menuItems.size == 1) {
            MenuItem(
                render = render,
                modifier = Modifier,
                menuItem = menuItems.first(),
            )
        } else {
            Column {
                menuItems.forEach {
                    MenuItem(
                        render = render,
                        modifier = Modifier,
                        menuItem = it,
                    )
                }
            }
        }
    }
}


@Composable
fun MenuItemProgressButton(
    render: Render,
    modifier: Modifier,
    menuItemButton: MenuItemProgressButton,
    snapshot: AnimatedSnapshot? = null,
) {
    val overlayText = menuItemButton.overlayText
    val progress = menuItemButton.progress
    val height = menuItemButton.height
    val onClick =  menuItemButton.onClick?.onClick ?: ViewEventHandler.NoOp.onClick
    val backgroundColor = ThemeColorTypeMapper.map(ColorToken.ThemeTertiary)
    val progressColor = ThemeColorTypeMapper.map(ColorToken.ThemeSecondary)

    Button(
        render = render,
        onClick = onClick,
        modifier = modifier
            .ifNonNull(height) { this.height(it.dp) }
            .conditional(
                condition = false,
                ifTrue = { this },
                ifFalse = { clickableNoRipple { } },
            ),
        colors = ButtonDefaults.buttonColors().copy(
            containerColor = backgroundColor
        ),
        shape = menuItemButton.shapeSpec?.let {
            render.shapeMapperComposable.map(it)
        } ?: AppShapes.CutCorners.medium,
        contentPadding = PaddingValues(0.dp),
    ) {
        Box(Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(progress)
                    .background(
                        color = progressColor
                    ),
            )
            Text(overlayText, modifier.fillMaxWidth().align(Alignment.Center))
        }
    }
}

@Composable
fun MenuItemViewStateWrapper(
    render: Render,
    modifier: Modifier,
    menuItemViewStateWrapper: MenuItem.MenuItemViewStateWrapper<*>,
) {
    val viewState = menuItemViewStateWrapper.viewState
    val width = menuItemViewStateWrapper.width?.dp
    val height = menuItemViewStateWrapper.height?.dp

    Box(
        modifier = modifier
            .conditional(width != null) { this.width(width!!) }
            .conditional(height != null) { this.height(height!!) },
    ) {
        View(
            render = render,
            view = View(viewState),
            modifier = modifier,
        )
    }
}
