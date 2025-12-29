package wallapp.deeplink

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import wallapp.pixel.navigation.NavigationArgument

object DeepLinkManagerNoOp : DeepLinkManager {

    override val navigationArgument: Flow<NavigationArgument?> = flowOf(null)

    override fun handleDeepLink(url: String) { }

    override fun handleAppShortcut(shortcutId: String): Boolean = false
}