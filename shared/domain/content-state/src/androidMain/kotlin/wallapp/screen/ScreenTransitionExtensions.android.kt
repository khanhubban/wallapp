package wallapp.screen

import wallapp.pixel.render.Render
import wallapp.screen.ScreenTransition.ScreenTransitionFade
import wallapp.screen.ScreenTransition.ScreenTransitionNone
import wallapp.screen.ScreenTransition.ScreenTransitionSlideVertically


fun Screen.screenTransition(render: Render): ScreenTransition {
    val existing = screenTransitionItem
    if (existing != null) {
        return existing as ScreenTransition
    }

    val label = routeFormat
    return when (screenTransitionType) {
        ScreenTransitionType.None -> ScreenTransitionNone(label)
        ScreenTransitionType.Fade -> ScreenTransitionFade(label)
        ScreenTransitionType.SlideVertically -> ScreenTransitionSlideVertically(render, label)
    }.also {
        screenTransitionItem = it
    }
}