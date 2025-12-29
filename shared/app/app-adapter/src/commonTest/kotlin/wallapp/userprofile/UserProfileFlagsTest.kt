package wallapp.userprofile

import wallapp.userprofile.UserProfileFlags.Flag.SpecialCasePlusEntitlement
import wallapp.userprofile.UserProfileFlags.hasSpecialCasePlusEntitlement
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class UserProfileFlagsTest {

    @Test fun `testConditionForHasUnlockAllFlag for True`() {
        val flag = UserProfileFlag(SpecialCasePlusEntitlement, "true")
        assertTrue(hasSpecialCasePlusEntitlement(flag))
    }

    @Test fun `testConditionForHasUnlockAllFlag for 1`() {
        val flag = UserProfileFlag(SpecialCasePlusEntitlement, "1")
        assertTrue(hasSpecialCasePlusEntitlement(flag))
    }

    @Test fun `testConditionForHasUnlockAllFlag for different value`() {
        val flag = UserProfileFlag(SpecialCasePlusEntitlement, "other")
        assertFalse(hasSpecialCasePlusEntitlement(flag))
    }

    @Test fun `hasSpecialCasePlusEntitlement false with different key`() {
        assertFalse(hasSpecialCasePlusEntitlement(listOf(UserProfileFlag("key", "value"))))
    }

    @Test fun `fromJson valid formatting`() {
        val json = """{"key":"specialCaseIsDeveloper", "value": "false"}"""
        val result = UserProfileFlag.fromExportString(json)
        assertNotNull(result)
    }

    @Test fun `fromJson bad formatting`() {
        assertNull(UserProfileFlag.fromExportString("foo"))
    }
}