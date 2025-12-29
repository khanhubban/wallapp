package wallapp.billing.sku

import co.touchlab.skie.configuration.annotations.EnumInterop

@EnumInterop.Enabled
enum class BillingProductType {
    InApp,
    Subscription,
}