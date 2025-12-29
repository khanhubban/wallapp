package wallapp.billing

import kotlin.test.Test
import kotlin.test.assertEquals


class BillingResultTest {

    @Test fun toAndFromJson() {
        BillingResult(
            success = true,
            responseCode = 0,
            isUserFacingError = false,
            debugMessage = "debugMessage",
        ).also { billingResult ->
            billingResult.exportString.let { exportString ->
                BillingResult.fromExportString(exportString)?.let { billingResult2 ->
                    assertEquals(billingResult, billingResult2)
                }
            }
        }
    }

    @Test fun `toAndFromJson alt`() {
        BillingResult(
            success = true,
            responseCode = 0,
            isUserFacingError = null,
            debugMessage = "debugMessage",
        ).also { billingResult ->
            billingResult.exportString.let { exportString ->
                BillingResult.fromExportString(exportString)?.let { billingResult2 ->
                    assertEquals(billingResult, billingResult2)
                }
            }
        }
    }

}