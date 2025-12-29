package wallapp.ui.content.search

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import wallapp.content.state.search.SearchBarViewState
import wallapp.content.state.search.SearchViewEvent
import wallapp.content.state.search.SearchViewEventSink
import wallapp.pixel.compose.BackHandler
import wallapp.pixel.font.TextStyleMapper
import wallapp.pixel.menu.MenuItem
import wallapp.pixel.render.Render
import wallapp.pixel.render.shapeMapperComposable
import wallapp.pixel.text.Text
import wallapp.pixel.theme.ThemeColorTypeMapper
import wallapp.pixel.util.keyboardAsState
import wallapp.pixel.view.ViewEventHandler

@Composable
fun SearchBar(
    render: Render,
    viewState: SearchBarViewState,
    height: Dp,
    overlayVisibilityProgress: Float,
    searchBarDisabled: Boolean,
    modifier: Modifier = Modifier,
) {
    val eventSink = viewState.eventSink
    val searchHint = viewState.searchHint
    val searchIcon = viewState.searchIcon
    val searchFieldTextStyle = TextStyleMapper.map(viewState.searchFieldTextStyle)
    val query = viewState.query
    val showClearIcon = viewState.showClearIcon
    val clearIcon = viewState.clearIcon
    val containerColor = ThemeColorTypeMapper.map(viewState.containerColor)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height),
        contentAlignment = Alignment.Center,
    ) {
        val recipeBinState = viewState.recipeBinViewState
        if (recipeBinState != null) {
            val viewSpec = recipeBinState.viewSpec
            SearchRecipeBinBar(
                render = render,
                viewState = recipeBinState,
                viewSpec = viewSpec,
                visibilityProgress = overlayVisibilityProgress,
                containerColor = containerColor,
            )
        } else {
            SearchBarText(
                render = render,
                query = query,
                searchHint = searchHint,
                eventSink = eventSink,
                modifier = Modifier
                    .fillMaxSize(),
                searchIcon = searchIcon,
                searchFieldTextStyle = searchFieldTextStyle,
                showClearIcon = showClearIcon,
                clearIcon = clearIcon,
                containerColor = if (searchBarDisabled) {
                    MaterialTheme.colorScheme.background
                } else {
                    containerColor
                },
                shape = render.shapeMapperComposable.map(viewState.searchInputViewShape)!!,
                disabled = searchBarDisabled,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchBarText(
    render: Render,
    query: String?,
    searchHint: Text,
    searchFieldTextStyle: TextStyle,
    eventSink: SearchViewEventSink,
    modifier: Modifier = Modifier,
    searchIcon: MenuItem,
    showClearIcon: Boolean,
    clearIcon: MenuItem,
    shape: Shape,
    containerColor: Color = MaterialTheme.colorScheme.background,
    disabled: Boolean = false, // disabled means the search bar will not use the query string to fill the text field
) {
    val paddingDefault = render.defaultViewSpec.paddingDefault
    val paddingLarge = render.defaultViewSpec.paddingLarge

    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    var focusState: FocusState? by remember { mutableStateOf(null) }
    var textValue by remember {
        mutableStateOf(TextFieldValue(
            text = if (disabled) "" else query ?: "",
            selection = TextRange(if (disabled) 0 else query?.length ?: 0)
        ))
    }
    val isKeyboardOpen by keyboardAsState()

    val color = TextFieldDefaults.colors(
        unfocusedIndicatorColor = Color.Transparent,
        focusedIndicatorColor = Color.Transparent,
        unfocusedContainerColor = containerColor,
        focusedContainerColor = containerColor,
    )

    val modifiedClearIcon = remember(key1 = clearIcon) {
        if (clearIcon is MenuItem.MenuItemButton) {
            clearIcon.copy(
                button = clearIcon.button.copy(
                    eventHandler = ViewEventHandler.createOnClick {
                        textValue = TextFieldValue("")
                        focusManager.clearFocus()
                        eventSink.invoke(SearchViewEvent.CloseSearch)
                    }
                )
            )
        } else {
            clearIcon
        }
    }

    if (!query.isNullOrEmpty() && !disabled) {
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
    }

    BackHandler(isKeyboardOpen && focusState?.hasFocus == true) {
        focusManager.clearFocus()
    }

    val trailingIcon: (@Composable () -> Unit) = {
        Row {
            Box {
                androidx.compose.animation.AnimatedVisibility(
                    visible = !showClearIcon,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    MenuItem(
                        render,
                        menuItem = searchIcon
                    )
                }
                androidx.compose.animation.AnimatedVisibility(
                    visible = showClearIcon,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    MenuItem(
                        render,
                        menuItem = modifiedClearIcon,
                    )
                }
            }
            Spacer(modifier = Modifier.width(paddingLarge))
        }
    }

    val placeholder: (@Composable () -> Unit) = {
        Text(searchHint)
    }

    val interactionSource = remember { MutableInteractionSource() }

    Box(modifier = modifier) {
        BasicTextField(
            value = textValue,
            onValueChange = {
                eventSink.invoke(SearchViewEvent.QueryChange(it.text))
                textValue = it
            },
            modifier = Modifier
                .fillMaxSize()
                .focusRequester(focusRequester)
                .onFocusChanged {
                    focusState = it
                    eventSink.invoke(SearchViewEvent.QueryFocused(it.isFocused))
                },
            textStyle = searchFieldTextStyle.copy(color = MaterialTheme.colorScheme.onSurface),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search,
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    eventSink.invoke(SearchViewEvent.QuerySubmit(textValue.text))
                }
            ),
            decorationBox = { innerTextField ->
                TextFieldDefaults.DecorationBox(
                    value = textValue.text,
                    innerTextField = innerTextField,
                    placeholder = placeholder,
                    trailingIcon = trailingIcon,
                    colors = color,
                    shape = shape,
                    interactionSource = interactionSource,
                    enabled = true,
                    singleLine = true,
                    visualTransformation = VisualTransformation.None,
                    contentPadding = PaddingValues(
                        horizontal = paddingLarge,
                        vertical = paddingDefault
                    )
                )
            }
        )
    }
}