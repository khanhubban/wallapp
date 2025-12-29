package wallapp.pixel.swipetodismiss

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import wallapp.pixel.compose.conditional

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SwipeToDismiss(
    onSwipeToDismiss: OnSwipeToDismiss?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable BoxScope.() -> Unit,
) {
    val refreshing = false
    val pullRefreshState = rememberPullRefreshState(refreshing, {
        onSwipeToDismiss?.invoke()
    })

    Box(
        modifier = modifier
            .conditional(enabled && onSwipeToDismiss != null) {
                pullRefresh(pullRefreshState)
            },
    ) {
        content()
    }
}



