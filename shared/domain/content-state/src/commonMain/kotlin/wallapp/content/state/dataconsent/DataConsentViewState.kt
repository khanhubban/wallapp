package wallapp.content.state.dataconsent

import androidx.compose.runtime.Immutable
import wallapp.content.state.settings.SettingViewState
import wallapp.pixel.menu.MenuItem
import wallapp.pixel.screen.ScreenViewState
import wallapp.pixel.text.Text

@Immutable
data class DataConsentViewState(
    val title: Text,
    val settings: List<SettingViewState>,
    val continueButton: MenuItem,
    val footer: MenuItem,
) : ScreenViewState
