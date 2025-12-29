package wallapp.ads.reward.internal

import wallapp.di.resolveDependency
import wallapp.image.ImageModel
import wallapp.image.imageUrl
import wallapp.test.WaeTest
import wallapp.test.waeTest
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class RewardAdInternalRepositoryPresetTest : WaeTest {

    @Test
    fun validateMediaUrls() = waeTest {
        val repository = resolveDependency<RewardAdInternalRepositoryPreset>()

        val specs = repository.allRewardAdInternalSpecs.value

        specs.forEach { spec ->
            val imageModel = spec.mediaImageModel
            assertTrue(imageModel is ImageModel.UrlVideo)
            val mediaUrl = imageModel.url
            assertNotNull(mediaUrl)
            // Format-only validation to keep the unit test offline.
            assertTrue(mediaUrl.startsWith("https://"), "Media URL is not https: $mediaUrl")
            assertTrue(ImageModel.isVideoUrl(mediaUrl), "Media URL is not a video: $mediaUrl")
            println("Media URL validated: $mediaUrl")

            val postPlaybackMedia = spec.postPlaybackMedia
            val postPlaybackMediaUrl = postPlaybackMedia.imageUrl
            assertNotNull(postPlaybackMediaUrl)
            assertTrue(postPlaybackMediaUrl.startsWith("https://"), "Media URL is not https: $postPlaybackMediaUrl")
            assertTrue(postPlaybackMediaUrl.endsWith(".png"), "Media URL is not a png: $postPlaybackMediaUrl")
            println("Post-playback URL validated: $postPlaybackMediaUrl")
        }
    }
}
