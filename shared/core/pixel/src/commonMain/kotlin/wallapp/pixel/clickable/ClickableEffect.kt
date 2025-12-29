package wallapp.pixel.clickable

sealed class ClickableEffect {

    data object Ripple : ClickableEffect()

    data object AlphaFade : ClickableEffect()

    companion object {
        /**
         * Always use [Ripple] for now because use of [AlphaFade] results in buttons that do not
         * respond to clicks.
         */
        val Default by lazy { Ripple }
//        val Default by lazy { if (PlatformFeature.IsIos) AlphaFade else Ripple }
    }
}