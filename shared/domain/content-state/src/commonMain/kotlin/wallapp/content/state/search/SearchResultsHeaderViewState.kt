package wallapp.content.state.search

import androidx.compose.runtime.Immutable
import wallapp.image.Image
import wallapp.pixel.menu.MenuItem
import wallapp.pixel.text.Text
import wallapp.pixel.view.ViewEventHandler
import wallapp.pixel.view.ViewState
import wallapp.theme.ColorToken

@Immutable
data class SearchResultsHeaderViewState(
    val viewSpec: SearchResultsHeaderViewSpec,
    val searchText: Text,
    val clearIcon: MenuItem,
    val topScrim: Image,
    val searchRecipeBinViewState: SearchRecipeBinViewState?,
    val clickEventHandler: ViewEventHandler,
    val searchContainerColor: ColorToken = ColorToken.ThemeSurface,
): ViewState