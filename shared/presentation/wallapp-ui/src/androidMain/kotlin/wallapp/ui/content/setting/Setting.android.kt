package wallapp.ui.content.setting

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import wallapp.pixel.render.Render
import wallapp.pixel.text.Text
import wallapp.ui.content.settings.SettingSwitch
import wallapp.ui.content.settings.SettingViewStateHeadingContent
import wallapp.ui.content.settings.SettingsFooter


@Preview(showBackground = true)
@Composable
fun SettingSwitchPreview() {
    SettingSwitch(Render.Preset)
}

@Preview(showBackground = true)
@Composable
fun SettingsFooterPreview(
    modifier: Modifier = Modifier,
    text: String = "Demo label goes here",
    summary: String? = null,
) {
    SettingsFooter()
}

@Preview(showBackground = true)
@Composable
fun SettingViewStateHeadingContentPreview(
) {
    SettingViewStateHeadingContent(title = Text.createPreset("Demo title"))
}