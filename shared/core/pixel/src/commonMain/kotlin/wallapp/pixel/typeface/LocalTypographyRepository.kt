package wallapp.pixel.typeface

import androidx.compose.material3.Typography
import androidx.compose.runtime.compositionLocalOf

/**
 * Defines a typography to be used by 3rd party libraries that do not have access with the
 * custom [wallapp.pixel.text.Text] rendering (and thus may have incorrectly sized text using the
 * default app typography.
 */
val LocalTypography3rdParty =
    compositionLocalOf<Typography> { error("No TypefaceRepository provided") }
