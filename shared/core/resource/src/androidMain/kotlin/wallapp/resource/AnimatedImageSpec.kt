package wallapp.resource

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class AnimatedImageSpec(
    val resource: Resource,
    val speed: Float = 1f,
    val infiniteRepeat: Boolean = false,
    val clipSpec: AnimatedImageClipSpec? = null,
    val startAnimation: Boolean = true,
    val animationStarted: () -> Unit = {},
    val animationCompleted: () -> Unit = {},
    val animationProgressUpdates: (Float) -> Unit = {},
) {

    override fun toString(): String {
        return resource.toString()
    }

    companion object {
        val Preset = AnimatedImageSpec(
            resource = Resource.Drawable(-1),
            speed = 1f,
            infiniteRepeat = false,
            clipSpec = AnimatedImageClipSpec.Preset,
        )
    }
}
