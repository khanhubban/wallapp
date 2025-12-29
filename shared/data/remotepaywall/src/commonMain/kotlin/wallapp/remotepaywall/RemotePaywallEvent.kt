package wallapp.remotepaywall

import kotlinx.serialization.Serializable

@Serializable
data class RemotePaywallEvent(
    val event: String,
) {
    // Currently unused, and can't natively be serialized. Always return null until we need it.
    val params: Map<String, Any>?
        get() = null
}