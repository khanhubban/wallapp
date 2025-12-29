package wallapp.image.cache

import wallapp.math.closestValue

data class ImageCacheKeyItem(
    val model: Any,
    val key: Any,
    val width: Int,
    val height: Int,
) {
    val size: Int
        get() = width * height
}


fun List<ImageCacheKeyItem>.getExactCached(width: Int, height: Int): ImageCacheKeyItem? =
    find { it.width == width && it.height == height }

fun List<ImageCacheKeyItem>.getBestCached(width: Int, height: Int): ImageCacheKeyItem? {
    if (isEmpty()) return null

    // First, try and find a cached item with this exact size
    find { it.width == width && it.height == height }?.also {
        return it
    }

    val desiredSize = width * height

    // Failing that, find the closest item
    val closestSize = map { it.size }.closestValue(desiredSize)
    return find { it.size == closestSize }
}
