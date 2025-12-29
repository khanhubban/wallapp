package wallapp.content.model

sealed class WallpaperCategoryType {

    abstract val name: String

    data object Singles : WallpaperCategoryType() {
        override val name = "Singles"

        const val IdSuffix = "~singles"
    }

    data object Collection : WallpaperCategoryType() {
        override val name = "Collection"
    }

    companion object {
        /**
         * This must be accessed lazily to avoid initialization order issues. #742.
         */
        val All by lazy { listOf(Singles, Collection) }

        fun fromName(name: String): WallpaperCategoryType {
            return All.first { it.name == name }
        }
    }
}

fun List<Id.CategoryId>.splitByCategoryType(): Pair<List<Id.CategoryId>?, List<Id.CategoryId>?> {
    val singles = this
        .filter { it.name.endsWith(WallpaperCategoryType.Singles.IdSuffix) }
//    require(singles.isEmpty() || singles.size == 1) { "A maximum of one single category is allowed (${singles.size})" }

    val collections = this
        .filter { it !in singles }
        .ifEmpty { null }

    return singles.ifEmpty { null } to collections
}

fun List<Id.CategoryId>.filterByCategoryType(categoryType: WallpaperCategoryType): List<Id.CategoryId>? {
    val (singles, collections) = splitByCategoryType()
    return when (categoryType) {
        WallpaperCategoryType.Singles -> singles
        WallpaperCategoryType.Collection -> collections
    }
}

fun List<Id.CategoryId>.filterByCategoryTypeSingles(): List<Id.CategoryId>?
        = filterByCategoryType(WallpaperCategoryType.Singles)
fun List<Id.CategoryId>.filterByCategoryTypeCollection(): List<Id.CategoryId>?
        = filterByCategoryType(WallpaperCategoryType.Collection)