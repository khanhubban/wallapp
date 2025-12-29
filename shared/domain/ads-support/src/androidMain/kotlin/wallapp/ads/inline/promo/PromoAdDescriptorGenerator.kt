package wallapp.ads.inline.promo


//class PromoAdDescriptorGenerator {
//
//    private val heroTextPairs = listOf(
//        R.string.promo_buy_once_alt_1_message_1 to R.string.promo_buy_once_alt_1_message_2,
//        R.string.promo_buy_once_alt_2_message_1 to R.string.promo_buy_once_alt_2_message_2,
//        R.string.promo_buy_once_alt_3_message_1 to R.string.promo_buy_once_alt_3_message_2,
//        R.string.promo_buy_once_alt_4_message_1 to R.string.promo_buy_once_alt_4_message_2,
//    )
//
//    private val backgroundDrawables = listOf(
//        R.drawable.promo_background_blue,
//        R.drawable.promo_background_cyan,
//        R.drawable.promo_background_dark,
//        R.drawable.promo_background_green,
//        R.drawable.promo_background_orange,
//        R.drawable.promo_background_pinky_orange,
//        R.drawable.promo_background_purple,
//        R.drawable.promo_background_purple_dark,
//        R.drawable.promo_background_red,
//        R.drawable.promo_background_sky_blue,
//    )
//
//    private fun getRandomHeroTextPair(): Pair<Int, Int> = heroTextPairs.random()
//
//    @DrawableRes private fun getRandomDrawableRes(): Int = backgroundDrawables.random()
//
//    private fun generateRandomPromoItemHeroTextAndBuyButton(): PromoItemHeroTextAndBuyButton {
//        val (message1, message2) = getRandomHeroTextPair()
//        return PromoItemHeroTextAndBuyButton(
//            message1,
//            message2,
//            getRandomDrawableRes(),
//            layoutIdRes = -1,//R.layout.view_promo_hero_text_and_button,
//        )
//    }
//
//    fun generate(appUiLocation: AppUiLocation): InlineAdDescriptorPromo {
//        return InlineAdDescriptorPromo(generateRandomPromoItemHeroTextAndBuyButton(), appUiLocation)
//    }
//}
//
//fun PromoItemHeroTextAndBuyButton.isTooSimilar(other: PromoItemHeroTextAndBuyButton): Boolean {
//    if (this.backgroundDrawable != null && this.backgroundDrawable == other.backgroundDrawable) {
//        return true
//    }
//    if (this.title != null
//        && this.title == other.title
//        && this.summary != null
//        && this.summary == other.summary) {
//        return true
//    }
//    return false
//}
//
//fun InlineAdDescriptorPromo.isTooSimilar(other: InlineAdDescriptorPromo?): Boolean {
//    if (other == null) return false
//
//    if (this.promoItem is PromoItemHeroTextAndBuyButton
//        && other.promoItem is PromoItemHeroTextAndBuyButton
//    ) {
//        return this.promoItem.isTooSimilar(other.promoItem)
//    }
//    return false
//}
//
//fun PromoAdDescriptorGenerator.generate(
//    appUiLocation: AppUiLocation,
//    ignore: InlineAdDescriptorPromo?,
//): InlineAdDescriptorPromo {
//    var result = generate(appUiLocation)
//
//    for (i in 0..10) {
//        if (!result.isTooSimilar(ignore)) {
//            return result
//        }
//        result = generate(appUiLocation)
//    }
//
//    return result
//}