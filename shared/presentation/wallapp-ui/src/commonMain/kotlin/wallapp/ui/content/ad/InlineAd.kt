package wallapp.ui.content.ad

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import wallapp.ads.inline.InlineAdViewState
import wallapp.content.state.ad.FeedAdViewState
import wallapp.image.Image
import wallapp.pixel.button.Button
import wallapp.pixel.button.ButtonAppearance
import wallapp.pixel.render.Render
import wallapp.pixel.text.Text
import wallapp.pixel.text.TextStyleBody
import wallapp.pixel.text.TextStyleCallToAction
import wallapp.pixel.text.TextStyleHeadline
import wallapp.pixel.text.TextStyleSubheading

@Composable
fun InlineAd(
    render: Render,
    viewState: FeedAdViewState,
) {
    InlineAd(
        render = render,
        viewState = viewState,
        modifier = Modifier,
    )
}

@Composable
fun InlineAd(
    render: Render,
    viewState: InlineAdViewState,
    modifier: Modifier = Modifier,
) {
    when (viewState) {
        is InlineAdViewState.Data -> { InlineAd(render, viewState, modifier) }
        is InlineAdViewState.Loading -> { InlineAd(render, viewState, modifier) }
        is InlineAdViewState.NoOp -> { InlineAd(render, viewState, modifier) }
    }
}

@Composable
fun InlineAdPlaceholder(
    render: Render,
    viewState: InlineAdViewState.Data,
    modifier: Modifier = Modifier,
) {
    val viewStateString = viewState.headline ?: viewState.callToAction ?: viewState.body ?: viewState.toString()
    val text = TextStyleBody("Data - $viewStateString")
    Text(
        text = text,
        modifier = modifier,
    )
}

@Composable
fun InlineAd(
    render: Render,
    viewState: InlineAdViewState.Data,
    modifier: Modifier = Modifier,
) {
    val headline = viewState.headline?.let {
        TextStyleHeadline(it)
    }
    val callToAction = viewState.callToAction?.let {
        TextStyleCallToAction(it)
    }
    val body = viewState.body?.let {
        TextStyleSubheading(it)
    }
    val icon = viewState.icon?.image
    val heroImage = viewState.images?.firstOrNull()?.image

    val shape = CutCornerShape(topStart = 16.dp, bottomEnd = 16.dp)

    val backgroundColor = MaterialTheme.colorScheme.surface

    val height = 360.dp
    val iconSize = 64.dp
    val heroImageHeight = 140.dp
    val buttonHeight = 56.dp
    val paddingDefault = render.defaultViewSpec.paddingDefault
    val paddingSmall = render.defaultViewSpec.paddingSmall

    Column(
        modifier = modifier
            .height(height)
            .background(color = backgroundColor)
            .padding(paddingDefault),
    ) {
        Row(
            modifier = Modifier.height(iconSize),
        ) {
            if (icon != null) {
                Image(
                    render = render,
                    image = icon,
                    modifier = Modifier.size(iconSize),
                )
            }

            Spacer(modifier = Modifier.width(paddingSmall))

            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
            ) {
                Column(
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    if (headline != null) {
                        Text(
                            text = headline,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }

                    // TODO: Add rating
                }
            }

        }

        Spacer(modifier = Modifier.height(paddingSmall))

        Row {
            if (body != null) {
                Text(text = body)
            }
        }

        Spacer(modifier = Modifier.height(paddingSmall))

        Row(
            modifier = Modifier
                .height(height = heroImageHeight)
                .fillMaxWidth(),
        ) {
            if (heroImage != null) {
                Image(
                    render = render,
                    image = heroImage,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }

        Spacer(modifier = Modifier.height(paddingSmall))

        if (callToAction != null) {
            Button(
                render = render,
                onClick = { },
                buttonAppearance = ButtonAppearance.Default,
                shape = shape,
                containerColor = null,
                modifier = Modifier.fillMaxWidth()
                    .height(buttonHeight),
                contentPadding = PaddingValues(
                    horizontal = paddingDefault,
                    vertical = 0.dp,
                ),
            ) {
                Text(text = callToAction)
            }
        }
    }
}

@Composable
fun InlineAd(
    render: Render,
    viewState: InlineAdViewState.Loading,
    modifier: Modifier = Modifier,
) {
    Text(
        text = viewState.toString().let { TextStyleBody(it) },
        modifier = modifier,
    )
}

@Composable
fun InlineAd(
    render: Render,
    viewState: InlineAdViewState.NoOp,
    modifier: Modifier = Modifier,
) {
    Text(
        text = viewState.toString().let { TextStyleBody(it) },
        modifier = modifier,
    )
}


@Composable
expect fun InlineAd(
    render: Render,
    viewState: FeedAdViewState,
    modifier: Modifier,
)

