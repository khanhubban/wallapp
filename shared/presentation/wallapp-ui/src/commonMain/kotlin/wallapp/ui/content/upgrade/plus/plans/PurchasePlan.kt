package wallapp.ui.content.upgrade.plus.plans

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import wallapp.content.state.upgrade.plus.plans.SubscriptionPlanViewState
import wallapp.pixel.button.Button
import wallapp.pixel.button.ButtonAppearance
import wallapp.pixel.menu.MenuItem
import wallapp.pixel.render.Render
import wallapp.pixel.render.shapeMapperComposable
import wallapp.pixel.text.Text
import wallapp.pixel.theme.ThemeColorTypeMapper
import wallapp.theme.ColorToken

@Composable
fun PurchasePlanOutline(
    render: Render,
    viewState: SubscriptionPlanViewState,
    modifier: Modifier = Modifier,
    height: Dp = 84.dp,
    highlightHeight: Dp = 24.dp,
) {
    val paddingDefault = render.defaultViewSpec.paddingDefault
    val highlight = viewState.highlight
    val price: Text = viewState.price
    val isSelected = viewState.isSelected
    val onClick = { viewState.eventSink(viewState.event) }
    val shape = render.shapeMapperComposable.map(viewState.shapeSpec)!!

    Box(
        modifier = modifier
            .height(height)
            .clip(shape)
    ) {
        PlanButtonOutline(
            render = render,
            onClick = onClick,
            shape = shape,
            isSelected = isSelected,
            contentPadding = PaddingValues(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = paddingDefault),
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Spacer(Modifier.weight(1f))
                    MenuItem(render, viewState.title)

                    Spacer(modifier = Modifier.height(paddingDefault / 4))

                    Text(text = price)
                    Spacer(Modifier.weight(1f))
                }
                if (highlight != null) {
                    PurchaseButtonHighlightFill(
                        text = highlight,
                        isSelected = isSelected,
                        modifier = Modifier
                            .height(highlightHeight)
                            .align(Alignment.TopEnd),
                    )
                }
            }
        }
    }
}

@Composable
fun PurchaseButtonHighlight(
    text: Text,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.extraSmall,
    contentColor: Color = if (!isSelected) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurface
    },
    backgroundColor: Color = if (!isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surface
    },
) {
    Row(
        modifier = modifier
            .clip(shape)
            .border(1.dp, color = MaterialTheme.colorScheme.outline, shape)
            .background(backgroundColor),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = text,
            modifier = Modifier
                .padding(horizontal = 8.dp),
            colorOverride = contentColor,
        )
    }
}

@Composable
fun PurchaseButtonHighlightFill(
    text: Text,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.medium,
    backgroundColor: ColorToken = if (isSelected) {
        ColorToken.ThemeSecondary
    } else {
        ColorToken.ThemeOnPrimary
    },
) {
    Row(
        modifier = modifier
            .clip(shape)
            .background(ThemeColorTypeMapper.map(backgroundColor)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = text,
            modifier = Modifier
                .padding(horizontal = 8.dp),
        )
    }
}

@Composable
fun PlanButtonOutline(
    render: Render,
    onClick: () -> Unit,
    isSelected: Boolean,
    shape: Shape,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    content: @Composable () -> Unit,
) {
    val (buttonAppearance) = if (isSelected) {
        ButtonAppearance.Default to null
    } else {
        ButtonAppearance.Outline to ColorToken.ThemeOnSurfaceVariant
    }

    val (containerColorToken, outLineColorToken) = if (isSelected) {
        null to null
    } else {
        ColorToken.ThemeOnPrimary to ColorToken.ThemeOnSurfaceVariant
    }

    Button(
        render = render,
        onClick = onClick,
        buttonAppearance = buttonAppearance,
        shape = shape,
        outlineColor = ThemeColorTypeMapper.map(outLineColorToken),
        containerColor = ThemeColorTypeMapper.map(containerColorToken),
        modifier = modifier,
        contentPadding = contentPadding,
    ) {
        content()
    }
}