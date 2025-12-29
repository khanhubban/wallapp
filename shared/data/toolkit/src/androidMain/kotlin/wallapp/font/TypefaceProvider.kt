package wallapp.font

import android.content.Context
import android.graphics.Typeface


interface TypefaceProvider {
    fun getDefaultFont(context: Context): Typeface?
}