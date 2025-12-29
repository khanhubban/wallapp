package wallapp.content.state.connections

import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import wallapp.data.content.ContentRepository
import wallapp.data.content.ContentResult.ConnectionsContentResult
import wallapp.pixel.screen.ScreenViewStateProvider
import wallapp.view.ViewStateFactory
import wallapp.view.ViewStateRefresher
import wallapp.viewmodel.ViewModel

class ConnectionsViewModel(
    private val selectedConnectionType: ConnectionType?,
    contentRepository: ContentRepository,
    viewStateRefresher: ViewStateRefresher,
    private val viewStateFactory: ViewStateFactory,
) : ViewModel(), ScreenViewStateProvider {

    private val initialTabIndex: Int
        get() = selectedConnectionType?.ordinal ?: 0

    private fun createViewState(
        connections: ConnectionsContentResult?,
    ): ConnectionsViewState {
        return viewStateFactory.createConnectionsViewState(
            connections,
            initialTabIndex = initialTabIndex,
        )
    }

    override val viewState: StateFlow<ConnectionsViewState> = combine(
        contentRepository.connectionsContent,
        viewStateRefresher.refresh,
    ) { connections, _ ->
        createViewState(connections)
    }.stateIn(initialValue = createViewState(connections = null))
}