package wallapp.pixel.typeface

import androidx.compose.runtime.compositionLocalOf

val LocalTypefaceRepository =
    compositionLocalOf<TypefaceRepository> { error("No TypefaceRepository provided") }
