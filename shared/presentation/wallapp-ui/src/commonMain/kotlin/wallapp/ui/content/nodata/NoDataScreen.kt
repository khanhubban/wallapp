package wallapp.ui.content.nodata

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import wallapp.pixel.render.Render

@Composable
fun NoDataScreen(
    render: Render,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.weight(.5f))
        Text("(no data)")
        Spacer(modifier = Modifier.weight(.5f))
    }
}