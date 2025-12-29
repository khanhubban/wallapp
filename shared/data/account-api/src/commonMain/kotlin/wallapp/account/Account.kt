package wallapp.account

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import wallapp.auth.firebase.FirebaseAuthUser

@Serializable
data class Account(
    val userId: String,
    val email: String?,
    val displayName: String?,
    val photoUrl: String?,
    val isAnonymous: Boolean,
    /**
     * A handful of users have a special entitlement that allows them to access all content.
     */
    val hasSpecialCasePlusEntitlement: Boolean,
    val hasSpecialCaseIsDeveloper: Boolean,
    val createdAt: Instant,
) {

    val bestDisplayName: String
        get() = displayName ?: email ?: userId

    val exportString: String
        get() = Json.encodeToString(kotlinx.serialization.serializer(), this)

    companion object {

        fun fromExportString(exportString: String): Account {
            return Json.decodeFromString(kotlinx.serialization.serializer(), exportString)
        }

        val Preset = Account(
            userId = "123456789",
            email = "test@example.com",
            displayName = "Taylor Test",
            photoUrl = "https://example.com/photo.jpg",
            isAnonymous = false,
            createdAt = Instant.fromEpochMilliseconds(0),
            hasSpecialCasePlusEntitlement = false,
            hasSpecialCaseIsDeveloper = false,
        )
    }
}

fun FirebaseAuthUser.toAccount(
    epochCreated: Long,
    hasSpecialCasePlusEntitlement: Boolean = false,
    hasSpecialCaseIsDeveloper: Boolean = false,
): Account = Account(
    userId = userId,
    email = email,
    displayName = displayName,
    photoUrl = photoUrl,
    isAnonymous = isAnonymous,
    createdAt = Instant.fromEpochMilliseconds(epochCreated),
    hasSpecialCasePlusEntitlement = hasSpecialCasePlusEntitlement,
    hasSpecialCaseIsDeveloper = hasSpecialCaseIsDeveloper,
)
