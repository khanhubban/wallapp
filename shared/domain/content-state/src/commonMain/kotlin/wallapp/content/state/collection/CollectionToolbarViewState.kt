package wallapp.content.state.collection

import androidx.compose.runtime.Immutable
import co.touchlab.skie.configuration.annotations.SealedInterop
import wallapp.content.state.profile.ProfileImageViewState
import wallapp.content.state.toolbar.CollapsingToolbarStateWrapper
import wallapp.pixel.menu.MenuItem
import wallapp.pixel.message.MessageBarViewState
import wallapp.pixel.text.Text
import wallapp.pixel.toolbar.ToolbarViewState
import wallapp.pixel.view.ViewState
import wallapp.theme.ColorToken

@Immutable
@SealedInterop.Enabled
sealed class CollectionToolbarViewState : ViewState {

    abstract val collapsingToolbarStateWrapper: CollapsingToolbarStateWrapper
    abstract val isExpanded: Boolean

    @Immutable
    data class Unlocked(
        val viewSpec: CollectionToolbarViewSpec.Unlocked,
        val toolbar: ToolbarViewState,
        val containerColorOverride: ColorToken,
        val title: Text,
        val artistProfileImage: ProfileImageViewState,
        val artistName: Text?,
        val collectionActionButtonViewState: CollectionActionButtonViewState,
        override val collapsingToolbarStateWrapper: CollapsingToolbarStateWrapper,
        override val isExpanded: Boolean,
    ) : CollectionToolbarViewState()

    @Immutable
    data class Locked(
        val viewSpec: CollectionToolbarViewSpec.Locked,
        val messageBarViewState: MessageBarViewState? = null,
        val toolbar: ToolbarViewState,
        val containerColorOverride: ColorToken,
        val title: Text,
        val artistProfileImage: ProfileImageViewState,
        val artistName: Text?,
        val collectionActionButtonViewState: CollectionActionButtonViewState,
        val adFreeCollectionLockedInfo: MenuItem?,
        override val collapsingToolbarStateWrapper: CollapsingToolbarStateWrapper,
        override val isExpanded: Boolean,
    ) : CollectionToolbarViewState()
}
