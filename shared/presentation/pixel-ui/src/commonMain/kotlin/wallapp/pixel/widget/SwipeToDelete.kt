package wallapp.pixel.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color


@ExperimentalMaterialApi
@Composable
fun SwipeToDelete(
    dismissContent: @Composable RowScope.() -> Unit,
    onDeleted: () -> Unit,
) {
    val confirmStateChange: (DismissValue) -> Boolean = {
        true
    }

    val dismissState = rememberDismissState(
        initialValue = DismissValue.Default,
        confirmStateChange = confirmStateChange,
    )

    if (dismissState.isDismissed(DismissDirection.EndToStart)) {
        onDeleted.invoke()
    }

    SwipeToDismiss(
        state = dismissState,
        background = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Red),
            )
        },
        dismissContent = dismissContent,
        directions = setOf(DismissDirection.EndToStart),
    )
}