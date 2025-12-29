package wallapp.content.state.search

import androidx.compose.runtime.Immutable
import co.touchlab.skie.configuration.annotations.SealedInterop
import wallapp.pixel.image.ImageViewState
import wallapp.pixel.screen.ScreenViewState
import wallapp.pixel.selection.SelectionGroupViewState
import wallapp.pixel.view.ViewEventHandler

@Immutable
@SealedInterop.Enabled
sealed class SearchInputViewState : ScreenViewState {

    @Immutable
    data object Loading : SearchInputViewState()

    @Immutable
    data class Data(
        val viewSpec: SearchInputViewSpec,
        val searchBar: SearchBarViewState,
        val searchColors: SearchColorsViewState,
        val contentCategoryGroup: SelectionGroupViewState,
        val searchFilterGroup: SelectionGroupViewState,
        val onScrimClick: ViewEventHandler?,
        val searchSheetExpanded: Boolean,
        val sheetDropShadow: ImageViewState,
    ) : SearchInputViewState() {

        val isOverlay: Boolean
            get() = onScrimClick != null
    }
}
