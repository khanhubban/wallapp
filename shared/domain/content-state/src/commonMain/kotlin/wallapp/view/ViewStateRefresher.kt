package wallapp.view

import kotlinx.coroutines.flow.Flow
import wallapp.manager.Manager

/**
 * There are numerous instances where we need to refresh screen [ViewState]s. As an example,
 * when a user favorites a post, when the theme changes, or ads are disabled.
 *
 * Rather than having each [ViewState] observe changes in this flags, the [refresh] flow is
 * a single observable to be monitored.
 *
 * Note: the [refresh] value means nothing, it's just a signal to refresh.
 */
interface ViewStateRefresher: Manager {

    val refresh: Flow<Int>

    fun globalRefresh()

}