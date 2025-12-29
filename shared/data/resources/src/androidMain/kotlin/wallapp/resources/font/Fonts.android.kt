package wallapp.resources.font

import wallapp.resource.Resource
import wallapp.resources.R

actual object Fonts {

    actual val Regular: Resource? by lazy { Resource.Font(R.font.app_font) }

    actual val Bold: Resource? = null
}