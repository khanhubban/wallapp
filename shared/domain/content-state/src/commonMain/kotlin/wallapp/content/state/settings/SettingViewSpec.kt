package wallapp.content.state.settings

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import wallapp.pixel.util.DpOptional
import wallapp.pixel.view.ViewSpec

@Immutable
interface SettingViewSpec : ViewSpec {

    @Immutable
    data class SettingItemPreviewRowViewSpec(
        val itemSpacing: DpOptional? = null,
        val horizontalPadding: Dp,
        val verticalPadding: Dp,
    ): SettingViewSpec

    @Immutable
    data class SettingItemPreviewViewSpec(
        val width: Dp,
        val height: Dp,
    ): SettingViewSpec

}