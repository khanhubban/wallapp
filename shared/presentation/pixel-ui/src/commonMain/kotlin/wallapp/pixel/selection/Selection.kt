package wallapp.pixel.selection

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import wallapp.pixel.button.Button
import wallapp.pixel.checkbox.BasicCheckbox
import wallapp.pixel.clickable.clickable
import wallapp.pixel.compose.conditional
import wallapp.pixel.menu.MenuItem
import wallapp.pixel.render.Render
import wallapp.pixel.render.shapeMapperComposable
import wallapp.pixel.shape.ShapeSpec

@Composable
fun Selection(
    render: Render,
    viewState: SelectionViewState,
    modifier: Modifier = Modifier,
) {
    val menuItem = viewState.menuItem
    val selected = viewState.selected

    val onCheckedChange = { checked: Boolean ->
        viewState.eventSink(SelectionViewEvent(viewState.key, checked))
    }

    when (viewState.selectionViewStyle) {
        SelectionViewStyle.Radio -> SelectionRow(
            render,
            menuItem,
            selected,
            onCheckedChange,
            viewState.shape,
            modifier,
            viewState.centerHorizontally,
        )

        SelectionViewStyle.Button -> SelectionButton(
            render,
            menuItem,
            selected,
            onCheckedChange,
            viewState.shape,
            modifier,
        )
    }

}


@Composable
fun SelectionButton(
    render: Render,
    menuItem: MenuItem,
    selected: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    shapeSpec: ShapeSpec,
    modifier: Modifier = Modifier,
) {
    val paddingDefault = render.defaultViewSpec.paddingDefault / 2

    val buttonPadding = PaddingValues(
        horizontal = paddingDefault / 2,
        vertical = paddingDefault / 2,
    )
    val shape = render.shapeMapperComposable.map(shapeSpec)!!

    val color = MaterialTheme.colorScheme.background
    val onColor = MaterialTheme.colorScheme.onBackground

    val buttonColors: ButtonColors = if (selected) {
        ButtonDefaults.buttonColors()
    } else {
        ButtonDefaults.buttonColors(
            containerColor = color,
            contentColor = onColor,
        )
    }

    Button(
        render = render,
        onClick = { onCheckedChange(!selected) },
        shape = shape,
        contentPadding = buttonPadding,
        colors = buttonColors,
        modifier = modifier,
    ) {
        MenuItem(
            render = render,
            menuItem = menuItem,
        )
    }

}

@Composable
private fun SelectionRow(
    render: Render,
    menuItem: MenuItem,
    selected: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    shape: ShapeSpec,
    modifier: Modifier = Modifier,
    centerHorizontally: Boolean,
) {
    val paddingDefault = render.defaultViewSpec.paddingDefault / 2

    Row(
        modifier = modifier
            .clickable(render) { onCheckedChange(!selected) },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (centerHorizontally) {
            Spacer(modifier = Modifier.weight(1f))
        } else {
            Spacer(modifier = Modifier.width(paddingDefault))
        }

        BasicCheckbox(
            render = render,
            checked = selected,
            onCheckedChange = { onCheckedChange(it) },
            shape = render.shapeMapperComposable.map(shape)!!,
            applyPadding = false,
        )

        Spacer(modifier = Modifier.width(paddingDefault))

        MenuItem(
            render = render,
            menuItem = menuItem,
            modifier = Modifier.conditional(!centerHorizontally) {
                weight(1f)
            },
        )

        if (centerHorizontally) {
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

