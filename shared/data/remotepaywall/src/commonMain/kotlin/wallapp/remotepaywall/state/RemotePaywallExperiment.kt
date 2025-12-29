package wallapp.remotepaywall.state

import kotlinx.serialization.Serializable

@Serializable
data class RemotePaywallExperiment(
    val id: String,
    val groupId: String,
    val variant: Variant,
) {
    @Serializable
    data class Variant(
        val id: String,
        val type: VariantType,
        val paywallId: String?,
    ) {
        enum class VariantType {
            TREATMENT,
            HOLDOUT
        }
    }

    companion object {
        fun presentById(id: String) = RemotePaywallExperiment(
            id = id,
            groupId = "",
            variant = Variant(id = "", type = Variant.VariantType.TREATMENT, paywallId = id)
        )
    }
}

typealias RemotePaywallExperimentID = String