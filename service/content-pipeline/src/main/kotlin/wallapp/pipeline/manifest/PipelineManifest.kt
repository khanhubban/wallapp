package wallapp.pipeline.manifest

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class PipelineManifest(
    val version: String,
    val baseUrl: String,
    val artist: ManifestArtist,
    val folder: ManifestFolder,
    val wallpapers: List<ManifestWallpaper>,
) {
    companion object {
        private val json = Json { ignoreUnknownKeys = true }
        fun parse(text: String): PipelineManifest = json.decodeFromString(text)
    }
}

@Serializable
data class ManifestArtist(val id: String, val label: String, val profileImagePath: String)

@Serializable
data class ManifestFolder(
    val id: String, val title: String,
    val profileImagePath: String, val featureBannerImagePath: String,
)

@Serializable
data class ManifestWallpaper(
    val id: String,
    val label: String,
    val isDark: Boolean,
    val width: Int,
    val height: Int,
    val downloadRenditionPath: String,
    val previewRenditionPath: String,
    val styles: List<String> = emptyList(),
    val tags: List<String> = emptyList(),
    val colors: List<String> = emptyList(),
)
