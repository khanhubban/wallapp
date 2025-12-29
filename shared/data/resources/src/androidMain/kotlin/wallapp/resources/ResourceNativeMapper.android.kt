package wallapp.resources

import wallapp.resource.Resource

actual fun LocalRawAssetDefault.toResourceNative(): Resource {
    return when (this) {
        LocalRawAssetDefault.LottieCelebration -> Resource.Raw(R.raw.celebration)
        LocalRawAssetDefault.LottieCollectionsLarge -> Resource.Raw(R.raw.collections_large)
        LocalRawAssetDefault.LottieFollowToggle -> Resource.Raw(R.raw.follow_toggle_alt)
        LocalRawAssetDefault.LottieHeartLarge -> Resource.Raw(R.raw.heart_large)
        LocalRawAssetDefault.LottieHeartSmall -> Resource.Raw(R.raw.heart_small)
        LocalRawAssetDefault.LottieLoading -> Resource.Raw(R.raw.loading)
        LocalRawAssetDefault.LottieOnboardingSelection -> Resource.Raw(R.raw.onboarding_selection)
    }
}

actual fun LocalImageAssetDefault.toResourceNative(): Resource {
    return Resource.DrawableCompose(this.toDrawableResource())
}

actual fun LocalFileAssetDefault.toResourceNative(): Resource {
    return Resource.FileResourceCompose(this.fileName)
}
