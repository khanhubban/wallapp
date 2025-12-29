package wallapp.content.state.appicon

import wallapp.content.state.upgrade.SubscriptionPlan
import wallapp.image.Image

data class AppIconSpec(
    val label: String,
    val icon: Image,
    val appIconPreviewSelected: Image,
    val appIconPreviewUnselected: Image? = null,
    val subscriptionPlan: SubscriptionPlan? = null,
)