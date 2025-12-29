package wallapp.content.state.home

import androidx.compose.runtime.Immutable
import co.touchlab.skie.configuration.annotations.SealedInterop
import wallapp.content.state.widget.NoDataViewState
import wallapp.pixel.pager.LastPagerStateUpdateSink
import wallapp.pixel.screen.ScreenViewState
import wallapp.pixel.tab.TabsViewState

@Immutable
@SealedInterop.Enabled
sealed class HomeViewState : ScreenViewState {

    @Immutable
    data object Loading : HomeViewState()

    @Immutable
    data class Data(
        val topBar: HomeTopBarViewState,
        val tabs: TabsViewState,
        val lastPagerStateUpdateSink: LastPagerStateUpdateSink?,
    ): HomeViewState()

    @Immutable
    data class NoData(
        val viewState: NoDataViewState,
    ): HomeViewState()
}
