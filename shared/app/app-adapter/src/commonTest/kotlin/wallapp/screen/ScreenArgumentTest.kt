package wallapp.ui.app.navigation

import wallapp.content.model.Id
import wallapp.remotepaywall.RemotePaywallEvent
import wallapp.screen.ScreenArgument
import wallapp.screen.ScreenArgument.AccountScreenArgument
import wallapp.screen.ScreenArgument.OssLicensesScreenArgument
import wallapp.screen.ScreenArgument.PaywallScreenArgument
import wallapp.screen.ScreenArgument.WallpaperShowcaseScreenArgument
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals


internal class ScreenArgumentTest {

    @Test
    fun `WallpaperShowcaseArgument to and from Json`() {
        val argument = WallpaperShowcaseScreenArgument(
            remixId = Id.RemixId("testId"),
        )
        val jsonString = argument.jsonString
        val fromString = ScreenArgument.fromJsonString(jsonString)
        assertEquals(argument, fromString)
    }

    @Test
    fun `fromJsonString returns null for invalid json`() {
        val fromString = ScreenArgument.fromJsonString("invalid json")
        assertEquals(null, fromString)
    }

    @Test
    fun toAndFrom_PaywallScreenArgument_Remote() {
        val argument: PaywallScreenArgument = PaywallScreenArgument.Remote(
            paywallEvent = RemotePaywallEvent(event = "testEvent"),
            autoTriggerPurchase = true,
            subscriptionPlan = null,
            subscriptionExpired = false,
        )
        val jsonString = argument.jsonString
        val fromString = ScreenArgument.fromJsonString(jsonString)
        assertEquals(argument, fromString)
    }

    @Test
    fun toAndFrom_PaywallScreenArgument_Arbitrated() {
        val argument: PaywallScreenArgument =
            PaywallScreenArgument.Arbitrated(autoTriggerPurchase = true, subscriptionPlan = null, subscriptionExpired = false)
        val jsonString = argument.jsonString
        val fromString = ScreenArgument.fromJsonString(jsonString)
        assertEquals(argument, fromString)
    }

    @Test
    fun toAndFrom_PaywallScreenArgument_Internal() {
        val argument: PaywallScreenArgument =
            PaywallScreenArgument.Internal(autoTriggerPurchase = true, subscriptionPlan = null, subscriptionExpired = false)
        val jsonString = argument.jsonString
        val fromString = ScreenArgument.fromJsonString(jsonString)
        assertEquals(argument, fromString)
    }

    @Test
    fun toAndFrom_OssLicensesScreenArgument() {
        val argument = OssLicensesScreenArgument
        val jsonString = argument.jsonString
        val fromString = ScreenArgument.fromJsonString(jsonString)
        assertEquals(argument, fromString)
    }

    @Test
    fun toAndFrom_AccountScreenArgument() {
        val argument = AccountScreenArgument
        val jsonString = argument.jsonString
        val fromString = ScreenArgument.fromJsonString(jsonString)
        assertEquals(argument, fromString)
    }

    @Test
    fun dataObjectComparison() {
        val argument1 = AccountScreenArgument
        val argument2 = OssLicensesScreenArgument

        assertNotEquals(argument1 as ScreenArgument, argument2 as ScreenArgument)

        val jsonString1 = argument1.jsonString
        val jsonString2 = argument2.jsonString
        assertNotEquals(jsonString1, jsonString2)

        val fromString1 = ScreenArgument.fromJsonString(jsonString1)
        val fromString2 = ScreenArgument.fromJsonString(jsonString2)
        assertNotEquals(fromString1, fromString2)
        assertEquals(argument1, fromString1)
        assertEquals(argument2, fromString2)
    }

}