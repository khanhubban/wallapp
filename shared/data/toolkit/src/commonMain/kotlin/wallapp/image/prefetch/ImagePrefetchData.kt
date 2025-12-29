package wallapp.image.prefetch

import androidx.compose.runtime.Immutable

@Immutable
data class ImagePrefetchData(
    val entries: List<ImagePrefetchEntry>?,
) {
    val size: Int
        get() = entries?.size ?: 0

    val debugStringShort: String
        get() {
            val size = size
            return "ImagePrefetchData(size=$size, items=${entries?.map { it.debugStringShort }})"
        }
}


fun combineImagePrefetchData(first: ImagePrefetchData?, second: ImagePrefetchData?): ImagePrefetchData? {
    val entries = ((first?.entries ?: emptyList()) + (second?.entries ?: emptyList()))
        .distinct()
        .ifEmpty { null }
    return entries?.let { ImagePrefetchData(it) }
}