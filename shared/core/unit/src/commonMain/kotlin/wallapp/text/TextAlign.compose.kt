package wallapp.text

import androidx.compose.ui.text.style.TextAlign as ComposeTextAlign

val TextAlign.composeTextAlign: ComposeTextAlign
    get() {
        return when (this) {
            is TextAlign.Center -> ComposeTextAlign.Center
            is TextAlign.Start -> ComposeTextAlign.Start
            is TextAlign.End -> ComposeTextAlign.End
            is TextAlign.Justify -> ComposeTextAlign.Justify
        }
    }