package wallapp.ui.content.button

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import me.onebone.toolbar.CollapsingToolbarScope
import wallapp.graphics.color.contrastingColor
import wallapp.pixel.animation.AnimatedSnapshot
import wallapp.pixel.animation.AnimatedViewSpec
import wallapp.pixel.animation.animate
import wallapp.pixel.animation.animateSnapshot
import wallapp.pixel.button.Button
import wallapp.pixel.button.ButtonViewState
import wallapp.pixel.compose.clickableNoRipple
import wallapp.pixel.compose.conditional
import wallapp.pixel.compose.ifNonNull
import wallapp.pixel.menu.MenuItem
import wallapp.pixel.render.Render
import wallapp.pixel.theme.ThemeColorTypeMapper
import wallapp.pixel.toolbar.CollapsingToolbarContent
import wallapp.pixel.view.onClick

@Composable
fun CollapsingToolbarScope.FollowButton(
    render: Render,
    progress: Float,
    button: ButtonViewState,
    animatedViewSpec: AnimatedViewSpec,
    paddingDefault: Dp,
    modifier: Modifier = Modifier,
) {
    CollapsingToolbarContent(
        render,
        modifier = modifier,
        animatedViewSpec = animatedViewSpec,
        progress = progress,
    ) { mod, snapshot ->
        FollowButton(render, button, snapshot, paddingDefault, mod)
    }
}


@Composable
fun FollowButton(
    render: Render,
    button: ButtonViewState,
    animatedSnapshot: AnimatedSnapshot,
    paddingDefault: Dp,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
    ) {
        FollowButton(
            render,
            button,
            modifier = Modifier
                .align(Alignment.Center),
            paddingDefault = paddingDefault,
            animationProgress = animatedSnapshot.progress,
            contentPadding = PaddingValues(0.dp),
        )
    }
}

@Composable
fun FollowButton(
    render: Render,
    viewState: ButtonViewState,
    modifier: Modifier = Modifier,
    animationProgress: Float,
    paddingDefault: Dp,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
) {
    val menuItem = viewState.menuItem
    require(menuItem is MenuItem.MenuItemGroup)
    val menuItems = menuItem.menuItems
    val (image, label) = if (menuItems.size == 1) {
        null to (menuItems[0] as MenuItem.MenuItemLabel)
    } else {
        (menuItems[0] as MenuItem.MenuItemIcon) to (menuItems[1] as MenuItem.MenuItemLabel)
    }

    val onClick = viewState.eventHandler?.onClick

    val snapshot = viewState.animatedViewSpec?.animate(render, animationProgress)

    val containerColorTarget = ThemeColorTypeMapper.map(viewState.containerColorToken)!!
    val containerColor by animateColorAsState(containerColorTarget)
    val contentColorTarget = containerColorTarget.contrastingColor
    val contentColor by animateColorAsState(contentColorTarget)

    val buttonColors = ButtonDefaults.buttonColors(
        containerColor = containerColor,
        contentColor = contentColor,
    )

    Button(
        render = render,
        onClick = onClick ?: {},
        modifier = modifier
            .ifNonNull(snapshot) { animateSnapshot(it) }
            .conditional(
                condition = onClick != null,
                ifTrue = { this },
                ifFalse = { clickableNoRipple { } },
            ),
        shape = snapshot?.shape ?: ButtonDefaults.shape,
        contentPadding = contentPadding,
        colors = buttonColors,
    ) {
        ButtonContent(render, animationProgress, image, label, paddingDefault)
    }
}

@Composable
private fun RowScope.ButtonContent(
    render: Render,
    animationProgress: Float,
    image: MenuItem?,
    label: MenuItem,
    paddingDefault: Dp,
) {
    val middlePadding = (paddingDefault / 2) + (paddingDefault * animationProgress * 1.25f)

    Spacer(modifier = Modifier.weight(1f))

    if (image != null) {
        MenuItem(
            render = render,
            menuItem = image,
        )

        Spacer(modifier = Modifier.width(middlePadding))
    }

    MenuItem(
        render = render,
        menuItem = label,
    )

    Spacer(modifier = Modifier.weight(1f))
}
