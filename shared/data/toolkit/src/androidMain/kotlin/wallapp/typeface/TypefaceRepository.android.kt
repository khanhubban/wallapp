package wallapp.apptypeface

import android.content.Context
import androidx.core.content.res.ResourcesCompat
import wallapp.pixel.typeface.Typeface
import wallapp.pixel.typeface.TypefaceAndroid
import wallapp.pixel.typeface.TypefaceRepository
import wallapp.resource.Resource
import wallapp.resources.font.Fonts
import android.graphics.Typeface as TypefaceSystem

class TypefaceRepositoryAndroid(
    private val context: Context,
) : TypefaceRepository {

    private val cache: MutableMap<String, TypefaceAndroid?> = mutableMapOf()

    private fun getTypeface(key: String, fontResource: Resource?): Typeface? {
        val font = fontResource as? Resource.Font ?: return null
        synchronized(cache) {
            return cache.getOrPut(key) {
                val typeface: TypefaceSystem? = ResourcesCompat.getFont(context, font.fontRes)
                typeface?.let { TypefaceAndroid(it) }
            }
        }
    }

    override val regularTypeface: Typeface? by lazy {
        getTypeface("regularTypeface", Fonts.Regular)
    }
    override val boldTypeface: Typeface? by lazy {
        getTypeface("boldTypeface", Fonts.Bold)
    }
}