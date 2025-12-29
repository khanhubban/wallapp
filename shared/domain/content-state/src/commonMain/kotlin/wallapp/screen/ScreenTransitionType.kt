package wallapp.screen


sealed class ScreenTransitionType {
    data object None : ScreenTransitionType()
    data object Fade : ScreenTransitionType()
    data object SlideVertically : ScreenTransitionType()
}