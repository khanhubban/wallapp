package wallapp.content.state.upgrade

enum class SubscriptionPlan {
    PlusBasic,
    PlusUnlimited,
}

val SubscriptionPlan.useOrangeHighlightColor: Boolean
    get() = this == SubscriptionPlan.PlusUnlimited

enum class PlusPlan {
    ANNUAL, MONTHLY
}