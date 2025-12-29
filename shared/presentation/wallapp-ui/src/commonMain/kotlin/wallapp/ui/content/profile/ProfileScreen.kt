package wallapp.ui.content.profile

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import wallapp.content.state.profile.ProfileViewState
import wallapp.pixel.feed.FeedGrid
import wallapp.pixel.render.Render

@Composable
fun ProfileScreen(
    render: Render,
    viewState: ProfileViewState,
    modifier: Modifier = Modifier,
) {
    when (viewState) {
        is ProfileViewState.Loading -> { }
        is ProfileViewState.Data -> {
            ProfileScreen(
                render,
                viewState,
                modifier,
            )
        }
    }
}

@Composable
fun ProfileScreen(
    render: Render,
    viewState: ProfileViewState.Data,
    modifier: Modifier = Modifier,
) {
    FeedGrid(
        render,
        viewState.feedViewState,
        modifier,
    )
}