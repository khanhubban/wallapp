package wallapp.ads.reward.internal

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import wallapp.image.Image
import wallapp.image.ImageModel
import wallapp.resources.string.StringRepository
import wallapp.util.buildBaseDomainUrl
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class RewardAdInternalRepositoryPreset(
    private val config: RewardAdInternalRepositoryConfig,
    private val strings: StringRepository,
) : RewardAdInternalRepository {

    private val callToActionLabel: String
        get() = "Learn more"

    private fun buildCallToActionUrl(url: String): String {
        return if (config.useAttributionUrls) {
            url
        } else {
            buildBaseDomainUrl(url) ?: url
        }
    }

    private fun buildMediaModels(
        id: RewardAdInternalId,
        resolutionSuffix: String = "720p",
    ): Pair<ImageModel, Image> {
        val urlPrefix = "https://storage.googleapis.com/wallapp-assets/static/ad/${id.name}"
        val videoUrl = "${urlPrefix}/${resolutionSuffix}.mp4"
        val postPlaybackUrl = "${urlPrefix}/end.png"
        return ImageModel.from(videoUrl) to ImageModel.from(postPlaybackUrl).let { Image.from(it) }
    }

    private fun createRewardAdInternalSpec(
        id: RewardAdInternalId,
        callToActionUrl: String,
        minimumWatchDuration: Duration,
    ): RewardAdInternalSpec {
        val (videoModel, postPlaybackMedia) = buildMediaModels(id)

        return RewardAdInternalSpec(
            id = id,
            mediaImageModel = videoModel,
            postPlaybackMedia = postPlaybackMedia,
            callToActionUrl = buildCallToActionUrl(callToActionUrl),
            callToAction = callToActionLabel,
            minimumWatchDuration = minimumWatchDuration,
        )
    }

    fun demo1() = createRewardAdInternalSpec(
        id = RewardAdInternalId(
            name = "ad~demo~01",
            RewardAdInternalBrand.Demo,
        ),
        callToActionUrl = "https://example.com",
        minimumWatchDuration = 4.seconds,
    )

    private val presetRewardAdInternalSpecs by lazy {
        listOf(
            demo1(),
        )
            .also { it.validate() }
    }

    override val enabled: Flow<Boolean> = flowOf(true)

    override val allRewardAdInternalSpecs: StateFlow<List<RewardAdInternalSpec>> =
        MutableStateFlow(presetRewardAdInternalSpecs)

}