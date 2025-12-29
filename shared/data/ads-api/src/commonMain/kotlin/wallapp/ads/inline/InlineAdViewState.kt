package wallapp.ads.inline

import androidx.compose.runtime.Immutable
import wallapp.ads.media.AdMedia
import wallapp.pixel.view.ViewState

@Immutable
sealed interface InlineAdViewState : ViewState {

    @Immutable
    data object NoOp : InlineAdViewState

    @Immutable
    data object Loading : InlineAdViewState

    @Immutable
    data class Data(
        val starRating: Double?,
        val advertiser: String?,
        val body: String?,
        val icon: AdMedia?,
        val images: List<AdMedia>?,
        val callToAction: String?,
        val callToActionIcon: AdMedia?,
        val headline: String?,
        val price: String?,
        val store: String?,
    ) : InlineAdViewState {

        constructor(contentState: InlineAdContentState) : this(
            starRating = contentState.starRating,
            advertiser = contentState.advertiser,
            body = contentState.body,
            icon = contentState.icon,
            images = contentState.images,
            callToAction = contentState.callToAction,
            callToActionIcon = contentState.callToActionIcon,
            headline = contentState.headline,
            price = contentState.price,
            store = contentState.store,
        )
    }

}