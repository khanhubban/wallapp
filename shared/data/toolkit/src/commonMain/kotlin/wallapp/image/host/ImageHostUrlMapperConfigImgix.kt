package wallapp.image.host

class ImageHostUrlMapperConfigImgix : ImageHostUrlMapperConfig {

    private val enablePreBuiltImageUrls: Boolean
        get() = true

    override val hostPrefixes: List<String> = listOf(
        "https://<appname>.imgix.net",
    )

    override val exclusionUrlsComponents: List<String>
        get() = if (enablePreBuiltImageUrls) {
            emptyList()
        } else {
            listOf(
                // Excluded to work around bug when profile image resizes on a toolbar
                "/static/artists/",
            )
        }
}