package wallapp.ui.content.downloadstatus

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import wallapp.content.state.downloadstatus.DownloadStatusViewState
import wallapp.pixel.render.Render
import wallapp.pixel.text.Text

@Composable
fun DownloadStatus(
    render: Render,
    viewState: DownloadStatusViewState,
    modifier: Modifier = Modifier,
) {
    val paddingSmall = render.defaultViewSpec.paddingSmall
    val paddingDefault = render.defaultViewSpec.paddingDefault

    val title = viewState.title
    val summary = viewState.summary
    val progress = viewState.progress

    Column(
        modifier = modifier.padding(horizontal = paddingDefault, vertical = paddingSmall),
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(text = title)
            Spacer(modifier = Modifier.weight(1f))
            // Placeholder end
            Text(text = summary)
        }
        Row(modifier = Modifier.fillMaxWidth().padding(top = 5.dp)) {
            LinearProgressIndicator(
                strokeCap = ProgressIndicatorDefaults.CircularDeterminateStrokeCap,
                progress = { progress },
                modifier = Modifier.weight(1f),
                trackColor = Color.Gray,
                color = Color.Red,
            )
        }
    }
}