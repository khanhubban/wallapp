package wallapp.content.state.rewardadinternal

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import wallapp.test.WaeTest
import wallapp.test.waeTest
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds


class RewardAdInternalPlaybackTimeHelperTest : WaeTest {

    class RewardAdInternalPlaybackTimeHelperConfigTest : RewardAdInternalPlaybackTimeHelperConfig {
        override val canIncrementPlaybackTime: MutableStateFlow<Boolean> = MutableStateFlow(false)
        override val minPlaybackDuration: MutableStateFlow<Duration?> = MutableStateFlow(15.seconds)
        override val currentEpochInSeconds: MutableStateFlow<Long> = MutableStateFlow(0L)
    }

    private fun TestScope.RewardAdInternalPlaybackTimeHelper(
        config: RewardAdInternalPlaybackTimeHelperConfigTest,
    ) = RewardAdInternalPlaybackTimeHelper(config, backgroundScope)

    @Test
    fun `test initial playback duration`() = waeTest {
        val config = RewardAdInternalPlaybackTimeHelperConfigTest()
        val helper = RewardAdInternalPlaybackTimeHelper(config)

        assertEquals(0.seconds, helper.currentPlaybackDuration.first())
        assertEquals(15.seconds, helper.playbackRemainingDuration.first())
    }

    @Ignore
    @Test
    fun `test incrementing currentEpochInSeconds when canIncrementPlaybackTime is true`() = waeTest {
        val config = RewardAdInternalPlaybackTimeHelperConfigTest()
        val helper = RewardAdInternalPlaybackTimeHelper(config)

        config.canIncrementPlaybackTime.value = true
        config.currentEpochInSeconds.value = 1L

        assertEquals(1.seconds, helper.currentPlaybackDuration.first { it == 1.seconds })
        assertEquals(14.seconds, helper.playbackRemainingDuration.first { it == 14.seconds })
    }

    @Ignore
    @Test
    fun `test playback duration does not increment when canIncrementPlaybackTime is false`() = waeTest {
        val config = RewardAdInternalPlaybackTimeHelperConfigTest()
        val helper = RewardAdInternalPlaybackTimeHelper(config)

        config.canIncrementPlaybackTime.value = true
        config.currentEpochInSeconds.value = 1L

        assertEquals(1.seconds, helper.currentPlaybackDuration.first { it == 1.seconds })
        assertEquals(14.seconds, helper.playbackRemainingDuration.first { it == 14.seconds })

        config.canIncrementPlaybackTime.value = false
        config.currentEpochInSeconds.value = 2L

        assertEquals(1.seconds, helper.currentPlaybackDuration.first()) // No increment
        assertEquals(14.seconds, helper.playbackRemainingDuration.first()) // No decrement
    }

    @Ignore
    @Test
    fun `test resuming playback duration after canIncrementPlaybackTime is set to true`() = waeTest {
        val config = RewardAdInternalPlaybackTimeHelperConfigTest()
        val helper = RewardAdInternalPlaybackTimeHelper(config)

        config.canIncrementPlaybackTime.value = true
        config.currentEpochInSeconds.value = 2L

        assertEquals(2.seconds, helper.currentPlaybackDuration.first { it == 2.seconds })
        assertEquals(13.seconds, helper.playbackRemainingDuration.first { it == 13.seconds })

        config.canIncrementPlaybackTime.value = false
        config.currentEpochInSeconds.value = 3L

        assertEquals(2.seconds, helper.currentPlaybackDuration.first()) // No increment
        assertEquals(13.seconds, helper.playbackRemainingDuration.first()) // No decrement

        config.currentEpochInSeconds.value = 4L
        config.currentEpochInSeconds.value = 5L
        config.currentEpochInSeconds.value = 6L
        config.canIncrementPlaybackTime.value = true
        config.currentEpochInSeconds.value = 7L

        assertEquals(3.seconds, helper.currentPlaybackDuration.first { it == 3.seconds }) // Increment resumes
        assertEquals(12.seconds, helper.playbackRemainingDuration.first { it == 12.seconds }) // Decrement resumes
    }
}