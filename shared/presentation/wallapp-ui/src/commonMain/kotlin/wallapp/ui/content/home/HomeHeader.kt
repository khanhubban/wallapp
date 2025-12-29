package wallapp.ui.content.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import wallapp.content.state.home.HomeHeaderViewState
import wallapp.pixel.compose.paddingAx
import wallapp.pixel.render.Render
import wallapp.pixel.selection.SelectionGroup
import wallapp.pixel.selection.SelectionGroupViewState
import wallapp.pixel.separator.Separator
import wallapp.ui.content.profile.ProfileImage

@Composable
fun HomeHeader(
    render: Render,
    viewState: HomeHeaderViewState,
    modifier: Modifier = Modifier,
) {
    val viewSpec = viewState.viewSpec
    val height = viewSpec.height
    val profileVerticalPadding = viewSpec.profileVerticalPadding
    val separatorHeight = viewSpec.separatorHeight
    val separatorPadding = viewSpec.separatorPadding

    val profileImage = viewState.profileImage
    val filterGroup = viewState.filterGroup

    Column(
        modifier = modifier.fillMaxWidth()
            .height(height)
            .background(color = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Spacer(modifier = Modifier.height(profileVerticalPadding))
        ProfileImage(
            render = render,
            profileImage = profileImage,
            eventHandler = null,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
        Spacer(modifier = Modifier.height(profileVerticalPadding))
        Separator(
            height = separatorHeight,
            modifier = Modifier.fillMaxWidth()
                .paddingAx(separatorPadding),
        )
        FilterGroup(render, filterGroup, Modifier.fillMaxWidth())
    }
}

@Composable
private fun FilterGroup(
    render: Render,
    viewState: SelectionGroupViewState,
    modifier: Modifier = Modifier,
) {
    val groupWidth = (viewState.viewSpec.itemWidth * viewState.selections.size) +
            render.defaultViewSpec.paddingDefault

    Row(
        modifier = modifier,
    ) {
        Spacer(modifier = Modifier.weight(1f))
        SelectionGroup(
            render = render,
            viewState = viewState,
            modifier = Modifier.width(groupWidth),
        )
        Spacer(modifier = Modifier.weight(1f))
    }
}