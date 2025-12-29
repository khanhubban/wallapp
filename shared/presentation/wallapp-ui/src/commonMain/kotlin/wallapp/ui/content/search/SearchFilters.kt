package wallapp.ui.content.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import wallapp.content.state.search.SearchInputViewState
import wallapp.pixel.render.Render
import wallapp.pixel.selection.SelectionGroup

@Composable
fun SearchFilters(
    render: Render,
    viewState: SearchInputViewState,
    footerContainerColor: Color,
    modifier: Modifier = Modifier,
) {
    when (viewState) {
        is SearchInputViewState.Loading -> { }

        is SearchInputViewState.Data -> {
            SearchFilters(
                render = render,
                viewState = viewState,
                footerContainerColor = footerContainerColor,
                modifier = modifier,
            )
        }
    }
}

@Composable
private fun SearchFilters(
    render: Render,
    viewState: SearchInputViewState.Data,
    footerContainerColor: Color,
    modifier: Modifier = Modifier,
) {
    val paddingDefault = render.defaultViewSpec.paddingDefault
    val selectionPadding = paddingDefault + (paddingDefault * 1.5f)

    // Note: matches `paddingDefault` defined in [SelectionRow]
    val filterItemPaddingDefault = render.defaultViewSpec.paddingDefault / 2
    val searchColorHorizontalPadding = selectionPadding + filterItemPaddingDefault

    val colorsViewState = viewState.searchColors

    val searchFilterGroup = viewState.searchFilterGroup

    val contentCategoryGroup = viewState.contentCategoryGroup

    Column(
        modifier = modifier,
    ) {
        SearchColors(
            render = render,
            viewState = colorsViewState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = paddingDefault,
                    start = searchColorHorizontalPadding,
                    end = searchColorHorizontalPadding,
                ),
        )

        Spacer(modifier = Modifier.height(paddingDefault))

        SelectionGroup(
            render = render,
            viewState = searchFilterGroup,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = selectionPadding),
        )

        Spacer(modifier = Modifier.fillMaxWidth().height(paddingDefault))

        Column(
            modifier = Modifier.background(footerContainerColor)
        ) {
            Spacer(modifier = Modifier.fillMaxWidth().height(paddingDefault))

            SelectionGroup(
                render = render,
                viewState = contentCategoryGroup,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = selectionPadding),
            )

//            Spacer(modifier = Modifier.height(filterToggleHeight))
        }
    }
}