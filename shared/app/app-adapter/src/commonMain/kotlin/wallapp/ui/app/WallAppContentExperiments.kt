package wallapp.ui.app

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import wallapp.content.model.Id.RemixId
import wallapp.content.state.error.network.ErrorNetworkViewModel
import wallapp.content.state.wallpaper.WallpaperShowcaseViewModel
import wallapp.di.resolveDependency
import wallapp.pixel.navigation.NavigationEvent
import wallapp.pixel.screen.ScreenViewState
import wallapp.screen.ScreenArgument.WallpaperShowcaseScreenArgument
import wallapp.viewmodel.ViewModelFactory

@Composable
fun WallAppContentSingleScreenWallpaper(
    modifier: Modifier = Modifier,
    screenArgument: WallpaperShowcaseScreenArgument? = null,
) {
    val remixId = RemixId("a~artistname~dune_dune~9_color_0")
    val viewModelFactory = resolveDependency<ViewModelFactory>()
    val viewModel = viewModelFactory.create(
        WallpaperShowcaseViewModel::class,
        screenArgument ?: WallpaperShowcaseScreenArgument(remixId),
    )
    val screenViewState: StateFlow<ScreenViewState> = viewModel.viewState
//    val navigationEvent = viewModel.navigationEvent
    val navigationEvent: MutableStateFlow<NavigationEvent> = MutableStateFlow(NavigationEvent.NoOpEvent)
    WallAppContentSingleScreen(screenViewState, navigationEvent, modifier)
}

@Composable
fun WallAppContentSingleScreenError(
    modifier: Modifier = Modifier,
) {
    val viewModelFactory = resolveDependency<ViewModelFactory>()
    val viewModel = viewModelFactory.create(ErrorNetworkViewModel::class)
    val screenViewState: StateFlow<ScreenViewState> = viewModel.viewState
//    val navigationEvent = viewModel.navigationEvent
    val navigationEvent: MutableStateFlow<NavigationEvent> = MutableStateFlow(NavigationEvent.NoOpEvent)
    WallAppContentSingleScreen(screenViewState, navigationEvent, modifier)
}
