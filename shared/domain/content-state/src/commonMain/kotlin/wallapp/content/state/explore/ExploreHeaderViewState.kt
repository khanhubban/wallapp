package wallapp.content.state.explore

import androidx.compose.runtime.Immutable
import wallapp.graphics.Color
import wallapp.pixel.menu.MenuItem
import wallapp.pixel.shape.ShapeSpec
import wallapp.pixel.view.ViewState

@Immutable
data class ExploreHeaderViewState(
    val viewSpec: ExploreHeaderViewSpec,
    val containerColor: Color,
    val searchIcon: MenuItem,
    val searchLabel: MenuItem,
    val searchOnClick: () -> Unit,
    val containerShapeSpec: ShapeSpec,
): ViewState
