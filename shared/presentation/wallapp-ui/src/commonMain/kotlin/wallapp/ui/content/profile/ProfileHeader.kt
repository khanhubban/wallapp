package wallapp.ui.content.profile

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import wallapp.content.state.profile.ProfileHeaderViewSpec
import wallapp.content.state.profile.ProfileHeaderViewState
import wallapp.graphics.composeColor
import wallapp.pixel.compose.paddingAx
import wallapp.pixel.menu.MenuItem
import wallapp.pixel.messagebar.MessageBar
import wallapp.pixel.render.Render
import wallapp.pixel.text.Text

@Composable
fun ProfileHeader(
    render: Render,
    viewState: ProfileHeaderViewState,
    modifier: Modifier = Modifier,
) {
    val title = viewState.title
    val summary = viewState.summary
    val upgradeButton = viewState.upgradeButton
    val viewSpec = viewState.viewSpec
    val messageBar = viewState.messageBar
    // note: it's likely `messageBarHeight` will be a dynamic value, that will animate to 0 if `messageBarViewState` becomes `null` in a future task
    val messageBarHeight = messageBar?.viewSpec?.maxHeight ?: 0.dp
    val height = viewSpec.height + messageBarHeight

    val containerColor = viewState.containerColor.composeColor

    val animatedHeight by animateDpAsState(
        targetValue = height,
        animationSpec = tween(durationMillis = 500),
    )
    val animatedMessageBarHeight by animateDpAsState(
        targetValue = messageBarHeight,
        animationSpec = tween(durationMillis = 500),
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(animatedHeight)
            .background(containerColor)
            .padding(top = viewSpec.statusBarHeight),
    ) {
        MessageBar(
            render = render,
            viewState = messageBar,
        )

        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(top = animatedMessageBarHeight),
        ) {
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .paddingAx(viewSpec.padding)
            ) {
                ProfileHeaderColumn(viewSpec, title, summary)

                if (upgradeButton != null) {
                    Spacer(modifier = Modifier.weight(1f))
                    MenuItem(
                        render,
                        upgradeButton,
                        modifier = Modifier
                            .align(Alignment.Bottom),
                    )
                }
            }
        }
    }

}

@Composable
private fun ProfileHeaderColumn(
    viewSpec: ProfileHeaderViewSpec,
    title: Text,
    summary: Text?,
    modifier: Modifier = Modifier,
) {
    val titleHeight = viewSpec.titleHeight
    val subtitleTopPadding = viewSpec.subtitleTopPadding
    val subtitleHeight = viewSpec.subtitleHeight

    Column(
        modifier = modifier,
    ) {
        Text(
            text = title,
            modifier = Modifier
                .height(titleHeight),
        )

        if (summary != null) {
            Spacer(modifier = Modifier.height(subtitleTopPadding))

            Text(
                text = summary,
                colorOverride = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .height(subtitleHeight),
            )
        }
    }
}