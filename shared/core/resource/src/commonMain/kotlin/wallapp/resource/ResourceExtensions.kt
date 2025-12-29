package wallapp.resource


expect fun getResourceUrl(resource: Resource): String?

val Resource.url: String?
    get() = getResourceUrl(this)
