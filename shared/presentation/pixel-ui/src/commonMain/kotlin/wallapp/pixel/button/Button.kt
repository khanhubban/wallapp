package wallapp.pixel.button

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import wallapp.image.Image
import wallapp.pixel.animation.animate
import wallapp.pixel.animation.animateSnapshot
import wallapp.pixel.clickable.clickable
import wallapp.pixel.compose.clickableNoRipple
import wallapp.pixel.compose.conditional
import wallapp.pixel.compose.ifNonNull
import wallapp.pixel.menu.MenuItem
import wallapp.pixel.render.Render
import wallapp.pixel.render.shapeMapperComposable
import wallapp.pixel.theme.ThemeColorTypeMapper
import wallapp.pixel.view.OnClickNoOp
import wallapp.pixel.view.onClick

@Composable
fun Button(
    render: Render,
    image: Image,
    onClick: (() -> Unit),
    modifier: Modifier = Modifier,
    shape: Shape = CircleShape,
    colors: ButtonColors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
    ),
    border: BorderStroke? = null,
) {
    Button(
        render = render,
        onClick = { onClick.invoke() },
        colors = colors,
        modifier = modifier,
        contentPadding = PaddingValues(0.dp),
        shape = shape,
        border = border,
    ) {
        Image(render, image)
    }
}

@Composable
fun Button(
    render: Render,
    viewState: ButtonViewState,
    modifier: Modifier = Modifier,
    animationProgress: Float? = 1f,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
) {
    val menuItem = viewState.menuItem
    val onClick = viewState.eventHandler?.onClick

    val animatedViewSpec = viewState.animatedViewSpec
    val snapshot = if (animatedViewSpec != null && animationProgress != null) {
        animatedViewSpec.animate(render, animationProgress)
    } else {
        null
    }
    val shape = requireNotNull(
        snapshot?.shape ?: viewState.shapeSpec?.let { render.shapeMapperComposable.map(it) }
    ) {
        "Must set shape for button, either through `animatedViewSpec` or `shapeSpec`"
    }
    val containerColor = viewState.containerColorToken?.let { ThemeColorTypeMapper.map(it) }

    Button(
        render = render,
        onClick = onClick,
        buttonAppearance = viewState.buttonAppearance,
        containerColor = containerColor,
        shape = shape,
        modifier = modifier
            .ifNonNull(snapshot) { animateSnapshot(it) }
            .conditional(
                condition = onClick != null,
                ifTrue = { this },
                ifFalse = { clickableNoRipple { } },
            ),
        contentPadding = contentPadding,
    ) {
        MenuItem(render, menuItem)
    }
}

@Composable
fun Button(
    render: Render,
    onClick: (() -> Unit)?,
    buttonAppearance: ButtonAppearance,
    shape: Shape,
    containerColor: Color?,
    outlineColor: Color? = null,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    content: @Composable () -> Unit,
) {
    val colors = ButtonDefaults.buttonColors(
        disabledContainerColor = MaterialTheme.colorScheme.background,
    )

    when (buttonAppearance) {
        ButtonAppearance.None -> {
            TextButton(
                render = render,
                onClick = onClick ?: OnClickNoOp,
                modifier = modifier,
                shape = shape,
                contentPadding = contentPadding,
            ) {
                content()
            }
        }

        ButtonAppearance.Default -> {
            Button(
                render = render,
                onClick = onClick ?: OnClickNoOp,
                modifier = modifier,
                shape = shape,
                contentPadding = contentPadding,
                colors = colors,
            ) {
                content()
            }
        }

        ButtonAppearance.Highlight -> {
            Button(
                render = render,
                onClick = onClick ?: OnClickNoOp,
                modifier = modifier,
                shape = shape,
                contentPadding = contentPadding,
                colors = ButtonDefaults.buttonColors(containerColor = requireNotNull(containerColor)),
            ) {
                content()
            }
        }

        ButtonAppearance.Disabled -> {
            Button(
                render = render,
                onClick = onClick ?: OnClickNoOp,
                modifier = modifier,
                shape = shape,
                contentPadding = contentPadding,
                enabled = false,
                isClickable = false,
                colors = colors,
            ) {
                content()
            }
        }

        ButtonAppearance.DisabledWithClick -> {
            Button(
                render = render,
                onClick = onClick ?: OnClickNoOp,
                modifier = modifier,
                shape = shape,
                contentPadding = contentPadding,
                enabled = false,
                colors = colors,
            ) {
                content()
            }
        }

        ButtonAppearance.Outline -> {
            val outlineBorder = BorderStroke(
                width = 2.dp,
                color = requireNotNull(outlineColor) { "Must set outlineColor for outline button" },
            )
            val outlineColors = if (containerColor != null) {
                ButtonDefaults.outlinedButtonColors(containerColor = containerColor)
            } else {
                ButtonDefaults.outlinedButtonColors()
            }
            Button(
                render = render,
                onClick = onClick ?: OnClickNoOp,
                modifier = modifier,
                shape = shape,
                border = outlineBorder,
                colors = outlineColors,
                contentPadding = contentPadding,
            ) {
                content()
            }
        }
    }
}


@Composable
fun Button(
    render: Render,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape,
    contentPadding: PaddingValues,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    enabled: Boolean = true,
    isClickable: Boolean = true,
    border: BorderStroke? = null,
    content: @Composable RowScope.() -> Unit,
) {
    val containerColor = if (enabled) colors.containerColor else colors.disabledContainerColor
    val contentColor = if (enabled) colors.contentColor else colors.disabledContentColor

    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(shape)
            .background(containerColor)
            .clickable(render, if (isClickable) onClick else OnClickNoOp)
            .ifNonNull(border) { border(it, shape) }
            .padding(contentPadding),
        contentAlignment = Alignment.Center,
    ) {
        CompositionLocalProvider(LocalContentColor provides contentColor) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                content = content,
            )
        }
    }
}

@Composable
internal fun TextButton(
    render: Render,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = ButtonDefaults.textShape,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    content: @Composable () -> Unit
) {

    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(shape)
            .clickable(render, onClick)
            .padding(contentPadding),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}
