package wallapp.ui.content.social

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import wallapp.content.state.social.SocialLinksViewState
import wallapp.pixel.menu.MenuItem
import wallapp.pixel.render.Render

@Composable
fun SocialLinks(
    render: Render,
    viewState: SocialLinksViewState,
    modifier: Modifier = Modifier,
) {
    val socialLinks = viewState.socialLinks
    val height = viewState.height
    Row(
        modifier = modifier.height(height),
    ) {
        Spacer(modifier = Modifier.weight(1f))
        socialLinks.forEach {
            MenuItem(
                render = render,
                menuItem = it,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .fillMaxHeight(),
            )
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}