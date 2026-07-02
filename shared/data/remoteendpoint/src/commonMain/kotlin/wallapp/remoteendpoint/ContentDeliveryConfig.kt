package wallapp.remoteendpoint

/**
 * Base URL of the content CDN (R2 custom domain). Versioned paths are appended:
 * "$baseUrl/api/<catalogVersion>/...". See delivery design §4.
 */
data class ContentDeliveryConfig(
    val baseUrl: String,
)
