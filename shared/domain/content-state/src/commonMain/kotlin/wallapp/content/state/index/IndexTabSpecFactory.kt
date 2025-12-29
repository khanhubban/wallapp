package wallapp.content.state.index

import wallapp.account.AccountManager
import wallapp.pixel.shape.ShapeSize
import wallapp.pixel.shape.ShapeSpec
import wallapp.pixel.shape.ShapeStyle
import wallapp.resources.image.ImageRepository
import wallapp.resources.string.StringRepository
import wallapp.text.TextNavBarTab

class IndexTabSpecFactory(
    private val imageRepository: ImageRepository,
    private val strings: StringRepository,
    private val accountManager: AccountManager,
) {
    
    fun create(tab: IndexTab): IndexTabSpec {
        return when (tab) {
            IndexTab.Account -> createAccount()
            IndexTab.Explore -> createExplore()
            IndexTab.Home -> createHome()
        }
    }

    private val roundedIconShapeSpec: ShapeSpec = ShapeSpec(
        shapeStyle = ShapeStyle.Circle,
        shapeSize = ShapeSize.Large,
    )

    private fun createHome() = IndexTabSpec(
        label = TextNavBarTab(strings.forYou),
        selectedIcon = imageRepository.homeFilled,
        unselectedIcon = imageRepository.homeOutlined,
    )

    private fun createExplore() = IndexTabSpec(
        label = TextNavBarTab(strings.explore),
        selectedIcon = imageRepository.exploreSelected,
        unselectedIcon = imageRepository.exploreUnselected,
    )

    private fun createAccount() = IndexTabSpec(
        label = TextNavBarTab(strings.account),
        selectedIcon = imageRepository.accountSelected,
        unselectedIcon = imageRepository.accountUnselected,
    )
}