package wallapp.ads.inline

import wallapp.ads.media.AdMedia

data class InlineAdContentState(
    val headline: String?,
    val callToAction: String?,
    val callToActionIcon: AdMedia?,
    val images: List<AdMedia>?,
    val body: String?,
    val icon: AdMedia?,
    val starRating: Double?,
    val store: String?,
    val price: String?,
    val advertiser: String?,
    val mediaContent: Any?,
    val hasVideoContent: Boolean,
)
