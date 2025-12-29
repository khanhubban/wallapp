package wallapp.pixel.tab

import androidx.compose.runtime.Immutable
import co.touchlab.skie.configuration.annotations.SealedInterop
import wallapp.pixel.view.ViewState
import wallapp.theme.ColorToken


@Immutable
@SealedInterop.Enabled
sealed class TabsViewState : ViewState {

    abstract val viewSpec: TabsViewSpec
    abstract val tabs: List<TabViewState>
    abstract val initialIndex: Int

    val size: Int
        get() = tabs.size

    data class Indicator(
        override val viewSpec: TabsViewSpec.Indicator,
        override val tabs: List<TabViewState>,
        override val initialIndex: Int,
        val containerColorToken: ColorToken? = null,
        val indicatorColorToken: ColorToken? = null,
    ) : TabsViewState()

    data class Pill(
        override val viewSpec: TabsViewSpec.Pill,
        override val tabs: List<TabViewState>,
        override val initialIndex: Int,
        val containerColorToken: ColorToken? = null,
        val indicatorColorToken: ColorToken? = null,
    ) : TabsViewState()

}
