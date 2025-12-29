package wallapp.resource


actual fun getResourceUrl(resource: Resource): String? {
    return if (resource is Resource.UrlImage) {
        resource.url
    } else {
        null
    }
}
