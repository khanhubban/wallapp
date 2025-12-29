package wallapp.content.state.home

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import wallapp.data.home.HomeContentType
import wallapp.resources.string.StringRepository
import wallapp.util.combine

class HomeFilterManager(
    private val strings: StringRepository,
) {

    fun setSelected(homeFilter: HomeFilter, selected: Boolean) {
        val flow = getHomeFilterFlow(homeFilter.key)
        flow.value = homeFilter.copy(selected = selected)
    }

    private fun getHomeFilterFlow(key: String): MutableStateFlow<HomeFilter> = when (key) {
        LikedKey -> likedFilter
        PurchasedKey -> purchasedFilter
        else -> throw IllegalArgumentException("Unknown key: $key")
    }

    private val likedFilter: MutableStateFlow<HomeFilter> by lazy {
        MutableStateFlow(
            HomeFilter(
                key = LikedKey,
                label = strings.liked,
                selected = false,
            )
        )
    }

    private val purchasedFilter: MutableStateFlow<HomeFilter> by lazy {
        MutableStateFlow(
            HomeFilter(
                key = PurchasedKey,
                label = strings.library,
                selected = false,
            )
        )
    }

    val allFilters: Flow<List<HomeFilter>> by lazy {
        combine(
            likedFilter,
            purchasedFilter,
        ) { liked, purchased ->
            listOf(
                liked,
                purchased,
            )
        }
    }

    val currentFilterKey: Flow<String?> by lazy {
        allFilters.map { filters ->
            filters.find { it.selected }?.key
        }
    }
    val homeContentType: Flow<HomeContentType> by lazy {
        currentFilterKey.map { key ->
            when (key) {
                LikedKey -> HomeContentType.Liked
                PurchasedKey -> HomeContentType.Purchased
                else -> HomeContentType.Suggested
            }
        }
    }

    companion object {
        val LikedKey = HomeContentType.Liked.key
        val PurchasedKey = HomeContentType.Purchased.key
    }
}