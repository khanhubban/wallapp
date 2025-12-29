package wallapp.content.state.search

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import wallapp.content.state.widget.EdgeFadeViewState
import wallapp.pixel.menu.MenuItem
import wallapp.pixel.shape.ShapeSpec
import wallapp.pixel.text.Text
import wallapp.pixel.view.ViewSpec
import wallapp.pixel.view.ViewState
import wallapp.theme.ColorToken

@Immutable
data class SearchRecipeBinViewState(
    val viewStates: List<ViewState>,
    val viewSpec: SearchRecipeBinViewSpec,
    val containerColorToken: ColorToken,
    val clearIcon: MenuItem,
    val shape: ShapeSpec,
    val edgeFade: EdgeFadeViewState,
    val checkIcon: MenuItem? = null,
    val endOffsetX: Dp = 0.dp, // Needed for search transition
)

@Immutable
data class SearchRecipeBinText(
    val tags: List<Text>
) : ViewState

@Immutable
data class SearchRecipeBinViewSpec(
    val startPadding: Dp,
    val endPadding: Dp,
    val recipeEndPadding: Dp,
    val spacing: Dp,
    val recipeTextOpacity: Float = 1f,
    val actionIconSpacing: Dp = 0.dp,
): ViewSpec