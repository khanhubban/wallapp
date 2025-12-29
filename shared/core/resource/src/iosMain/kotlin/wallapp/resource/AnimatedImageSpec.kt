package wallapp.resource

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable
import wallapp.graphics.Color

@Immutable
@Serializable
data class AnimatedImageSpec(
    val resource: Resource,
    val speed: Float,
    val infiniteRepeat: Boolean,
    val clipSpec: AnimatedImageClipSpec?,
    /**
     * Workaround for Kottie / #716.
     */
    private val backgroundColorInt: ULong?,
    /**
     * Specify this resource should forcibly render at a 'fill' aspect ratio. Useful if an animation
     * is supposed to fill bounds, but the aspect ratio of the animated image will cause clipping on
     * certain devices. An example being the tall Celebration animation having blank space on the
     * edges of the squarer iPhone SE. #1651.
     */
    val fillAspectRatio: Boolean = false,
    val startAnimation: Boolean = true,
    val animationStarted: () -> Unit = {},
    val animationCompleted: () -> Unit = {},
    val animationProgressUpdates: (Float) -> Unit = {},
) {

    val id: String
        get() = resource.toString()

    constructor(
        resource: Resource,
        speed: Float = 1f,
        infiniteRepeat: Boolean = false,
        backgroundColor: Color? = null,
        clipSpec: AnimatedImageClipSpec? = null,
        fillAspectRatio: Boolean = false,
    ) : this(
        resource = resource,
        speed = speed,
        infiniteRepeat = infiniteRepeat,
        clipSpec = clipSpec,
        backgroundColorInt = backgroundColor?.value,
        fillAspectRatio = fillAspectRatio,
    )

    val backgroundColor: Color? by lazy {
        backgroundColorInt?.let { Color(it) }
    }
}
