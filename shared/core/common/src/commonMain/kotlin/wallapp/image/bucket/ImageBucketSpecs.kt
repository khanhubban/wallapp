package wallapp.image.bucket

object ImageBucketSpecs {

    val PhoneUHD = ImageBucketSpec(
        key = "p~uhd",
        label = "Phone UHD",
        maxWidthPx = 1440,
        maxHeightPx = 3120,
        maxDensity = 4.0f,
    )

    val PhoneAppleXl = ImageBucketSpec(
        key = "p~a~xl",
        label = "Phone Apple XL",
        maxWidthPx = 1290,
        maxHeightPx = 2796,
        maxDensity = 3.0f,
    )

    val PhoneAppleNormal = ImageBucketSpec(
        key = "p~a~n",
        label = "Phone Apple Normal",
        maxWidthPx = 1179,
        maxHeightPx = 2556,
        maxDensity = 3.0f,
    )

    val Phone_5_0_Inch = ImageBucketSpec(
        key = "p~five0",
        label = "Phone 5.0 inch",
        maxWidthPx = 1080,
        maxHeightPx = 2160,
        maxDensity = 2.75f,
    )

    val PhoneSmallest = ImageBucketSpec(
        key = "p~s",
        label = "Phone Smallest",
        maxWidthPx = 960,
        maxHeightPx = 1800,
        maxDensity = 2.0f,
    )

    val FoldableFlipOpen = ImageBucketSpec(
        key = "f~fo",
        label = "Phone Foldable Flip Open",
        maxWidthPx = 2208,
        maxHeightPx = 1840,
        maxDensity = 4.0f,
    )

    val TabletSmallest = ImageBucketSpec(
        key = "t~s",
        label = "Tablet Small",
        maxWidthPx = 744,
        maxHeightPx = 1133,
        maxDensity = 1.0f,
    )

    val TabletMedium = ImageBucketSpec(
        key = "t~m",
        label = "Tablet Medium",
        maxWidthPx = 1848,
        maxHeightPx = 2960,
        maxDensity = 3.0f,
    )

    val TabletLarge = ImageBucketSpec(
        key = "t~l",
        label = "Tablet Large",
        maxWidthPx = 2048,
        maxHeightPx = 2732,
        maxDensity = 3.0f,
    )

    // Sort specs by ascending order of maxWidthPx, maxHeightPx, and maxDensity
    val All = listOf(
        PhoneSmallest,
        Phone_5_0_Inch,
        PhoneAppleNormal,
        PhoneAppleXl,
        PhoneUHD,
        FoldableFlipOpen,
        TabletSmallest,
        TabletMedium,
        TabletLarge,
    )

    val Preset: ImageBucketSpec
        get() = PhoneAppleNormal
}