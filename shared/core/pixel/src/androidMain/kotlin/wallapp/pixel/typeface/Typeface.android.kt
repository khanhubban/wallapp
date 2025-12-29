package wallapp.pixel.typeface

import android.graphics.Typeface as TypefaceSystem

data class TypefaceAndroid(
    val typefaceSystem: TypefaceSystem,
) : Typeface

val Typeface.typefaceSystem: TypefaceSystem
    get() = (this as TypefaceAndroid).typefaceSystem