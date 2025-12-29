package wallapp.data.wallpaper

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import wallapp.content.model.Id
import wallapp.content.model.Id.CategoryId
import wallapp.content.model.Id.DesignId
import wallapp.content.model.Id.RemixId
import wallapp.content.model.Ids
import wallapp.content.model.WallpaperCategory
import wallapp.content.model.WallpaperCategoryWithRemixes
import wallapp.content.model.WallpaperItem
import wallapp.content.model.WallpaperRemix

abstract class WallpaperRepositoryImpl : WallpaperRepository {

    override val refreshCounter = MutableStateFlow(0)

    override fun getRemix(remixId: RemixId): Flow<WallpaperRemix?> =
        remixes
            .map { remixes ->
                remixes.find { it.id == remixId }
            }

    override fun getRemixes(remixIds: List<RemixId>): Flow<List<WallpaperRemix>?> =
        remixes
            .map { remixes ->
                remixes
                    .filter { remixIds.contains(it.id) }
                    .ifEmpty { null }
            }

//    override fun getRemixesForDesign(designId: DesignId): Flow<List<WallpaperRemix>?> =
//        remixes
//            .map{ remixes ->
//                remixes.filter { it.designId == designId }
//            }

    private fun WallpaperCategory.getOrderedRemixes(remixes: List<WallpaperRemix>): List<WallpaperRemix>? {
        return remixIds
            .mapNotNull { remixId -> remixes.find { remixId == it.id } }
            .ifEmpty { null }
    }

    override fun getRemixesForCategory(categoryId: CategoryId): Flow<List<WallpaperRemix>?> =
        combine(
            getCategory(categoryId),
            remixes,
        ) { category, remixes ->
            category?.getOrderedRemixes(remixes)
        }

    override fun getCategory(categoryId: CategoryId): Flow<WallpaperCategory?> =
        categories
            .map { categories ->
                categories.find { it.id == categoryId }
            }

    override fun getCategories(categoryIds: List<CategoryId>): Flow<List<WallpaperCategory>?> =
        categories
            .map { categories ->
                categories.filter { categoryIds.contains(it.id) }
                    .ifEmpty { null }
            }

    override val categoriesWithRemixes: Flow<List<WallpaperCategoryWithRemixes>> by lazy {
        combine(categories, remixes) { categories, remixes ->
            categories.mapNotNull { category ->
                val orderedRemixes = category.getOrderedRemixes(remixes)
                if (orderedRemixes.isNullOrEmpty()) {
                    null
                } else {
                    WallpaperCategoryWithRemixes(
                        category = category,
                        remixes = orderedRemixes,
                    )
                }
            }
        }
    }

    override fun getItems(ids: Ids): Flow<List<WallpaperItem>?> =
        allWallpaperItems.map {
            it.filter { item -> ids.ids.contains(item.id) }
        }

    override fun getItem(id: Id): Flow<WallpaperItem?> {
        require(id is RemixId || id is DesignId || id is CategoryId) {
            "Item ${id::class.simpleName} is not a WallpaperItem}"
        }
        return allWallpaperItems.map {
            it.find { item -> item.id == id }
        }
    }
}