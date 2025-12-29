package wallapp.resources

import wallapp.resource.LocalRawAsset
import wallapp.resource.Resource

sealed class LocalRawAssetDefault(val fileName: String): LocalRawAsset {
    override fun toResource(): Resource {
        return this.toResourceNative()
    }

    data object LottieCelebration: LocalRawAssetDefault("celebration")
    data object LottieCollectionsLarge: LocalRawAssetDefault("collections_large")
    data object LottieFollowToggle: LocalRawAssetDefault("follow_toggle_alt")
    data object LottieHeartLarge: LocalRawAssetDefault("heart_large")
    data object LottieHeartSmall: LocalRawAssetDefault("heart_small")
    data object LottieLoading: LocalRawAssetDefault("loading")
    data object LottieOnboardingSelection: LocalRawAssetDefault("onboarding_selection")
}

expect fun LocalRawAssetDefault.toResourceNative(): Resource
