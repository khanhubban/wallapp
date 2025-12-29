package wallapp.ui.app

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.mapNotNull
import wallapp.content.state.index.IndexViewModel
import wallapp.content.state.index.IndexViewState
import wallapp.content.state.search.SearchInputViewState
import wallapp.di.resolveDependency
import wallapp.pixel.compose.collectAsState
import wallapp.pixel.render.Render
import wallapp.pixel.view.SingleView
import wallapp.pixel.view.View
import wallapp.resources.AppTypography
import wallapp.resources.Typography3rdParty
import wallapp.screen.ScreenArgument
import wallapp.theme.Theme
import wallapp.theme.ThemeManager
import wallapp.ui.Render
import wallapp.ui.ScreenControllers
import wallapp.ui.WallAppSingleView
import wallapp.viewmodel.ViewModelFactory

@Composable
fun WallAppContentSingleView(
    singleView: SingleView,
    modifier: Modifier = Modifier,
) {
    val render: Render = remember { Render() }
    val typography: Typography = AppTypography()
    val typography3rdParty: Typography = Typography3rdParty()

    val screenControllers = createSingleScreenScreenControllers()
    val theme by resolveDependency<ThemeManager>().theme.collectAsState()
    when (singleView) {
        SingleView.SearchInput -> {
            WallAppContentSearchInput(
                render,
                screenControllers,
                theme,
                typography = typography,
                typography3rdParty = typography3rdParty,
                modifier,
            )
        }
    }
}

@Composable
fun WallAppContentSearchInput(
    render: Render,
    screenControllers: ScreenControllers,
    theme: Theme,
    typography: Typography,
    typography3rdParty: Typography,
    modifier: Modifier = Modifier,
) {
    val viewModelFactory = resolveDependency<ViewModelFactory>()
    val viewModel = viewModelFactory.create(IndexViewModel::class, ScreenArgument.IndexScreenArgument)
    val overlayScreen by viewModel.viewState.filterIsInstance<IndexViewState.Success>()
        .mapNotNull { state ->
            state.overlayScreen as? SearchInputViewState
        }
        .collectAsState(initial = null)
    if (overlayScreen != null) {
        WallAppSingleView(
            render,
            screenControllers,
            View(overlayScreen!!),
            theme,
            typography = typography,
            typography3rdParty = typography3rdParty,
            modifier,
        )
    }
}