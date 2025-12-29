package wallapp.ui.content.account

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import wallapp.content.state.account.AccountOverviewViewState
import wallapp.pixel.compose.paddingAx
import wallapp.pixel.render.Render
import wallapp.pixel.render.shapeMapperComposable
import wallapp.ui.content.loading.Loading

@Composable
fun AccountOverviewLoading(
    render: Render,
    viewState: AccountOverviewViewState.Loading,
    modifier: Modifier = Modifier,
) {
    val viewSpec = viewState.viewSpec
    val padding = viewSpec.padding
    val loadingSize = viewSpec.loadingSize
    val shape = render.shapeMapperComposable.map(viewSpec.backgroundShapeSpec)!!

    Column(
        modifier = modifier
            .clip(shape)
            .background(color = MaterialTheme.colorScheme.surface)
            .paddingAx(padding),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Loading(render, modifier = Modifier.size(loadingSize))
    }
}