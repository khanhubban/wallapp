package wallapp.content.state.settings

import wallapp.image.Image
import wallapp.pixel.view.ViewState

data class SettingAppIconViewState(
    val appIconPreviewSelected: Image,
    val appIconPreviewUnselected: Image?,
    val wallAppIcon: Image,
    val lockedImage: Image?,
) : ViewState