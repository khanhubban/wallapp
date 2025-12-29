package wallapp.userprofile

object UserProfileRepositoryArbitrator {

    /**
     * Email is only persisted when the user opted into newsletters and has a non-anonymous auth email.
     */
    fun computePersistedEmail(authEmail: String?, newsletter: Boolean, isAnonymous: Boolean): String? {
        if (!newsletter || isAnonymous) return null
        return authEmail
    }

    /**
     * Resolves email for an update using current values plus any incoming updates.
     */
    fun resolveEmailForUpdate(
        authEmail: String?,
        currentNewsletter: Boolean,
        currentIsAnonymous: Boolean,
        newsletterUpdate: Boolean?,
        isAnonymousUpdate: Boolean?
    ): String? {
        val effectiveNewsletter = newsletterUpdate ?: currentNewsletter
        val effectiveIsAnonymous = isAnonymousUpdate ?: currentIsAnonymous
        return computePersistedEmail(authEmail, effectiveNewsletter, effectiveIsAnonymous)
    }

    /**
     * Returns a copy of the fields with the email value rewritten using resolveEmailForUpdate.
     */
    fun sanitizeEmailFields(
        fieldsAndValues: List<Pair<String, Any?>>,
        emailField: String,
        newsletterField: String,
        isAnonymousField: String,
        authEmail: String?,
        currentNewsletter: Boolean,
        currentIsAnonymous: Boolean
    ): List<Pair<String, Any?>> {
        val newsletterUpdate = fieldsAndValues.firstOrNull { it.first == newsletterField }?.second as? Boolean
        val isAnonymousUpdate = fieldsAndValues.firstOrNull { it.first == isAnonymousField }?.second as? Boolean
        val shouldUpdateEmail = fieldsAndValues.any { it.first == emailField } ||
            newsletterUpdate != null ||
            isAnonymousUpdate != null

        if (!shouldUpdateEmail) return fieldsAndValues.toList()

        val persistedEmail = resolveEmailForUpdate(
            authEmail = authEmail,
            currentNewsletter = currentNewsletter,
            currentIsAnonymous = currentIsAnonymous,
            newsletterUpdate = newsletterUpdate,
            isAnonymousUpdate = isAnonymousUpdate
        )

        val sanitizedFields = fieldsAndValues.filterNot { it.first == emailField }.toMutableList()
        sanitizedFields.add(emailField to persistedEmail)
        return sanitizedFields
    }
}
