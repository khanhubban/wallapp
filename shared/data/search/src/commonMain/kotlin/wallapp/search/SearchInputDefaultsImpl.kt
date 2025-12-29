package wallapp.search

import wallapp.content.model.ContentCategory
import wallapp.data.content.ContentCategoryFactory

class SearchInputDefaultsImpl(
    private val contentCategoryFactory: ContentCategoryFactory,
) : SearchInputDefaults {

    override val selectedContentCategoryDefault: List<ContentCategory>
        // Visually, the filters will default to being empty
        get() = emptyList()// contentCategoryFactory.all.map { it.contentCategory }
}