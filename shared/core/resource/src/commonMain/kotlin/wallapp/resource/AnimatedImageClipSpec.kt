package wallapp.resource

import androidx.compose.runtime.Immutable
import co.touchlab.skie.configuration.annotations.SealedInterop
import kotlinx.serialization.Serializable

@Immutable
@Serializable
@SealedInterop.Enabled
sealed class AnimatedImageClipSpec {

    /**
     * Play the animation between these two frames. [maxInclusive] determines whether the animation
     * should play the max frame or stop one frame before it.
     */
    @Immutable
    @Serializable
    data class Frame(
        val min: Int? = null,
        val max: Int? = null,
        val maxInclusive: Boolean = true,
    ) : AnimatedImageClipSpec()

    /**
     * Play the animation between these two progress values.
     */
    @Immutable
    @Serializable
    data class Progress(
        val min: Float = 0f,
        val max: Float = 1f,
    ) : AnimatedImageClipSpec()

    companion object {
        val PresetFrame = Frame(
            min = 0,
            max = 1,
            maxInclusive = true,
        )

        val PresetProgress = Progress(
            min = 0f,
            max = 1f,
        )

        val Preset = PresetFrame
    }
}