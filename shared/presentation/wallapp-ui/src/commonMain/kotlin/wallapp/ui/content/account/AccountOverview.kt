package wallapp.ui.content.account

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import wallapp.content.state.account.AccountOverviewViewState
import wallapp.pixel.render.Render


@Composable
fun AccountOverview(
    render: Render,
    viewState: AccountOverviewViewState,
    modifier: Modifier = Modifier,
) {
    AccountOverviewContent(render, viewState, modifier = modifier.fillMaxWidth())
}

@Composable
private fun AccountOverviewContent(
    render: Render,
    viewState: AccountOverviewViewState,
    modifier: Modifier,
) {
    when (viewState) {
        is AccountOverviewViewState.Loading -> {
            AccountOverviewLoading(render, viewState, modifier)
        }

        is AccountOverviewViewState.SignedIn -> {
            val animatedMessageBarHeight by animateDpAsState(
                targetValue = viewState.messageBarHeight,
                animationSpec = tween(durationMillis = 500),
            )
            AccountOverviewSignedIn(
                render = render,
                viewState = viewState,
                modifier = modifier.padding(top = animatedMessageBarHeight),
            )
        }

        is AccountOverviewViewState.SignedOut -> {
            val animatedMessageBarHeight by animateDpAsState(
                targetValue = viewState.messageBarHeight,
                animationSpec = tween(durationMillis = 500),
            )
            AccountOverviewSignedOut(
                render = render,
                viewState = viewState,
                modifier = modifier.padding(top = animatedMessageBarHeight),
            )
        }
    }
}

@Composable
private fun AccountOverviewContainer(
    render: Render,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(color = MaterialTheme.colorScheme.surface)
            .padding(vertical = 24.dp),
    ) {
        content()
    }
}