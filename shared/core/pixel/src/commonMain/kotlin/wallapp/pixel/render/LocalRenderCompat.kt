package wallapp.pixel.render

import androidx.compose.runtime.compositionLocalOf

/**
 * This exists solely for compatibility so [Render] can be accessed from legacy views.
 * All [Composable] functions should take this as an argument.
 */
val LocalRenderCompat =
    compositionLocalOf<Render> { error("No LocalRender provided") }
