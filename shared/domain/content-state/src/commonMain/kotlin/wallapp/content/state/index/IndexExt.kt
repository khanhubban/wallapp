package wallapp.content.state.index

import wallapp.content.state.explore.ExploreHeaderViewState
import wallapp.content.state.explore.ExploreViewState
import wallapp.content.state.home.HomeHeaderViewState
import wallapp.content.state.home.HomeOnboardingHeaderViewState
import wallapp.content.state.home.HomeOnboardingViewState
import wallapp.content.state.home.HomeTopBarViewState
import wallapp.content.state.home.HomeViewState
import wallapp.content.state.profile.ProfileViewState
import wallapp.content.state.search.SearchResultsViewState
import wallapp.pixel.screen.ScreenViewState
import wallapp.pixel.util.DpOptional
import wallapp.pixel.view.ViewState


val ViewState.indexTopBarHeight: DpOptional?
    get() = when (this) {
        is HomeHeaderViewState -> DpOptional(viewSpec.height)
        is HomeTopBarViewState -> DpOptional(viewSpec.height)
        is HomeOnboardingHeaderViewState -> DpOptional(viewSpec.height)
        is ExploreHeaderViewState -> DpOptional(viewSpec.height)
        else -> null
    }

internal val ScreenViewState.indexScrollableTopBarHeight: DpOptional?
    get() = when (this) {
        is HomeOnboardingViewState.Success -> null // feedViewState.toolbar?.indexTopBarHeight
        is HomeViewState.Data -> DpOptional(topBar.viewSpec.height)
        is ExploreViewState.Success -> feedViewState.toolbar?.indexTopBarHeight
        is SearchResultsViewState.Data -> null
        is ProfileViewState -> null //feedViewState.toolbar?.indexTopBarHeight
        else -> null
    }
