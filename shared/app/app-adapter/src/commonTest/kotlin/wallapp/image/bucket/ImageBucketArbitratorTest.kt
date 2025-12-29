package wallapp.image.bucket

import wallapp.image.bucket.DeviceImageBucketSpecs.Foldable
import wallapp.image.bucket.DeviceImageBucketSpecs.Phone
import wallapp.image.bucket.DeviceImageBucketSpecs.Tablet
import wallapp.image.bucket.ImageBucketArbitrator.arbitrateImageBucketSpec
import kotlin.test.Test
import kotlin.test.assertEquals


class ImageBucketArbitratorTest {

    private fun arbitrateImageBucketSpec(deviceSpec: DeviceImageBucketSpec): ImageBucketSpec =
        arbitrateImageBucketSpec(
            deviceWidthPx = deviceSpec.widthPx,
            deviceHeightPx = deviceSpec.heightPx,
            deviceDensity = deviceSpec.density,
        )

    private fun assertEquals(expected: ImageBucketSpec, deviceSpec: DeviceImageBucketSpec) {
        assertEquals(expected, arbitrateImageBucketSpec(deviceSpec), "Failed for `$deviceSpec`")
    }

    @Test fun `OnePlus 5T`() {
        assertEquals(ImageBucketSpecs.PhoneAppleNormal, Phone.OnePlus5T)
    }

    @Test fun `Pixel 1`() {
        assertEquals(ImageBucketSpecs.Phone_5_0_Inch, Phone.Pixel1)
    }

    @Test fun `Pixel 2 XL`() {
        assertEquals(ImageBucketSpecs.PhoneUHD, Phone.Pixel2Xl)
    }

    @Test fun `Pixel 5`() {
        assertEquals(ImageBucketSpecs.PhoneAppleNormal, Phone.Pixel5)
    }

    @Test fun `Pixel 6 Pro`() {
        assertEquals(ImageBucketSpecs.PhoneUHD, Phone.Pixel6Pro)
    }

    @Test fun `iPhone 8`() {
        assertEquals(ImageBucketSpecs.PhoneSmallest, Phone.iPhone8)
    }

    @Test fun `iPhone XR`() {
        // The XR will look pretty bad, but such is the hardware
        assertEquals(ImageBucketSpecs.PhoneSmallest, Phone.iPhoneXR)
    }

    @Test fun `iPhone 13 Mini`() {
        assertEquals(ImageBucketSpecs.PhoneAppleNormal, Phone.iPhone13Mini)
    }

    @Test fun `iPhone 15 Plus`() {
        assertEquals(ImageBucketSpecs.PhoneAppleXl, Phone.iPhone15Plus)
    }

    @Test fun `iPhone 15 Pro`() {
        assertEquals(ImageBucketSpecs.PhoneAppleNormal, Phone.iPhone15Pro)
    }

    @Test fun `iPhone 15 Pro Max`() {
        assertEquals(ImageBucketSpecs.PhoneAppleXl, Phone.iPhone15ProMax)
    }

    @Test fun `S20 Plus`() {
        assertEquals(ImageBucketSpecs.PhoneAppleNormal, Phone.S20Plus)
    }

    @Test fun `S22 Ultra`() {
        assertEquals(ImageBucketSpecs.PhoneUHD, Phone.S22Ultra)
    }

    @Test fun `S24 Ultra`() {
        assertEquals(ImageBucketSpecs.PhoneUHD, Phone.S24Ultra)
    }

    @Test fun `iPad Mini Gen6`() {
        assertEquals(ImageBucketSpecs.TabletMedium, Tablet.iPadMiniGen6)
    }

    @Test fun `iPad Air Gen5`() {
        assertEquals(ImageBucketSpecs.TabletMedium, Tablet.iPadAirGen5)
    }

    @Test fun `iPad Pro Gen4 11`() {
        assertEquals(ImageBucketSpecs.TabletMedium, Tablet.iPadProGen4_11)
    }

    @Test fun `iPad Pro Gen6 12_9`() {
        assertEquals(ImageBucketSpecs.TabletLarge, Tablet.iPadProGen6_12_9)
    }

    @Test fun `Pixel Tab`() {
        assertEquals(ImageBucketSpecs.TabletMedium, Tablet.PixelTab)
    }

    @Test fun `SGalaxyTab S9 Ultra`() {
        assertEquals(ImageBucketSpecs.TabletMedium, Tablet.SGalaxyTab_S9_Ultra)
    }

    @Test fun `SGalaxyTab S9`() {
        assertEquals(ImageBucketSpecs.TabletMedium, Tablet.SGalaxyTab_S9)
    }

    @Test fun `Z Fold5 Open`() {
        assertEquals(ImageBucketSpecs.FoldableFlipOpen, Foldable.ZFold5Open)
    }

    @Test fun `Z Fold5 Close`() {
        assertEquals(ImageBucketSpecs.PhoneUHD, Foldable.ZFold5Closed)
    }

    @Test fun `Pixel Fold Open`() {
        assertEquals(ImageBucketSpecs.FoldableFlipOpen, Foldable.PixelFoldOpen)
    }

    @Test fun `Pixel Fold Close`() {
        assertEquals(ImageBucketSpecs.PhoneUHD, Foldable.PixelFoldClosed)
    }

    @Test fun `Z Flip5 Open`() {
        assertEquals(ImageBucketSpecs.FoldableFlipOpen, Foldable.ZFlip5Open)
    }

    @Test fun `Z Flip5 Close`() {
        assertEquals(ImageBucketSpecs.PhoneUHD, Foldable.ZFlip5Closed)
    }

}
