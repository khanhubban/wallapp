package wallapp.ui.content.ad

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import wallapp.content.state.ad.FeedAdViewState
import wallapp.pixel.render.Render


@Composable
actual fun InlineAd(
    render: Render,
    viewState: FeedAdViewState,
    modifier: Modifier,
) {
    val inlineAdItem = viewState.inlineAdItem
    val height = 340.dp

//    AndroidView(
//        modifier = modifier
//            .fillMaxWidth()
//            .height(height),
//        factory = { context ->
//            FrameLayout(context).apply {
//                inlineAdItem.bindAd(this, false)
//            }
//        },
//        update = { },
//    )
}
