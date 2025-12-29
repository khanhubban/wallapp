package wallapp.ui.content.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.valentinilk.shimmer.shimmer
import wallapp.content.state.settings.SettingAppIconViewState
import wallapp.content.state.settings.SettingViewState
import wallapp.content.state.settings.SettingViewState.ItemPreviewRowViewState
import wallapp.content.state.settings.SettingViewState.ItemPreviewStyle
import wallapp.content.state.settings.SettingViewState.ItemPreviewViewState
import wallapp.image.Image
import wallapp.pixel.button.Button
import wallapp.pixel.button.ButtonStyle
import wallapp.pixel.clickable.ClickableEffect
import wallapp.pixel.clickable.clickable
import wallapp.pixel.compose.clickableIfNotNull
import wallapp.pixel.compose.collectAsState
import wallapp.pixel.compose.conditional
import wallapp.pixel.compose.scale
import wallapp.pixel.render.Render
import wallapp.pixel.render.shapeMapperComposable
import wallapp.pixel.shape.ShapeStyle
import wallapp.pixel.text.Text
import wallapp.pixel.text.Text.Companion.presetText
import wallapp.pixel.view.View
import wallapp.pixel.view.onClick
import wallapp.ui.content.search.EdgeFade

@Composable
fun Setting(
    render: Render,
    viewState: SettingViewState,
    modifier: Modifier = Modifier,
) {
    val paddingHorizontal = 32.dp
    val paddingVertical = 8.dp
    val defaultInsetPadding = PaddingValues(
        horizontal = paddingHorizontal,
        vertical = paddingVertical
    )
    val headingInsetPadding = PaddingValues(
        horizontal = paddingHorizontal,
        vertical = paddingVertical
    )

    when (viewState) {
        is SettingViewState.Divider -> SettingDivider(viewState, modifier)
        is SettingViewState.Footer -> SettingsFooter(viewState, modifier)
        is SettingViewState.Heading -> {
            SettingHeading(viewState, headingInsetPadding, modifier)
        }

        is SettingViewState.Spacer -> SettingSpacer(viewState, modifier)
        is SettingViewState.Switch -> {
            SettingSwitch(render, viewState, defaultInsetPadding, modifier)
        }

        is SettingViewState.SwitchMutable -> {
            SettingSwitchMutable(render, viewState, defaultInsetPadding, modifier)
        }

        is SettingViewState.Detail -> {
            SettingDetail(
                render,
                viewState,
                defaultInsetPadding,
                modifier = modifier
            )
        }

        is ItemPreviewRowViewState -> {
            SettingItemPreviewRow(
                render,
                viewState,
                modifier = modifier,
            )
        }

        is SettingViewState.ViewStateWrapper ->
            SettingViewStateWrapper(
                render,
                viewState,
                modifier,
            )
    }
}

@Composable
fun SettingSwitch(
    render: Render,
    viewState: SettingViewState.Switch,
    paddingValues: PaddingValues,
    modifier: Modifier = Modifier,
) {
    val checked = viewState.checked
    val onClick = viewState.onClicked

    SettingSwitch(
        render = render,
        modifier = modifier,
        paddingValues = paddingValues,
        checked = checked,
        onCheckedChange = viewState.onCheckedChange,
        onClick = onClick,
        icon = viewState.icon,
        showShimmer = viewState.showShimmer,
//        iconTint = switch.iconTint,
//        iconBackground = switch.iconBackground,
//        iconBackgroundDark = switch.iconBackgroundDark,
        title = viewState.title,
        summary = viewState.summary,
        switchContentDescription = viewState.switchContentDescription,
    )
}

@Composable
fun SettingSwitchMutable(
    render: Render,
    viewState: SettingViewState.SwitchMutable,
    paddingValues: PaddingValues,
    modifier: Modifier = Modifier,
) {
    val stateFlow = viewState.mutableStateFlow
    val checked = stateFlow.collectAsState()

    SettingSwitch(
        render = render,
        modifier = modifier,
        paddingValues = paddingValues,
        checked = checked.value,
        onCheckedChange = viewState.onCheckedChange,
        onClick = viewState.onClicked,
        icon = viewState.icon,
//        iconTint = switch.iconTint,
//        iconBackground = switch.iconBackground,
//        iconBackgroundDark = switch.iconBackgroundDark,
        title = viewState.title,
        summary = viewState.summary,
        switchContentDescription = viewState.switchContentDescription,
    )
}

@Composable
fun SettingSwitch(
    render: Render,
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(0.dp),
    checked: Boolean = true,
    showShimmer: Boolean = false,
    onCheckedChange: ((Boolean) -> Unit)? = null,
    onClick: () -> Unit = {},
    icon: Image? = null,
//    iconTint: Color = Colors.Blue,
//    iconBackground: Color = Colors.BlueBackgroundLightTheme,
//    iconBackgroundDark: Color = Colors.BlueBackgroundDarkTheme,
    title: Text = "Title".presetText,
    summary: Text? = "Here is a message that wraps over a few lines".presetText,
    switchContentDescription: String? = null,
) {
    Column(
        modifier = modifier
            .clickable(render) { onClick.invoke() }
            .conditional(showShimmer) { shimmer() }
            .padding(paddingValues),
        verticalArrangement = Arrangement.Center,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f),
            ) {
                Text(text = title)
                if (summary != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(summary)
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                modifier = Modifier.semantics {
                    contentDescription = switchContentDescription.orEmpty()
                }
            )
        }
    }
}

@Composable
fun SettingDivider(
    viewState: SettingViewState.Divider,
    modifier: Modifier = Modifier,
) {
    SettingDivider(
        modifier = modifier,
        inset = viewState.inset,
    )
}

@Composable
fun SettingDivider(
    modifier: Modifier = Modifier,
    inset: Dp = 0.dp,
) {
    Divider(
        modifier = modifier.padding(start = inset)
    )
}

@Composable
fun SettingsFooter(
    viewState: SettingViewState.Footer,
    modifier: Modifier = Modifier,
) {
    SettingsFooter(
        modifier = modifier,
        messages = viewState.messages,
    )
}

@Composable
fun SettingsFooter(
    modifier: Modifier = Modifier,
    messages: List<Text> = listOf("Demo label goes here".presetText),
) {
    Column(
        modifier,
    ) {
        Spacer(modifier = Modifier.weight(1f))
        CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
            messages.forEach { message ->
                Text(
                    text = message,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
fun SettingHeading(
    viewState: SettingViewState.Heading,
    paddingValues: PaddingValues,
    modifier: Modifier = Modifier,
) {
    SettingViewStateHeadingContent(
        modifier = modifier,
        title = viewState.title,
        paddingValues = paddingValues,
    )
}

@Composable
fun SettingViewStateHeadingContent(
    title: Text,
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(0.dp),
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .padding(paddingValues)
            .fillMaxWidth(),
    ) {
        Text(text = title)
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
fun SettingDetail(
    render: Render,
    viewState: SettingViewState.Detail,
    paddingValues: PaddingValues,
    modifier: Modifier = Modifier,
) {
    val title = viewState.title
    val summary = viewState.summary
    val onClick = viewState.onClick?.onClick
    val minHeight = if (summary != null) {
        64.dp
    } else {
        56.dp
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .heightIn(min = minHeight)
            .clickableIfNotNull(render, onClick)
            .padding(paddingValues)
            .fillMaxWidth(),
    ) {
        if (summary != null) {
            Column {
                Text(text = title)
                Spacer(modifier = Modifier.height(4.dp))
                Text(summary)
            }
        } else {
            Text(text = title)
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
fun SettingSpacer(
    viewState: SettingViewState.Spacer,
    modifier: Modifier = Modifier,
) {
    SettingSpacer(
        modifier = modifier,
        height = viewState.height,
    )
}

@Composable
fun SettingSpacer(
    modifier: Modifier = Modifier,
    height: Dp = 40.dp,
) {
    Spacer(modifier = modifier.height(height))
}

@Composable
fun SettingViewStateWrapper(
    render: Render,
    viewState: SettingViewState.ViewStateWrapper,
    modifier: Modifier = Modifier,
) {
    View(render, View(viewState.viewState, null), modifier)
}

@Composable
fun SettingItemPreviewRow(
    render: Render,
    viewState: ItemPreviewRowViewState,
    modifier: Modifier = Modifier,
) {
    val paddingDefault = render.defaultViewSpec.paddingDefault
    val viewSpec = viewState.viewSpec
    val paddingHorizontal = viewSpec?.horizontalPadding ?: 0.dp
    val paddingVertical = viewSpec?.verticalPadding ?: 0.dp
    val itemSpacing = viewSpec?.itemSpacing?.dp ?: paddingDefault

    val title = viewState.title
    val itemPreviews = viewState.itemPreviews

    val scrolling = viewState.scrolling
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .padding(vertical = paddingVertical),
    ) {
        if (title != null) {
            Column(modifier = Modifier.fillMaxWidth()
                .padding(horizontal = paddingHorizontal)) {
                Text(text = title)
                Spacer(modifier = Modifier.height(paddingDefault))
            }
        }
        Box {
            Box(modifier = Modifier.padding(horizontal = paddingHorizontal / 2)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .conditional(scrolling) { this.horizontalScroll(scrollState) }
                        .padding(horizontal = paddingHorizontal / 2),
                ) {
                    itemPreviews.forEachIndexed { index, itemPreview ->
                        val itemViewSpec = itemPreview.viewSpec
                        val itemWidth = itemViewSpec.width
                        val itemHeight = itemViewSpec.height
                        val onClick = {
                            if (viewState.currentIndex != index) {
                                viewState.onSelectedChanged.invoke(index)
                            }
                        }

                        Box(
                            modifier = Modifier
                                .width(itemWidth)
                                .height(itemHeight),
                            contentAlignment = Alignment.Center,
                        ) {
                            SettingItemPreview(
                                render = render,
                                viewState = itemPreview,
                                isSelected = index == viewState.currentIndex,
                                onClick = onClick,
                                modifier = Modifier.fillMaxSize(),
                            )
                        }
                        if (index < itemPreviews.size - 1) {
                            if (scrolling) {
                                Spacer(modifier = Modifier.width(itemSpacing))
                            } else {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }

            val edgeFade = viewState.edgeFade
            if (edgeFade != null) {
                EdgeFade(
                    render = render,
                    edgeFade = edgeFade,
                    modifier = Modifier.fillMaxHeight()
                )
            }
        }
    }
}

@Composable
fun SettingItemPreview(
    render: Render,
    viewState: ItemPreviewViewState<Any>,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (viewState.style) {
        ItemPreviewStyle.Default -> {
            SettingItemPreviewDefault(
                render = render,
                viewState = viewState,
                isSelected = isSelected,
                onClick = onClick,
            )
        }

        ItemPreviewStyle.Button -> {
            SettingItemPreviewButton(
                render = render,
                viewState = viewState,
                isSelected = isSelected,
                onClick = onClick,
                modifier = modifier,
            )
        }
    }
}


@Composable
fun SettingItemPreviewButton(
    render: Render,
    viewState: ItemPreviewViewState<Any>,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val paddingDefault = render.defaultViewSpec.paddingDefault
    val buttonPadding = PaddingValues(
        horizontal = paddingDefault / 2,
        vertical = paddingDefault / 2,
    )
    val label = viewState.label!!
    val shape = render.shapeMapperComposable.map(viewState.shapeSpec)!!

    val color = MaterialTheme.colorScheme.background
    val onColor = MaterialTheme.colorScheme.onBackground

    val (contentColor, buttonStyle, colors) = if (isSelected) {
        Triple(
            color, ButtonStyle.Default, ButtonDefaults.buttonColors(
                containerColor = onColor,
                contentColor = color,
            )
        )
    } else {
        Triple(onColor, ButtonStyle.Outline, ButtonDefaults.outlinedButtonColors())
    }

    val border: BorderStroke? = when (buttonStyle) {
        ButtonStyle.Default -> null
        ButtonStyle.Outline -> ButtonDefaults.outlinedButtonBorder
    }
    val buttonColors: ButtonColors = when (buttonStyle) {
        ButtonStyle.Default -> ButtonDefaults.buttonColors()
        ButtonStyle.Outline -> ButtonDefaults.outlinedButtonColors()
    }

    Button(
        render = render,
        onClick = onClick,
        border = border,
        shape = shape,
        contentPadding = buttonPadding,
        colors = buttonColors,
        modifier = modifier,
    ) {
        Text(
            label,
            colorOverride = contentColor,
        )
    }
}

@Composable
fun SettingItemPreviewDefault(
    render: Render,
    viewState: ItemPreviewViewState<Any>,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val label = viewState.label
    val shape = render.shapeMapperComposable.map(viewState.shapeSpec)!!

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        SettingItemPreview(
            render = render,
            item = viewState.item,
            onClick = { onClick() },
            isSelected = isSelected,
            shape = shape,
        )
        if (label != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = label,
//                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
fun <T> SettingItemPreview(
    render: Render,
    item: T,
    onClick: () -> Unit,
    isSelected: Boolean,
    shape: Shape,
    selectedColor: Color = MaterialTheme.colorScheme.onBackground,
) {
    val insetPadding = render.defaultViewSpec.paddingDefault / 4
    val insetScale = shape.scale(.75f)

    Box(
        modifier = Modifier
            .clip(shape)
            .conditional(isSelected && item !is SettingAppIconViewState) { this.background(selectedColor) }
            .clickable(render) { onClick.invoke() },
        contentAlignment = Alignment.Center,
    ) {
        SettingItem(
            render = render,
            item = item,
            shape = insetScale,
            onClick = onClick,
            modifier = Modifier
                .padding(insetPadding)
                .align(Alignment.Center),
            isSelected = isSelected,
            showBorder = !isSelected,
        )
    }
}

@Composable
private fun <T> SettingItem(
    render: Render,
    item: T,
    shape: Shape,
    onClick: () -> Unit,
    isSelected: Boolean,
    showBorder: Boolean,
    modifier: Modifier = Modifier,
) {
    if (item is Image) {
        SettingItemImage(
            render = render,
            item = item,
            onClick = onClick,
            modifier = modifier,
            shape = shape,
            border = if (showBorder) { ButtonDefaults.outlinedButtonBorder } else { null },
        )
    } else if (item is ShapeStyle) {
        SettingItemShape(
            render = render,
            shape = shape,
            modifier = modifier,
        )
    } else if (item is SettingAppIconViewState) {
        SettingItemAppIcon(
            render = render,
            item = item,
            isSelected = isSelected,
            onClick = onClick,
            modifier = modifier,
        )
    }
}

@Composable
private fun SettingItemAppIcon(
    render: Render,
    item: SettingAppIconViewState,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val (borderImage, borderContentDescription) = if (isSelected) {
        item.appIconPreviewSelected to item.appIconPreviewSelected.contentDescription.toString().plus(" - ${item.wallAppIcon.contentDescription.toString()}")
    } else {
        item.appIconPreviewUnselected to item.wallAppIcon.contentDescription.toString()
    }
    val lockedImage = item.lockedImage

    Box(
        modifier = modifier,
    ) {
        Image(
            render = render,
            image = item.wallAppIcon,
            modifier = Modifier
                .fillMaxSize()
                .clickable(ClickableEffect.AlphaFade, onClick),
        )

        lockedImage?.also {
            Image(
                render = render,
                image = lockedImage,
                modifier = Modifier
                    .fillMaxSize(),
            )
        }

        borderImage?.also {
            Image(
                render = render,
                image = borderImage,
                modifier = Modifier.fillMaxSize().semantics {
                    contentDescription = borderContentDescription
                },
            )
        }
    }
}

@Composable
private fun SettingItemImage(
    render: Render,
    item: Image,
    shape: Shape,
    onClick: () -> Unit,
    border: BorderStroke?,
    modifier: Modifier = Modifier,
) {
    Button(
        render,
        image = item,
        onClick = onClick,
        modifier = modifier,
        shape = shape,
        border = border,
    )
}

@Composable
private fun SettingItemShape(
    render: Render,
    shape: Shape,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(shape)
            .background(MaterialTheme.colorScheme.onBackground),
        contentAlignment = Alignment.Center,
    ) {
    }
}