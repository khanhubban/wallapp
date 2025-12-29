package wallapp.userprofile

import kotlin.test.Test
import kotlin.test.assertEquals

class UserProfileRepositoryArbitratorTest {

    @Test
    fun `computePersistedEmail requires newsletter opt-in and non-anonymous`() {
        val email = "person@example.com"
        assertEquals(email, UserProfileRepositoryArbitrator.computePersistedEmail(email, true, false))
        assertEquals(null, UserProfileRepositoryArbitrator.computePersistedEmail(email, false, false))
        assertEquals(null, UserProfileRepositoryArbitrator.computePersistedEmail(email, true, true))
        assertEquals(null, UserProfileRepositoryArbitrator.computePersistedEmail(null, true, false))
    }

    @Test
    fun `resolveEmailForUpdate applies incoming newsletter and anonymous changes`() {
        val authEmail = "person@example.com"

        val initial = UserProfileRepositoryArbitrator.resolveEmailForUpdate(
            authEmail = authEmail,
            currentNewsletter = false,
            currentIsAnonymous = false,
            newsletterUpdate = null,
            isAnonymousUpdate = null
        )
        assertEquals(null, initial)

        val newsletterOn = UserProfileRepositoryArbitrator.resolveEmailForUpdate(
            authEmail = authEmail,
            currentNewsletter = false,
            currentIsAnonymous = false,
            newsletterUpdate = true,
            isAnonymousUpdate = null
        )
        assertEquals(authEmail, newsletterOn)

        val anonymousOn = UserProfileRepositoryArbitrator.resolveEmailForUpdate(
            authEmail = authEmail,
            currentNewsletter = true,
            currentIsAnonymous = false,
            newsletterUpdate = null,
            isAnonymousUpdate = true
        )
        assertEquals(null, anonymousOn)
    }

    @Test
    fun `sanitizeEmailFields rewrites email when newsletter or anonymous changes`() {
        val authEmail = "person@example.com"
        val fields = listOf(
            "newsletter" to true,
            "email" to "old@example.com"
        )
        val sanitized = UserProfileRepositoryArbitrator.sanitizeEmailFields(
            fieldsAndValues = fields,
            emailField = "email",
            newsletterField = "newsletter",
            isAnonymousField = "isAnonymous",
            authEmail = authEmail,
            currentNewsletter = false,
            currentIsAnonymous = false
        )
        val emailValue = sanitized.firstOrNull { it.first == "email" }?.second
        assertEquals(authEmail, emailValue)
    }

    @Test
    fun `sanitizeEmailFields clears email when newsletter is disabled`() {
        val fields = listOf(
            "newsletter" to false,
            "email" to "keep@example.com"
        )
        val sanitized = UserProfileRepositoryArbitrator.sanitizeEmailFields(
            fieldsAndValues = fields,
            emailField = "email",
            newsletterField = "newsletter",
            isAnonymousField = "isAnonymous",
            authEmail = "person@example.com",
            currentNewsletter = true,
            currentIsAnonymous = false
        )
        val emailValue = sanitized.firstOrNull { it.first == "email" }?.second
        assertEquals(null, emailValue)
    }
}
