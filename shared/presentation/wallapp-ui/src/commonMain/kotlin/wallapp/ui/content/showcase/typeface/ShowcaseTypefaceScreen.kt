package wallapp.ui.content.showcase.typeface

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import wallapp.content.state.showcase.typeface.ShowcaseTypefaceViewState
import wallapp.pixel.compose.statusBarsPadding
import wallapp.pixel.render.Render
import wallapp.pixel.text.Text
import wallapp.pixel.text.TextStyleBody
import wallapp.pixel.text.TextStyleSubheading
import wallapp.ui.content.toolbar.ToolbarOffset
import androidx.compose.material3.Text as TextMaterial3

private val Message = "A quick brown Fox"

@Composable
private fun TextHeader(
    label: String,
    modifier: Modifier = Modifier,
    topPadding: Dp = 32.dp,
) {
    val text = TextStyleBody(string = label, textAlign = null)
    Text(text, modifier = modifier.padding(top = topPadding))
}

@Composable
private fun Text(
    message: String = Message,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontWeight: FontWeight? = null,
) {
    var header = ""
    if (fontSize != TextUnit.Unspecified) {
        header = "[$fontSize]"
    }
    if (fontWeight != null) {
        val prefix = when (fontWeight) {
            FontWeight.Bold -> { "Bold" }
            FontWeight.Normal -> { "Normal" }
            else -> { fontWeight }
        }
        header = "[$prefix] $header"
    }

    Column {
        TextHeader(header, topPadding = 16.dp)
        // Explicitly render using Material3 rather than [Text].
        TextMaterial3(
            text = message,
            modifier = modifier,
            fontSize = fontSize,
            fontWeight = fontWeight,
        )
    }
}

@Composable
fun ShowcaseTypefaceScreen(
    render: Render,
    viewState: ShowcaseTypefaceViewState,
    modifier: Modifier = Modifier,
) {
    val toolbar = viewState.toolbar
    Column(
        modifier = modifier
            .statusBarsPadding(render.windowFrame),
    ) {
        ToolbarOffset(render, toolbar, offsetForStatusBar = false)

        TypographyShowcase(render)
    }
}

@Composable
private fun SectionHeader(
    label: String,
    modifier: Modifier = Modifier,
    topPadding: Dp = 32.dp,
    bottomPadding: Dp = 8.dp,
) {
    val text = TextStyleSubheading(string = label, textAlign = null, colorToken = null, maxLines = 1, useMarquee = false)
    Text(
        text = text,
        modifier = modifier.padding(top = topPadding, bottom = bottomPadding),
    )
}

@Composable
fun TypographyShowcase(
    render: Render,
    modifier: Modifier = Modifier,
) {
    val paddingDefault = render.defaultViewSpec.paddingDefault
    LazyColumn(
        modifier = modifier
            .padding(horizontal = paddingDefault),
    ) {
//        item { SectionHeader("App typeface:") }
        item { TextStylesPreview(message = Message) }

        item { Divider() }

//        item { SectionHeader("Font weights:") }
//        item { Text(fontWeight = FontWeight.Normal, fontSize = 16.sp) }
//        item { Text(fontWeight = FontWeight.Normal, fontSize = 20.sp) }
//        item { Text(fontWeight = FontWeight.Normal, fontSize = 24.sp) }
//        item { Text(fontWeight = FontWeight.Normal, fontSize = 30.sp) }
//        item { Text(fontWeight = FontWeight.Bold, fontSize = 16.sp) }
//        item { Text(fontWeight = FontWeight.Bold, fontSize = 20.sp) }
//        item { Text(fontWeight = FontWeight.Bold, fontSize = 24.sp) }
//        item { Text(fontWeight = FontWeight.Bold, fontSize = 30.sp) }

        item { Spacer(modifier = Modifier.height(paddingDefault))}
    }

}