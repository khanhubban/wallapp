package wallapp.deeplink

import kotlinx.coroutines.flow.Flow
import wallapp.content.model.Id
import wallapp.screen.ScreenArgument

interface DeepLinkMapper {

    fun map(url: String): Flow<ScreenArgument?>
    fun map(id: Id): ScreenArgument?
}