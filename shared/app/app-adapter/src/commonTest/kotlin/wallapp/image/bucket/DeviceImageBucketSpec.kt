package wallapp.image.bucket

data class DeviceImageBucketSpec(
    val deviceLabel: String,
    val widthPx: Int,
    val heightPx: Int,
    val density: Float,
)

fun IphoneBucketSpec(
    labelSuffix: String,
    widthPx: Int,
    heightPx: Int,
    deviceDensity: Float = 3.0f,
) = DeviceImageBucketSpec(
    widthPx = widthPx,
    heightPx = heightPx,
    density = deviceDensity,
    deviceLabel = "iPhone $labelSuffix",
)

fun IpadBucketSpec(
    labelSuffix: String,
    widthPx: Int,
    heightPx: Int,
    deviceDensity: Float = 3.0f,
) = DeviceImageBucketSpec(
    widthPx = widthPx,
    heightPx = heightPx,
    density = deviceDensity,
    deviceLabel = "iPhone $labelSuffix",
)

fun GalaxyBucketSpec(
    labelSuffix: String,
    widthPx: Int,
    heightPx: Int,
    deviceDensity: Float = 3.0f,
) = DeviceImageBucketSpec(
    widthPx = widthPx,
    heightPx = heightPx,
    density = deviceDensity,
    deviceLabel = "Galaxy $labelSuffix",
)

fun GalaxyTabBucketSpec(
    labelSuffix: String,
    widthPx: Int,
    heightPx: Int,
    deviceDensity: Float = 3.0f,
) = DeviceImageBucketSpec(
    widthPx = widthPx,
    heightPx = heightPx,
    density = deviceDensity,
    deviceLabel = "Galaxy $labelSuffix",
)

fun PixelBucketSpec(
    labelSuffix: String,
    widthPx: Int,
    heightPx: Int,
    deviceDensity: Float,
) = DeviceImageBucketSpec(
    widthPx = widthPx,
    heightPx = heightPx,
    density = deviceDensity,
    deviceLabel = "Pixel $labelSuffix",
)

fun PixelTabBucketSpec(
    labelSuffix: String,
    widthPx: Int,
    heightPx: Int,
    deviceDensity: Float,
) = DeviceImageBucketSpec(
    widthPx = widthPx,
    heightPx = heightPx,
    density = deviceDensity,
    deviceLabel = "Pixel $labelSuffix",
)
