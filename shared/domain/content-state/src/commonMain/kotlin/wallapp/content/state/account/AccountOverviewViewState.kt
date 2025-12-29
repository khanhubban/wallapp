package wallapp.content.state.account

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import co.touchlab.skie.configuration.annotations.SealedInterop
import wallapp.content.state.profile.ProfileImageViewState
import wallapp.content.state.signin.SignInButtonViewState
import wallapp.pixel.text.Text
import wallapp.pixel.view.ViewEventHandler
import wallapp.pixel.view.ViewState

@Immutable
@SealedInterop.Enabled
sealed class AccountOverviewViewState : ViewState {

    @Immutable
    data class Loading(
        val viewSpec: AccountOverviewViewSpec,
    ) : AccountOverviewViewState()

    @Immutable
    data class SignedIn(
        val viewSpec: AccountOverviewViewSpec,
        val profileImage: ProfileImageViewState,
        val displayName: Text?,
        val email: Text?,
        val onClick: ViewEventHandler,
        val messageBarHeight: Dp,
        val profileImageContentDescription: String,
    ): AccountOverviewViewState()

    @Immutable
    data class SignedOut(
        val viewSpec: AccountOverviewViewSpec,
        val profileImage: ProfileImageViewState?,
        val profileEventHandler: ViewEventHandler?,
        val signInMessage: Text?,
        val signInButtons: List<SignInButtonViewState>,
        val signInButtonsUseLightTheme: Boolean,
        val messageBarHeight: Dp,
    ): AccountOverviewViewState()
}