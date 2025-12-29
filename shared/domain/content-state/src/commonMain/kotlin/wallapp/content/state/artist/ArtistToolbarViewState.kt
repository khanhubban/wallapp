package wallapp.content.state.artist

import androidx.compose.runtime.Immutable
import wallapp.content.state.profile.ProfileImageViewState
import wallapp.content.state.social.SocialLinksViewState
import wallapp.content.state.toolbar.CollapsingToolbarStateWrapper
import wallapp.pixel.message.MessageBarViewState
import wallapp.pixel.text.Text
import wallapp.pixel.toolbar.ToolbarViewState
import wallapp.pixel.view.ViewState
import wallapp.theme.ColorToken

@Immutable
data class ArtistToolbarViewState(
    val messageBar: MessageBarViewState?,
    val toolbarViewState: ToolbarViewState,
    val containerColorOverride: ColorToken,
    val name: Text,
    val profileImage: ProfileImageViewState,
    val socialLinks: SocialLinksViewState?,
    val collapsingToolbarStateWrapper: CollapsingToolbarStateWrapper,
) : ViewState
