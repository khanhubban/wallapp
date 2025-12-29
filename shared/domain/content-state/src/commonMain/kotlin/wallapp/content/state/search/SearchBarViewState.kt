package wallapp.content.state.search

import androidx.compose.runtime.Immutable
import wallapp.font.TextStyle
import wallapp.pixel.menu.MenuItem
import wallapp.pixel.shape.ShapeSpec
import wallapp.pixel.text.Text
import wallapp.pixel.view.ViewState
import wallapp.theme.ColorToken

@Immutable
data class SearchBarViewState(
    val eventSink: SearchViewEventSink,
    val searchIcon: MenuItem,
    val searchInputViewShape: ShapeSpec,
    val searchHint: Text,
    val searchFieldTextStyle: TextStyle,
    val query: String?,
    val showClearIcon: Boolean,
    val clearIcon: MenuItem,
    val recipeBinViewState: SearchRecipeBinViewState?,
    val transitionEnabled: Boolean,
    val containerColor: ColorToken,
) : ViewState