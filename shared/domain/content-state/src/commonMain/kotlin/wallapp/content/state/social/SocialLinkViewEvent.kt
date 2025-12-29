package wallapp.content.state.social

import androidx.compose.runtime.Immutable
import wallapp.pixel.view.ViewEvent

@Immutable
data class SocialLinkViewEvent(
    val url: String,
) : ViewEvent

