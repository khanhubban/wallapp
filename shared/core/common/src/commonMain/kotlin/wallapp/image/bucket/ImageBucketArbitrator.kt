package wallapp.image.bucket

object ImageBucketArbitrator {

    fun arbitrateImageBucketSpec(
        deviceWidthPx: Int,
        deviceHeightPx: Int,
        deviceDensity: Float,
    ): ImageBucketSpec {
        val sortedBuckets = ImageBucketSpecs.All.sortedWith(
            compareBy(
                { it.maxWidthPx },
                { it.maxHeightPx },
                { it.maxDensity }
            )
        )

        // Find the first bucket where the device specs exceed the bucket specs
        val suitableBucket = sortedBuckets.firstOrNull { bucket ->
            deviceWidthPx <= bucket.maxWidthPx &&
                    deviceHeightPx <= bucket.maxHeightPx &&
                    deviceDensity <= bucket.maxDensity
        }

        return suitableBucket ?: sortedBuckets.last()
    }}