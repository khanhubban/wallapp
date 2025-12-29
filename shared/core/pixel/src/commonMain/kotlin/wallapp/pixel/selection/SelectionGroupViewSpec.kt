package wallapp.pixel.selection

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import co.touchlab.skie.configuration.annotations.SealedInterop
import wallapp.pixel.view.ViewSpec

@Immutable
@SealedInterop.Enabled
sealed interface SelectionGroupViewSpec : ViewSpec {

    val itemWidth: Dp
    val itemHeight: Dp

    @Immutable
    data class Row(
        override val itemWidth: Dp,
        override val itemHeight: Dp,
    ) : SelectionGroupViewSpec

    @Immutable
    data class Grid(
        override val itemWidth: Dp,
        override val itemHeight: Dp,
        val containerPaddingValues: PaddingValues? = null,
        val horizontalItemSpacing: Dp = 0.dp,
        val verticalItemSpacing: Dp = 0.dp,
    ) : SelectionGroupViewSpec
}