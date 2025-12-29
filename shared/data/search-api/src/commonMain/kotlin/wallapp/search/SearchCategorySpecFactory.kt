package wallapp.search

import wallapp.resources.string.Strings
import wallapp.search.model.SearchCategory
import wallapp.search.model.SearchCategorySpec

class SearchCategorySpecFactory(
    private val strings: Strings,
) {
    fun find(searchCategory: SearchCategory): SearchCategorySpec? {
        return all.find { it.searchCategory.key == searchCategory.key }
    }

    val all: List<SearchCategorySpec>
        get() = listOf(
            threeD,
            abstract,
//            amoled,
//            cityscapes,
//            surreal,
            pattern,
//            digital,
//            landscape,
            photography,
            illustration,
//            vector,
        )

    private val threeD: SearchCategorySpec
        get() = SearchCategorySpec(
            searchCategory = SearchCategory.ThreeD,
            label = strings.threeD,
        )

//    private val landscape: SearchCategorySpec
//        get() = SearchCategorySpec(
//            searchCategory = SearchCategory.Landscape,
//            label = strings.landscape,
//        )

    private val abstract: SearchCategorySpec
        get() = SearchCategorySpec(
            searchCategory = SearchCategory.Abstract,
            label = strings.abstract,
        )

    private val photography: SearchCategorySpec
        get() = SearchCategorySpec(
            searchCategory = SearchCategory.Photography,
            label = strings.photography,
        )

    private val illustration: SearchCategorySpec
        get() = SearchCategorySpec(
            searchCategory = SearchCategory.Illustration,
            label = strings.illustration,
        )

//    private val vector: SearchCategorySpec
//        get() = SearchCategorySpec(
//            searchCategory = SearchCategory.Vector,
//            label = strings.vector,
//        )

//    private val amoled: SearchCategorySpec
//        get() = SearchCategorySpec(
//            searchCategory = SearchCategory.Amoled,
//            label = strings.amoled,
//        )

//    private val cityscapes: SearchCategorySpec
//        get() = SearchCategorySpec(
//            searchCategory = SearchCategory.Cityscapes,
//            label = strings.cityscapes,
//        )

//    private val digital: SearchCategorySpec
//        get() = SearchCategorySpec(
//            searchCategory = SearchCategory.Digital,
//            label = strings.digital,
//        )

    private val pattern: SearchCategorySpec
        get() = SearchCategorySpec(
            searchCategory = SearchCategory.Pattern,
            label = strings.pattern,
        )

//    private val surreal: SearchCategorySpec
//        get() = SearchCategorySpec(
//            searchCategory = SearchCategory.Surreal,
//            label = strings.surreal,
//        )
}