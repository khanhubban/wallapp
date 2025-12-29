package wallapp.image.prefetch

interface ImagePrefetchManager {

    fun updateImagesToPrefetch(screenId: Any, data: ImagePrefetchData?)
    fun updateScreenId(screenId: Any)
}
