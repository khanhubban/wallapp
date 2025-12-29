package wallapp.render

import androidx.compose.runtime.Immutable
import wallapp.pixel.clickable.ClickableEffect
import wallapp.pixel.render.RenderConfig

@Immutable
object RenderConfigDefault : RenderConfig {

    override val clickableEffect: ClickableEffect
        get() = ClickableEffect.Default
}