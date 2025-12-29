package wallapp.image.host

class ImageHostUrlMapperConfigMock : ImageHostUrlMapperConfig {

    override val hostPrefixes: List<String> = listOf(
        "https://<appname>.imgix.net/"
    )

    override val exclusionUrlsComponents: List<String>
        get() = emptyList()
}