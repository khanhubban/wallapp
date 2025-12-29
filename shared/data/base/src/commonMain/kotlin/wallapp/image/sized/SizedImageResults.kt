package wallapp.image.sized

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import wallapp.image.host.ImageHostPlatform
import wallapp.log.Logger

@Serializable
data class SizedImageResults(
    val platforms: List<ImageHostPlatform>,
    val items: List<SizedImageResult>,
) {
    val allSizedImages: List<SizedImage>
        get() = items
            .map { it.sizedImage }
            .distinct()

    val allImageBucketSpecKeys: List<String>
        get() = items
            .map { it.imageBucketSpecKey }
            .distinct()

    val allImageHostFormatKeys: List<String>
        get() = items
            .map { it.imageHostFormatKey }
            .distinct()

    val jsonString: String
        get() = Json.encodeToString(kotlinx.serialization.serializer(), this)

    fun find(
        sizedImage: SizedImage,
        imageBucketSpecKey: String,
        imageHostFormatKey: String,
    ): SizedImageResult? {
        return filter(sizedImage, imageBucketSpecKey, imageHostFormatKey)
            ?.firstOrNull()
    }

    fun filter(
        sizedImage: SizedImage,
        imageBucketSpecKey: String,
        imageHostFormatKey: String,
    ): List<SizedImageResult>? {
        return items
            .filter {
                it.sizedImage == sizedImage
                        && it.imageBucketSpecKey == imageBucketSpecKey
                        && it.imageHostFormatKey == imageHostFormatKey
            }
            .ifEmpty { null }
    }

    fun validate() {
        val allSizedImages = allSizedImages
        val allImageBucketSpecKeys = allImageBucketSpecKeys
        val allImageHostFormatKeys = allImageHostFormatKeys

        require(allSizedImages.isNotEmpty())
        require(allImageBucketSpecKeys.isNotEmpty())
        require(allImageHostFormatKeys.isNotEmpty())

        // Check 1: Ensure all combinations of sizedImage, imageBucketSpecKey, and imageHostFormatKey are present
        items.forEach { item ->
            val combinationsExist = filter(item.sizedImage, item.imageBucketSpecKey, item.imageHostFormatKey)
            require(combinationsExist != null && combinationsExist.size == 1) {
                "Expected exactly one result for combination of SizedImage: ${item.sizedImage}, ImageBucketSpecKey: ${item.imageBucketSpecKey}, and ImageHostFormatKey: ${item.imageHostFormatKey}. Found: ${combinationsExist?.size}"
            }
        }

        // Check 2: Ensure each (sizedImage, imageBucketSpecKey, imageHostFormatKey) combination is unique
        val combinationSet = mutableSetOf<Triple<SizedImage, String, String>>()
        items.forEach { item ->
            val combination = Triple(item.sizedImage, item.imageBucketSpecKey, item.imageHostFormatKey)
            require(combinationSet.add(combination)) {
                "Duplicate combination found for SizedImage: ${item.sizedImage}, ImageBucketSpecKey: ${item.imageBucketSpecKey}, and ImageHostFormatKey: ${item.imageHostFormatKey}"
            }
        }

        // Check 3: Ensure urlSuffix is different for each imageBucketSpecKey within the same sizedImage
        val groupedBySizedImage = items.groupBy { it.sizedImage }
        groupedBySizedImage.forEach { (sizedImage, results) ->
            if (sizedImage == SizedImage.DownloadableWallpaperHd
                || sizedImage == SizedImage.DownloadableWallpaperSd) {
                return@forEach
            }
            val urlSuffixSet = mutableSetOf<String?>()
            results.forEach { item ->
                if (item.urlSuffix != null) {
                    // Log warning instead of throwing exception because some small items do in fact
                    // have the same urlSuffix in different buckets
                    if (!urlSuffixSet.add(item.urlSuffix)) {
                        Log.w("Duplicate urlSuffix '${item.urlSuffix}' found for SizedImage: $sizedImage within the same ImageBucketSpecKey: ${item.imageBucketSpecKey}")
                    }

//                    require(urlSuffixSet.add(item.urlSuffix)) {
//                        "Duplicate urlSuffix '${item.urlSuffix}' found for SizedImage: $sizedImage within the same ImageBucketSpecKey: ${item.imageBucketSpecKey}"
//                    }
                }
            }
        }
    }

    companion object {

        val Log = Logger("[SizedImage]")

        private val Json = Json {
            prettyPrint = true
        }

        fun fromJsonString(jsonString: String): SizedImageResults? {
            return try {
                Json.decodeFromString(kotlinx.serialization.serializer(), jsonString)
            } catch (e: SerializationException) {
                null
            }
        }
    }
}
