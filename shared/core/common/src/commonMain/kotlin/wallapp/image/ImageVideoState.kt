package wallapp.image

import androidx.compose.runtime.Immutable
import co.touchlab.skie.configuration.annotations.SealedInterop

@Immutable
@SealedInterop.Enabled
sealed class ImageVideoState {

    abstract val playbackCallbacks: ImageVideoPlaybackCallbacks?
    abstract val isLooping: Boolean
    abstract val pausePlayback: Boolean
    abstract val useAudio: Boolean

    @Immutable
    data class Playback(
        val seekToMillisOnLoop: Int?,
        override val useAudio: Boolean,
        override val pausePlayback: Boolean = false,
        val startPositionMillis: Int? = null,
        override val playbackCallbacks: ImageVideoPlaybackCallbacks? = null,
    ) : ImageVideoState() {
        constructor(
            isLooping: Boolean,
            useAudio: Boolean,
            pausePlayback: Boolean = false,
            startPositionMillis: Int? = null,
            playbackCallbacks: ImageVideoPlaybackCallbacks? = null,
        ) : this(
            seekToMillisOnLoop = if (isLooping) 0 else null,
            useAudio = useAudio,
            pausePlayback = pausePlayback,
            startPositionMillis = startPositionMillis,
            playbackCallbacks = playbackCallbacks,
        )

        override val isLooping: Boolean
            get() = seekToMillisOnLoop != null
    }
}