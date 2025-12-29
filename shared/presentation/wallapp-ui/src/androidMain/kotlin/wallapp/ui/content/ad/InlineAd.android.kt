package wallapp.ui.content.ad

import android.app.Activity
import android.widget.FrameLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import wallapp.ads.inline.support.InlineAdItem
import wallapp.content.state.ad.FeedAdViewState
import wallapp.pixel.compose.conditional
import wallapp.pixel.render.Render
import wallapp.system.ui.controller.UiControllerAndroid

var ShowInlineAdComposeDebug: Boolean = false

@Composable
actual fun InlineAd(
    render: Render,
    viewState: FeedAdViewState,
    modifier: Modifier,
) {
    val inlineAdItem = viewState.inlineAdItem

    if (ShowInlineAdComposeDebug) {
        InlineAdCompose(
            render,
            inlineAdItem = inlineAdItem,
            modifier = Modifier,
        )
    } else {
        InlineAdNative(
            render,
            inlineAdItem = inlineAdItem,
            modifier = Modifier,
        )
    }
}

@Composable
fun InlineAdNative(
    render: Render,
    inlineAdItem: InlineAdItem,
    modifier: Modifier = Modifier,
) {
    val height = 344.dp
    val paddingDefault = render.defaultViewSpec.paddingDefault

    AndroidView(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .padding(horizontal = paddingDefault)
            .background(MaterialTheme.colorScheme.surface)
            .conditional(ShowInlineAdComposeDebug) { this.alpha(0f) },
        factory = { context -> //
            FrameLayout(context).apply {
                inlineAdItem.bindAd(this, false)
            }
        },
        update = { },
    )
}

@Composable
fun InlineAdCompose(
    render: Render,
    inlineAdItem: InlineAdItem,
    modifier: Modifier = Modifier,
) {
    val adViewState = inlineAdItem.viewStateFlow.collectAsState().value
    val height = 360.dp

    val activity = (LocalContext.current) as? Activity
    LaunchedEffect(inlineAdItem) {
        inlineAdItem.bind(UiControllerAndroid(activity!!), delayInit = true)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height),
    ) {
        if (ShowInlineAdComposeDebug) {
            InlineAd(
                render,
                viewState = adViewState,
                modifier = Modifier
                    .align(Alignment.TopCenter),
            )
        }
    }
}

