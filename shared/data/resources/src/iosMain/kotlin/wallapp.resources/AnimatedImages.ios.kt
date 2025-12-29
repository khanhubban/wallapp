package wallapp.resources

import wallapp.resource.AnimatedImageClipSpec
import wallapp.resource.AnimatedImageSpec
import wallapp.resource.Resource

actual object AnimatedImages {

    private val Placeholder = Resource.Placeholder

    actual val Celebration: Resource
        get() = Resource.AnimatedImage(
            animatedImageSpec = AnimatedImageSpec(
                resource = Resource.from(LocalRawAssetDefault.LottieCelebration),
                infiniteRepeat = false,
                fillAspectRatio = true,
            ),
            fallback = null,
        )

    actual val CollectionsLarge: Resource
        get() = Resource.AnimatedImage(
            animatedImageSpec = AnimatedImageSpec(
                resource = Resource.from(LocalRawAssetDefault.LottieCollectionsLarge),
                infiniteRepeat = true
            ),
            fallback = null,
        )

    actual val FavoriteOn: Resource
        get() = Resource.AnimatedImage(
            animatedImageSpec = AnimatedImageSpec(
                resource = Resource.from(LocalRawAssetDefault.LottieHeartSmall),
                infiniteRepeat = false,
                clipSpec = AnimatedImageClipSpec.Progress(min = 0f, max = 0.5f),
                speed = 1.25f,
            ),
            fallback = null,
        )

    actual val FavoriteOff: Resource
        get() = Resource.AnimatedImage(
            animatedImageSpec = AnimatedImageSpec(
                resource = Resource.from(LocalRawAssetDefault.LottieHeartSmall),
                infiniteRepeat = false,
                clipSpec = AnimatedImageClipSpec.Progress(min = 0.5f, max = 1f),
                speed = 1.25f,
            ),
            fallback = null,
        )

    actual val FollowOn: Resource
        get() = Resource.AnimatedImage(
            animatedImageSpec = AnimatedImageSpec(
                resource = Resource.from(LocalRawAssetDefault.LottieFollowToggle),
                infiniteRepeat = false,
                clipSpec = AnimatedImageClipSpec.Progress(min = 0.15f, max = .5f),
            ),
            fallback = null,
        )

    actual val FollowOff: Resource
        get() = Resource.AnimatedImage(
            animatedImageSpec = AnimatedImageSpec(
                resource = Resource.from(LocalRawAssetDefault.LottieFollowToggle),
                infiniteRepeat = false,
                clipSpec = AnimatedImageClipSpec.Progress(min = 0.65f, max = 1f),
            ),
            fallback = null,
        )

    actual val HeartLarge: Resource
        get() = Resource.AnimatedImage(
            animatedImageSpec = AnimatedImageSpec(
                resource = Resource.from(LocalRawAssetDefault.LottieHeartLarge),
                infiniteRepeat = true
            ),
            fallback = null,
        )

    actual val Loading: Resource
        get() = Resource.AnimatedImage(
            animatedImageSpec = AnimatedImageSpec(
                resource = Resource.from(LocalRawAssetDefault.LottieLoading),
                infiniteRepeat = true
            ),
            fallback = null,
        )
    actual val OnboardingSelection: Resource
        get() = Resource.AnimatedImage(
            animatedImageSpec = AnimatedImageSpec(
                resource = Resource.from(LocalRawAssetDefault.LottieOnboardingSelection),
                infiniteRepeat = false
            ),
            fallback = null,
        )
}
