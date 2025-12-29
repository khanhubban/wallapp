package wallapp.content.state.home

import androidx.compose.runtime.Immutable
import wallapp.pixel.text.Text
import wallapp.pixel.view.ViewState
import wallapp.theme.Theme

@Immutable
data class HomeOnboardingHeaderViewState(
    val viewSpec: HomeOnboardingHeaderViewSpec,
    val theme: Theme,
    val title: Text,
    val summary: Text,
) : ViewState
