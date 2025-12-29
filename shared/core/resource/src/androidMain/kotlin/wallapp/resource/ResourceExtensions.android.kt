package wallapp.resource

import androidx.annotation.RawRes

actual fun getResourceUrl(resource: Resource): String? {
    return if (resource is Resource.UrlImage) {
        resource.url
    } else {
        null
    }
}

@get:RawRes
val Resource.rawRes: Int
    get() = (this as Resource.Raw).rawRes
