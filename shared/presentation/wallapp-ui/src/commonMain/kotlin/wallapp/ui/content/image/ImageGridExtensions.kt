package wallapp.ui.content.image

import wallapp.math.splitList
import wallapp.pixel.image.ImageViewState

fun List<ImageViewState>.mapToImageStackItems(): List<ImageGridItem>? {
    val unsplit = this
    return if (unsplit.isEmpty()) {
        null
    } else if (unsplit.size > 3) {
        unsplit.take(3)
    } else {
        unsplit
    }.map {
        ImageGridItem(it)
    }
}


fun List<ImageViewState>.mapToImageGridItems(): List<List<ImageGridItem>>? {
    val unsplit = this
    val splitImages = if (unsplit.isEmpty()) {
        null
    } else if (unsplit.size == 1) {
        listOf(unsplit)
    } else {
        unsplit.splitList(numLists = 1)
    }

    return splitImages?.map { row ->
        row.map { imageViewState ->
            ImageGridItem(imageViewState)
        }
    }
}
