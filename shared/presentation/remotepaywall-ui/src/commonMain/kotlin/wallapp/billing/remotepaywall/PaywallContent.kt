package wallapp.billing.remotepaywall

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import wallapp.pixel.render.Render
import wallapp.remotepaywall.RemotePaywallCallbacks
import wallapp.remotepaywall.RemotePaywallEvent
import wallapp.remotepaywall.RemotePaywallUiController

@Composable
expect fun PaywallContent(
    render: Render,
    remotePaywallUiController: RemotePaywallUiController,
    remotePaywallEvent: RemotePaywallEvent,
    modifier: Modifier = Modifier,
    remotePaywallCallbacks: RemotePaywallCallbacks? = null,
    loadingComposable: @Composable (() -> Unit) = {},
    errorComposable: @Composable ((Throwable) -> Unit) = {},
)