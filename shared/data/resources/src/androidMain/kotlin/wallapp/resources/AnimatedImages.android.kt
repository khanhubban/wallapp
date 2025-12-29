package wallapp.resources

import wallapp.resource.AnimatedImageClipSpec
import wallapp.resource.AnimatedImageSpec
import wallapp.resource.Resource
import wallapp.resource.Resource.AnimatedImage

actual object AnimatedImages {

    private val AnimatedImageSpec.animatedImage: AnimatedImage
        get() = AnimatedImage(this)

    actual val Celebration: Resource
        get() = AnimatedImageSpec(
            resource = Resource.from(LocalRawAssetDefault.LottieCelebration),
            infiniteRepeat = false,
        ).animatedImage

    actual val CollectionsLarge: Resource
        get() = AnimatedImageSpec(
            resource = Resource.from(LocalRawAssetDefault.LottieCollectionsLarge),
            infiniteRepeat = true,
        ).animatedImage

    actual val FollowOn: Resource
        get() = AnimatedImageSpec(
            resource = Resource.from(LocalRawAssetDefault.LottieFollowToggle),
            speed = 1.5f,
            infiniteRepeat = false,
            clipSpec = AnimatedImageClipSpec.Progress(min = 0f, max = .5f),
        ).animatedImage

    actual val FollowOff: Resource
        get() = AnimatedImageSpec(
            resource = Resource.from(LocalRawAssetDefault.LottieFollowToggle),
            speed = 1.5f,
            infiniteRepeat = false,
            clipSpec = AnimatedImageClipSpec.Progress(min = .5f, max = 1f),
        ).animatedImage

    actual val FavoriteOn: Resource
        get() = AnimatedImageSpec(
            resource = Resource.from(LocalRawAssetDefault.LottieHeartSmall),
            speed = 1.25f,
            infiniteRepeat = false,
            clipSpec = AnimatedImageClipSpec.Progress(min = 0f, max = .5f),
        ).animatedImage

    actual val FavoriteOff: Resource
        get() = AnimatedImageSpec(
            resource = Resource.from(LocalRawAssetDefault.LottieHeartSmall),
            speed = 1.25f,
            infiniteRepeat = false,
            clipSpec = AnimatedImageClipSpec.Progress(min = .5f, max = 1f),
        ).animatedImage

    actual val HeartLarge: Resource
        get() = AnimatedImageSpec(
            resource = Resource.from(LocalRawAssetDefault.LottieHeartLarge),
            infiniteRepeat = true,
        ).animatedImage

    actual val Loading: Resource
        get() = AnimatedImageSpec(
            resource = Resource.from(LocalRawAssetDefault.LottieLoading),
            infiniteRepeat = true,
        ).animatedImage

    actual val OnboardingSelection: Resource
        get() = AnimatedImageSpec(
            resource = Resource.from(LocalRawAssetDefault.LottieOnboardingSelection),
            infiniteRepeat = false,
        ).animatedImage

//    actual val Favorite: Resource
//        get() = AnimatedImageSpec(
//            resource = Raw.Favorite,
//            infiniteRepeat = true,
//        ).animatedImage
//
//    actual val FavoriteOn: Resource
//        get() = AnimatedImageSpec(
//            resource = Raw.Favorite,
//            infiniteRepeat = false,
//            clipSpec = AnimatedImageClipSpec.Progress(min = 0.15f, max = .5f),
//        ).animatedImage
//
//    actual val FavoriteOff: Resource
//        get() = AnimatedImageSpec(
//            resource = Raw.Favorite,
//            infiniteRepeat = false,
//            clipSpec = AnimatedImageClipSpec.Progress(min = 0.65f, max = 1f),
//        ).animatedImage

//    actual val FavoriteAlt: Resource
//        get() = AnimatedImageSpec(
//            resource = Raw.FavoriteAlt,
//            speed = 2f,
//            infiniteRepeat = true,
//        ).animatedImage
//
//    actual val FavoriteAltOn: Resource
//        get() = AnimatedImageSpec(
//            resource = Raw.FavoriteAlt,
//            speed = 3.5f,
//            infiniteRepeat = false,
//            clipSpec = AnimatedImageClipSpec.Frame(min = 0, max = 70),
//        ).animatedImage
//
//    actual val FavoriteAltOff: Resource
//        get() = AnimatedImageSpec(
//            resource = Raw.FavoriteAlt,
//            speed = 2f,
//            infiniteRepeat = false,
//            clipSpec = AnimatedImageClipSpec.Frame(min = 80, max = 120),
//        ).animatedImage
//
}