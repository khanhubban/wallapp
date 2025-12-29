package wallapp.ads.inline.promo

import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import wallapp.promo.PromoCategory

interface PromoItem {
    @get:StringRes val title: Int?
    @get:StringRes val summary: Int?
    @get:DrawableRes val backgroundDrawable: Int?
    @get:LayoutRes val layoutIdRes: Int
}

data class PromoItemHeroTextAndBuyButton(
    @StringRes override val title: Int?,
    @StringRes override val summary: Int?,
    @DrawableRes override val backgroundDrawable: Int? = null,
    @LayoutRes override val layoutIdRes: Int = -1,//R.layout.view_promo_hero_text_and_button,
) : PromoItem

/**
 *
 */
data class PromoItemImage(
    val promoCategory: PromoCategory,
    @StringRes override val title: Int?,
    @StringRes override val summary: Int?,
    val promoImageUri: String = "",
    @DrawableRes override val backgroundDrawable: Int? = null,
    @LayoutRes override val layoutIdRes: Int = -1,//R.layout.view_layout_promo_image_ad,
) : PromoItem {

    constructor(promoItemImage: PromoItemImage,
                @StringRes title: Int? = promoItemImage.title,
                @StringRes summary: Int? = promoItemImage.summary)
            : this(promoItemImage.promoCategory, title, summary, promoItemImage.promoImageUri,
            promoItemImage.backgroundDrawable, promoItemImage.layoutIdRes)

}

/**
 *
 */
class PromoItemText(
    @StringRes override val title: Int? = null,
    @StringRes override val summary: Int? = null,
    @DrawableRes val titleDrawable: Int? = null,
    @DrawableRes override val backgroundDrawable: Int? = null,
    @LayoutRes override val layoutIdRes: Int = -1,//R.layout.item_promo_text,
): PromoItem
