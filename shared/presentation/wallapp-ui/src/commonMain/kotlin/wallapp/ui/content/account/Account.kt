package wallapp.ui.content.account

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import wallapp.content.state.account.AccountViewState
import wallapp.content.state.profile.ProfileImageViewState
import wallapp.pixel.compose.navigationBarsPadding
import wallapp.pixel.compose.statusBarsPadding
import wallapp.pixel.render.Render
import wallapp.pixel.text.Text
import wallapp.pixel.toolbar.Toolbar
import wallapp.ui.content.profile.ProfileImage
import wallapp.ui.content.settings.SettingsFeed

@Composable
fun Account(
    render: Render,
    viewState: AccountViewState,
    modifier: Modifier = Modifier,
) {
    val toolbarViewState = viewState.toolbarViewState
    val email = viewState.email
    val displayName = viewState.displayName
    val profileImage = viewState.profileImage
    val settingViewStates = viewState.settings

    Surface {
        Box(modifier = modifier
            .background(color = MaterialTheme.colorScheme.background)
            .statusBarsPadding(render.windowFrame)
            .navigationBarsPadding(render.windowFrame),
        ) {
            Toolbar(render, toolbarViewState)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = toolbarViewState.height),
            ) {
                if (profileImage != null) {
                    AccountHeader(render, profileImage, displayName, email)
                }

                SettingsFeed(
                    render = render,
                    viewStates = settingViewStates,
//                    modifier = Modifier.padding(top = toolbarHeight),
                )
            }

        }
    }


}

@Composable
fun AccountHeader(
    render: Render,
    profileImage: ProfileImageViewState,
    displayName: Text?,
    email: Text?,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
    ) {
        ProfileImage(
            render, profileImage,
            eventHandler = profileImage.eventHandler,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )

        if (email != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(email, modifier = Modifier.align(Alignment.CenterHorizontally))
        }
    }
}