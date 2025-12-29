package wallapp.ui.content.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import wallapp.content.state.profile.ProfileConnectionViewState
import wallapp.content.state.profile.ProfileConnectionsViewState
import wallapp.pixel.clickable.clickable
import wallapp.pixel.render.Render

@Composable
fun ProfileConnection(
    render: Render,
    viewState: ProfileConnectionViewState,
    modifier: Modifier = Modifier,
) {
    val count = viewState.count
    val label = viewState.label

    Column(
        modifier = modifier
            .padding(vertical = 8.dp)
            .clickable(render) { viewState.onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = count,
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
        )

        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
fun ProfileConnections(
    render: Render,
    viewState: ProfileConnectionsViewState,
    modifier: Modifier = Modifier,
) {
    val connections = viewState.connections
    Row(
        modifier = modifier,
    ) {
        connections.forEach {
            ProfileConnection(
                render = render,
                viewState = it,
                modifier = Modifier.weight(1f),
            )
        }
    }
}