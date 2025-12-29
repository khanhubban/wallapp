package wallapp.content.state.signup

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import wallapp.account.state.signin.SignInViewModel
import wallapp.content.model.WallpaperItem
import wallapp.data.content.ContentRepository
import wallapp.pixel.screen.ScreenViewStateProvider
import wallapp.screen.ScreenSystemBarController
import wallapp.screen.ScreenSystemBarControllerHolder
import wallapp.view.ViewStateFactory
import wallapp.viewmodel.ViewModel

class SignUpViewModel(
    contentRepository: ContentRepository,
    private val viewStateFactory: ViewStateFactory,
    private val signInViewModel: SignInViewModel,
) : ViewModel(), ScreenViewStateProvider, ScreenSystemBarControllerHolder {

    override val screenSystemBarController: StateFlow<ScreenSystemBarController> by lazy {
        MutableStateFlow(ScreenSystemBarController.TranslucentStatusBar())
    }

    private val currentBackgroundIndex = MutableStateFlow(0)

    private fun createViewState(
        showcaseItems: List<WallpaperItem>? = allShowcaseItems.value,
    ): SignUpViewState {
        return viewStateFactory.createSignUpViewState(
            googleSignInButtonViewState = signInViewModel.googleSignInButtonViewState,
            appleSignInButtonViewState = signInViewModel.appleSignInButtonViewState,
            showUpgradeButton = true,
        ) {
            
        }
    }

    private val allShowcaseItems: StateFlow<List<WallpaperItem>?> =
        contentRepository.signUpContentResult.map {
            it.showcaseWallpapers
        }.stateIn(null)

    override val viewState: StateFlow<SignUpViewState> =
        allShowcaseItems.map {
            createViewState(it)
        }.stateIn(createViewState())
}
