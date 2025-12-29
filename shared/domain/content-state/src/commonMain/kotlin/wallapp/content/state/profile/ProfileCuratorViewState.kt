package wallapp.content.state.profile

import androidx.compose.runtime.Immutable
import wallapp.image.Image
import wallapp.pixel.text.Text
import wallapp.pixel.view.ViewEventHandler
import wallapp.pixel.view.ViewId
import wallapp.pixel.view.ViewState
import wallapp.string.quote
import wallapp.theme.ColorToken

// "Curator" being a catch-all term for Artists and Folders. This item is typically only displayed
// in the search results.
@Immutable
data class ProfileCuratorViewState(
    val viewSpec: ProfileCuratorViewSpec,
    override val viewId: ViewId,
    val backgroundColor: ColorToken,
    val name: Text,
    val profileImage: ProfileImageViewState,
    val profileShadowImage: Image?,
    val eventHandler: ViewEventHandler,
): ViewState {

    init {
        require(IdSuffix in viewId.id) { "id must contain ${IdSuffix.quote()} - use `createId()`" }
    }

    companion object {
        // Append unique suffix to avoid id collisions with other views
        const val IdSuffix = "-pc"
    }
}
