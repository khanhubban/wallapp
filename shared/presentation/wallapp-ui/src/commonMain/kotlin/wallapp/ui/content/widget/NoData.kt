package wallapp.ui.content.widget

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import wallapp.content.state.widget.NoDataViewState
import wallapp.image.Image
import wallapp.pixel.render.Render
import wallapp.pixel.text.Text

@Composable
fun NoDataScreen(
    render: Render,
    viewState: NoDataViewState,
    modifier: Modifier = Modifier,
) {
    NoData(
        render,
        viewState,
        modifier = modifier.fillMaxWidth(),
    )
}

@Composable
fun NoData(
    render: Render,
    viewState: NoDataViewState,
    modifier: Modifier = Modifier,
) {
    val title = viewState.title
    val summary = viewState.summary
    val image = viewState.image

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.weight(.5f))
        Text(title)
        if (image != null) {
            Image(
                render,
                image = image,
                modifier = Modifier.size(150.dp),
            )
        }
        if (summary != null) {
            Text(summary)
        }
        Spacer(modifier = Modifier.weight(.5f))
    }
}