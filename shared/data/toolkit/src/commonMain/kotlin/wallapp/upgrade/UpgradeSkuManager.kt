package wallapp.upgrade


/**
 * Responsible for providing the app with the current billing Sku(s) to use.
 *
 * Helpful when offering users a cheaper SKU for a limited time.
 */
interface UpgradeSkuManager {
    val plusSku: String
}

class UpgradeSkuManagerMock(override val plusSku: String): UpgradeSkuManager