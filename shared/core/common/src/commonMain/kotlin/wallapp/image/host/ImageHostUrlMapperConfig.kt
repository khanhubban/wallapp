package wallapp.image.host

interface ImageHostUrlMapperConfig {

    val hostPrefixes: List<String>

    val exclusionUrlsComponents: List<String>
}