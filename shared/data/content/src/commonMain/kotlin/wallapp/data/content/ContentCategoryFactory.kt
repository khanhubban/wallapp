package wallapp.data.content

import wallapp.content.model.ContentCategory
import wallapp.content.model.ContentCategorySpec
import wallapp.resources.string.Strings

class ContentCategoryFactory(
    private val strings: Strings,
) {
    val all: List<ContentCategorySpec>
        get() = listOf(
            contentCategoryCollections,
            contentCategorySingles,
        )

    val contentCategorySingles: ContentCategorySpec
        get() = ContentCategorySpec(
            contentCategory = ContentCategory.Singles,
            label = strings.singles,
        )

    val contentCategoryCollections: ContentCategorySpec
        get() = ContentCategorySpec(
            contentCategory = ContentCategory.Collection,
            label = strings.collections,
        )

}