package wallapp.deeplink

object DeepLinkSlugResolver {

    private const val Domain = "example.com"
    private const val SchemeHttp = "http"
    private const val SchemeHttps = "https"
    private const val BaseUrlHttp = "$SchemeHttp://$Domain/"
    private const val BaseUrlHttps = "$SchemeHttps://$Domain/"

    fun resolve(url: String): String? {
        if (!url.startsWith(BaseUrlHttp) && !url.startsWith(BaseUrlHttps)) {
            return null
        }

        return url
            .removePrefix(BaseUrlHttps)
            .removePrefix(BaseUrlHttp)
            .substringBefore('?')
            .let {
                if (it.endsWith('/')) {
                    it.removeSuffix("/")
                } else {
                    it
                }
            }
    }
}