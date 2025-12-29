package wallapp.remotepaywall

data class RemotePaywallCallbacks(
    val onDismissRequest: () -> Unit,
)