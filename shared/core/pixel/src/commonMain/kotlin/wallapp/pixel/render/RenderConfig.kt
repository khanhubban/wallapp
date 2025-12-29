package wallapp.pixel.render

import wallapp.pixel.clickable.ClickableEffect

interface RenderConfig {

    val clickableEffect: ClickableEffect

    companion object {

        val Preset = object : RenderConfig {

            override val clickableEffect = ClickableEffect.Default
        }
    }
}