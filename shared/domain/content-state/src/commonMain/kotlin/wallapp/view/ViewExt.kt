package wallapp.view

import wallapp.pixel.view.View

fun List<View>.subtractByInferredId(other: List<View>): List<View>? {
    val currentIds = mapNotNull { it.viewState.viewId }
        .distinct()
    if (currentIds.size != size) {
        return null
    }

    val otherIds = other
        .mapNotNull { it.viewState.viewId }
        .distinct()
    if (otherIds.size != other.size) {
        return null
    }

    return filter { it.viewState.viewId !in otherIds }
}