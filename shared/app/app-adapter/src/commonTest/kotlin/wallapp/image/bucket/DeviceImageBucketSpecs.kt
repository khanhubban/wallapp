package wallapp.image.bucket

@Suppress("MemberVisibilityCanBePrivate")
object DeviceImageBucketSpecs {

    object Phone {
        val iPhone8 = IphoneBucketSpec("8", 750, 1334, deviceDensity = 2f)
        val iPhoneXR = IphoneBucketSpec("XR", 828, 1792, 2f)
        val iPhone13Mini = IphoneBucketSpec("13 Mini", 1125, 2436)
        val iPhone15Plus = IphoneBucketSpec("15 Plus", 1290, 2796)
        val iPhone15Pro = IphoneBucketSpec("15 Pro", 1179, 2556)
        val iPhone15ProMax = IphoneBucketSpec("15 Pro Max", 1290, 2796)

        val OnePlus5T = DeviceImageBucketSpec("OnePlus 5T", 1080, 2160, 3f)

        val Pixel1 = PixelBucketSpec("1", 1080, 1920, deviceDensity = 2.6f)
        val Pixel2Xl = PixelBucketSpec("2 XL", 1440, 2880, deviceDensity = 3.5f)
        val Pixel5 = PixelBucketSpec("5", 1080, 2340, deviceDensity = 2.75f)
        val Pixel6Pro = PixelBucketSpec("6 Pro", 1440, 3120, deviceDensity = 3.5f)

        val S20Plus = GalaxyBucketSpec("S20 Plus", 1080,  2400, 2.8125f)
        val S22Ultra = GalaxyBucketSpec("S22 Ultra", 1440, 3088, deviceDensity = 3f)
        val S24Ultra = GalaxyBucketSpec("S24 Ultra", 1440, 3120, deviceDensity = 3.75f)
    }

    object Foldable {
        val PixelFoldOpen = PixelBucketSpec("Pixel Fold (Open)", 2208, 1840, deviceDensity = 3.73f)
        val PixelFoldClosed = PixelBucketSpec("Pixel Fold (Closed)", 1080, 2092, deviceDensity = 4.00f)
        val ZFold5Open = GalaxyBucketSpec("Z Fold 5 (Open)", 2176, 1812, deviceDensity = 3.73f)
        val ZFold5Closed = GalaxyBucketSpec("Z Fold 5 (Closed)", 904, 2316, deviceDensity = 3.74f)
        val ZFlip5Open = GalaxyBucketSpec("Z Flip 5 (Open)", 1080, 2640, deviceDensity = 4.25f)
        val ZFlip5Closed = GalaxyBucketSpec("Z Flip 5 (Closed)", 720, 748, deviceDensity = 3.06f)
    }

    object Tablet {
        val iPadMiniGen6 = IpadBucketSpec("iPad Mini 6", 1488, 2266, 2.0f)
        val iPadAirGen5 = IpadBucketSpec("iPad Air (5th Gen)", 1640 , 2360, 2.0f)
        val iPadProGen4_11 = IpadBucketSpec("iPad Pro 11 (4th Gen)", 1668, 2388, 2.0f)
        val iPadProGen6_12_9 = IpadBucketSpec("Pro 12.9 (6th Gen)", 2048, 2732, 2.0f)
        val PixelTab = PixelTabBucketSpec("Pixel Tab", 1600, 2560, 2.76f)
        val SGalaxyTab_S9_Ultra = GalaxyTabBucketSpec("Samsung Galaxy Tab S9 Ultra", 1848, 2960, 2.39f)
        val SGalaxyTab_S9 = GalaxyTabBucketSpec("Samsung Galaxy Tab S9", 1600, 2560, 2.39f)
    }
}