package wallapp.inappreview

import platform.StoreKit.SKStoreReviewController

class InAppReviewManagerIos : InAppReviewManager {

    override fun requestReview() {
        SKStoreReviewController.requestReview()
    }
}
