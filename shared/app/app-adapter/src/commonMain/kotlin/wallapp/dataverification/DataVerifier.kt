package wallapp.dataverification

import io.ktor.client.HttpClient
import io.ktor.client.request.head
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import wallapp.content.model.Wallpaper
import wallapp.data.content.media.ContentMediaGetResult
import wallapp.data.content.media.ContentMediaRepository
import wallapp.data.model.ModelRepository
import wallapp.data.wallpaper.StaticWallpaperSize
import wallapp.image.bucket.ImageBucketSpec
import wallapp.image.host.ImageHostPlatform
import wallapp.mediamap.MediaMapRepositoryDefault
import wallapp.string.quote

class DataVerifier(
    private val httpClient: HttpClient,
    private val modelRepository: ModelRepository,
    val mediaMapRepository: MediaMapRepositoryDefault,
    private val contentMediaRepository: ContentMediaRepository,
) {
    val Wallpaper.staticWallpaperSizes: List<StaticWallpaperSize>
        get() {
            return if (this.isSingle) {
                listOf(StaticWallpaperSize.StandardResolution, StaticWallpaperSize.FullResolution)
            } else {
                listOf(StaticWallpaperSize.FullResolution)
            }
        }

    suspend fun checkUrlIsAccessible(url: String): HttpStatusCode {
        val response: HttpResponse = httpClient.head(url)
        return response.status// == HttpStatusCode.OK
    }

    suspend fun verifyWallpapersHaveValidDownloadUrls(
        verifyUrlStatus: Boolean,
        // This can be removed when this function is updated to dynamically change the bucket at runtime
        imageBucketSpecs: List<ImageBucketSpec> = listOf(mediaMapRepository.currentImageBucketSpec.value!!),
    ): DataVerificationResults {
//        val imageBucketSpecs = ImageBucketSpecs.All

        val allWallpapers = modelRepository.allRemixes
            .filter { it.size > 500 }
            .first()
//            .filter { it.artistId.name.contains("a~adam") }
        mediaMapRepository.isReady.filter { it }.first()

        val platforms = ImageHostPlatform.entries

        val successResults = mutableListOf<DataVerificationResult.Success>()
        val errorResults = mutableListOf<DataVerificationResult.Error>()
        val mediaMapUrls = mutableListOf<String>()

        allWallpapers.forEach { wallpaper ->
            for (platform in platforms) {
                for (imageBucketSpec in imageBucketSpecs) {
                    // Now configure the image host platform and image bucket spec

                    require(imageBucketSpec == mediaMapRepository.currentImageBucketSpec.value) {
                        "ImageBucketSpec mismatch: ${imageBucketSpec.label} != ${mediaMapRepository.currentImageBucketSpec.value?.label} - this code must be updated to dynamically change the bucket at runtime"
                    }

                    for (staticWallpaperSize in wallpaper.staticWallpaperSizes) {
                        val getResult = contentMediaRepository
                            .getWallpaperMedia(wallpaper, staticWallpaperSize).first()
                        val logKey = "[${wallpaper.id.name.quote()} / $staticWallpaperSize / $platform / ${imageBucketSpec.label}]"
                        when (getResult) {
                            is ContentMediaGetResult.Success -> {
                                val url = getResult.imageModel.url
                                if (verifyUrlStatus) {
                                    println("Verifiying URL: $url")
                                    val headResult = httpClient.head(url)
                                    if (headResult.status == HttpStatusCode.OK) {
                                        successResults.add(DataVerificationResult.Success("$logKey: mediaId: ${getResult.mediaId.id}, $url"))
                                    } else {
                                        errorResults.add(DataVerificationResult.Error("$logKey: URL not accessible (${headResult.status.value} / ${headResult.status.description}) ($url)"))
                                    }
                                } else {
                                    successResults.add(DataVerificationResult.Success("$logKey: mediaId: ${getResult.mediaId.id}, (unverified) $url"))
                                }
                            }

                            is ContentMediaGetResult.Error -> {
                                errorResults.add(DataVerificationResult.Error("$logKey: No media URL found, ${getResult.errorMessage}"))
                            }
                        }
                    }
                }
            }
        }

        return DataVerificationResults(
            successResults.sortedBy { it.message },
            errorResults.sortedBy { it.message },
        )
    }
}