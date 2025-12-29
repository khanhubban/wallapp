package wallapp.ui.content.account

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import wallapp.content.state.account.AccountOverviewViewState
import wallapp.pixel.clickable.clickable
import wallapp.pixel.compose.paddingAx
import wallapp.pixel.render.Render
import wallapp.pixel.render.shapeMapperComposable
import wallapp.pixel.text.Text
import wallapp.ui.content.profile.ProfileImage

@Composable
fun AccountOverviewSignedIn(
    render: Render,
    viewState: AccountOverviewViewState.SignedIn,
    modifier: Modifier = Modifier,
) {
    val viewSpec = viewState.viewSpec
    val profileImage = viewState.profileImage
    val displayName = viewState.displayName
    val email = viewState.email
    val padding = viewSpec.padding
    val paddingDefault = viewSpec.paddingDefault
    val shape = render.shapeMapperComposable.map(viewSpec.backgroundShapeSpec)!!
    val eventHandler = viewState.onClick

    Row(
        modifier = modifier
            .clip(shape)
            .background(color = MaterialTheme.colorScheme.surface)
            .clickable(render) { eventHandler() }
            .paddingAx(padding),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ProfileImage(
            render,
            profileImage,
            eventHandler = eventHandler,
            modifier = Modifier.semantics {
                contentDescription = viewState.profileImageContentDescription
            }
        )

        Spacer(modifier = Modifier.width(paddingDefault))

        Column(
            modifier = Modifier.weight(1f),
        ) {
            if (displayName != null) {
                Text(text = displayName)
            }
            if (email != null && displayName != null) {
                Spacer(modifier = Modifier.height(8.dp))
            }
            if (email != null) {
                Text(
                    text = email,
                    colorOverride = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}