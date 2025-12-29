package wallapp.pixel.toolbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.MutableWindowInsets
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import wallapp.pixel.menu.MenuItem
import wallapp.pixel.render.Render
import wallapp.pixel.theme.ThemeColorTypeMapper
import wallapp.pixel.view.View
import wallapp.pixel.view.ViewState

@Composable
fun Toolbar(
    render: Render,
    toolbar: ViewState,
    modifier: Modifier = Modifier,
    useDefaultWindowInsets: Boolean = true,
) {
    when (toolbar) {
        is ToolbarViewState -> {
            Toolbar(
                render = render,
                toolbar = toolbar,
                modifier = modifier,
                useDefaultWindowInsets = useDefaultWindowInsets,
            )
        }
        else -> {
            View(
                render = render,
                view = View(toolbar),
                modifier = modifier,
            )
        }
    }
}

/**
 * [useDefaultWindowInsets]: set this to [false] if the toolbar is not intended to be placed on a
 * view that respects the status bar insets.
 * [useSimpleToolbar]: iOS is exhibiting a strange issue with toolbar rendering. Work around with a
 * simpler, custom implementation. See #33.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun Toolbar(
    render: Render,
    toolbar: ToolbarViewState,
    modifier: Modifier = Modifier,
    useDefaultWindowInsets: Boolean = true,
    useSimpleToolbar: Boolean = true,
) {
    val height = toolbar.height
    val navigationIcon = toolbar.navigationIcon
    val title = toolbar.title
    val actionItems = toolbar.actionItems
    val actionItemSize = 48.dp
    val centeredTitle = toolbar.centeredTitle
    val containerColor: Color = ThemeColorTypeMapper.map(toolbar.containerColorOverride)
    val windowInsets = if (useDefaultWindowInsets) {
        TopAppBarDefaults.windowInsets
    } else {
        MutableWindowInsets()
    }

    val navigationIconComposable = @Composable {
        if (navigationIcon != null) {
            Box(
                modifier = Modifier
                    .height(height),
                contentAlignment = Alignment.Center,
            ) {
                MenuItem(render, navigationIcon)
            }
        }
    }

    val titleComposable = @Composable {
        if (title != null) {
            Box(
                modifier = Modifier
                    .height(height),
                contentAlignment = if (centeredTitle) { Alignment.Center } else { Alignment.CenterStart }
            ) {
                MenuItem(render, title)
            }
        }
    }

    val actions: @Composable() (RowScope.() -> Unit) = {
        if (actionItems != null) {
            Row(
                modifier = Modifier
                    .height(height),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                actionItems.forEach {
                    MenuItem(
                        render,
                        it,
                        modifier = Modifier.size(actionItemSize),
                    )
                }
            }
        }

//            if (overflowItems != null) {
//                OverflowMenu {
//                    Column(
//                        modifier = Modifier.width(168.dp)
//                    ) {
//                        overflowItems.forEach {
//                            MenuItem(menuItem = it)
//                        }
//                    }
//                }
//            } else {
        Spacer(modifier = Modifier.width(8.dp))
//            }
    }

    Toolbar(
        render,
        useSimpleToolbar = useSimpleToolbar,
        centeredTitle = centeredTitle,
        containerColor,
        windowInsets,
        modifier = modifier
            .height(height),
        navigationIconComposable,
        titleComposable,
        actions,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Toolbar(
    render: Render,
    useSimpleToolbar: Boolean,
    centeredTitle: Boolean,
    containerColor: Color,
    windowInsets: WindowInsets,
    modifier: Modifier = Modifier,
    navigationIconComposable: @Composable () -> Unit = {},
    titleComposable: @Composable () -> Unit = {},
    actions: @Composable() (RowScope.() -> Unit) = {},
) {
    val colors = TopAppBarDefaults
        .centerAlignedTopAppBarColors(containerColor = containerColor)

    if (centeredTitle) {
        if (useSimpleToolbar) {
            CenterAlignedTopAppBarSimple(
                render,
                containerColor = containerColor,
                modifier = modifier,
                navigationIconComposable = navigationIconComposable,
                titleComposable = titleComposable,
                actions = actions,
            )
        } else {
            CenterAlignedTopAppBar(
                modifier = modifier,
                colors = colors,
                windowInsets = windowInsets,
                navigationIcon = navigationIconComposable,
                title = titleComposable,
                actions = actions,
            )
        }
    } else {
        TopAppBar(
            modifier = modifier,
            colors = colors,
            windowInsets = windowInsets,
            navigationIcon = navigationIconComposable,
            title = titleComposable,
            actions = actions,
        )
    }
}

@Composable
fun CenterAlignedTopAppBarSimple(
    render: Render,
    containerColor: Color,
    modifier: Modifier = Modifier,
    navigationIconComposable: @Composable () -> Unit = {},
    titleComposable: @Composable () -> Unit = {},
    actions: @Composable() (RowScope.() -> Unit) = {},
) {
    val paddingDefault = render.defaultViewSpec.paddingDefault
    val endPadding = paddingDefault / 4

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(containerColor),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .padding(start = endPadding)
                .align(Alignment.CenterStart),
        ) {
            navigationIconComposable()
        }

        Box(
            modifier = Modifier
                .fillMaxHeight()
                .align(Alignment.Center),
        ) {
            titleComposable()
        }

        Row(
            modifier = Modifier
                .fillMaxHeight()
                .padding(end = endPadding)
                .align(Alignment.CenterEnd),
        ) {
            actions()
        }
    }
}