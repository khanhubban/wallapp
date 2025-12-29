package wallapp.auth.firebase

data class FirebaseAuthUser(
    val userId: String,
    val email: String?,
    val displayName: String?,
    val photoUrl: String?,
    val isAnonymous: Boolean,
    val providerId: String,
) {
    val bestDisplayName: String
        get() = displayName ?: email ?: userId

    companion object {
        val Preset = FirebaseAuthUser(
            userId = "1234567890",
            email = null,
            displayName = null,
            photoUrl = null,
            isAnonymous = true,
            providerId = "preset_provider",
        )
    }
}
