package wallapp.deeplink

import kotlinx.coroutines.flow.Flow
import wallapp.pixel.navigation.NavigationArgument

interface DeepLinkManager {

    val navigationArgument: Flow<NavigationArgument?>

    fun handleDeepLink(url: String)

    fun handleAppShortcut(shortcutId: String): Boolean
}