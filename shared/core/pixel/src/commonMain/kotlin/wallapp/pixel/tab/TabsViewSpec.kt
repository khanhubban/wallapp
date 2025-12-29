package wallapp.pixel.tab

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import co.touchlab.skie.configuration.annotations.SealedInterop
import wallapp.pixel.util.DpOptional
import wallapp.pixel.view.ViewSpec

@Immutable
@SealedInterop.Enabled
sealed class TabsViewSpec : ViewSpec {

    abstract val tabHeight: Dp

    data class Indicator(
        override val tabHeight: Dp = 50.dp,
        val tabIndicatorHeight: Dp = 6.dp,
        val tabContainerHeight: Dp = tabHeight + tabIndicatorHeight,
        val tabWidth: DpOptional? = null
    ) : TabsViewSpec()

    data class Pill(
        val containerHeight: Dp,
        val tabWidth: DpOptional? = null,
        override val tabHeight: Dp,
    ) : TabsViewSpec()
}
