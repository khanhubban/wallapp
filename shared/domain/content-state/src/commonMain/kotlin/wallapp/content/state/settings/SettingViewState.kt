package wallapp.content.state.settings

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import co.touchlab.skie.configuration.annotations.SealedInterop
import kotlinx.coroutines.flow.MutableStateFlow
import wallapp.content.state.settings.SettingViewSpec.SettingItemPreviewRowViewSpec
import wallapp.content.state.settings.SettingViewSpec.SettingItemPreviewViewSpec
import wallapp.content.state.widget.EdgeFadeViewState
import wallapp.image.Image
import wallapp.pixel.shape.ShapeSpec
import wallapp.pixel.text.Text
import wallapp.pixel.view.ViewEventHandler
import wallapp.pixel.view.ViewState

@SealedInterop.Enabled
sealed class SettingViewState : ViewState {

    @Immutable
    data class Switch(
        val title: Text,
        val icon: Image?,
        val summary: Text?,
        val checked: Boolean,
        val onCheckedChange: (Boolean) -> Unit,
        val onClicked: () -> Unit,
        val showShimmer: Boolean = false,
        val switchContentDescription: String,
    ) : SettingViewState()


    data class SwitchMutable(
        val title: Text,
        val icon: Image?,
        val summary: Text?,
        val mutableStateFlow: MutableStateFlow<Boolean>,
        /**
         * Called when the value is changed, either by the switch UI being clicked directly or the
         * underlying UI element being clicked to toggle the switch.
         */
        val onChanged: ((Boolean) -> Unit)? = null,
        val switchContentDescription: String,
    ) : SettingViewState() {

        val onCheckedChange: ((Boolean) -> Unit) = {
            mutableStateFlow.value = it
            onChanged?.invoke(it)
        }

        val onClicked: () -> Unit = {
            val value = !checked
            mutableStateFlow.value = value
            onChanged?.invoke(value)
        }

        val checked: Boolean
            get() = mutableStateFlow.value
    }

    @Immutable
    data class Heading(
        val title: Text,
    ): SettingViewState()

    @Immutable
    data class Divider(
        val inset: Dp = 0.dp,
    ): SettingViewState()

    @Immutable
    data class Spacer(
        val height: Dp,
    ): SettingViewState()

    @Immutable
    data class Detail(
        val title: Text,
        val summary: Text? = null,
        val onClick: ViewEventHandler? = null,
    ): SettingViewState()

    @Immutable
    data class Footer(
        val messages: List<Text>,
    ): SettingViewState()

    @Immutable
    data class ViewStateWrapper(
        val viewState: ViewState,
    ): SettingViewState()

    enum class ItemPreviewStyle {
        Default,
        Button,
    }

    @Immutable
    data class ItemPreviewViewState<T>(
        val viewSpec: SettingItemPreviewViewSpec,
        val item: T,
        val style: ItemPreviewStyle = ItemPreviewStyle.Default,
        val label: Text?,
        val shapeSpec: ShapeSpec,
    ) : ViewState

    @Immutable
    data class ItemPreviewRowViewState(
        val viewSpec: SettingItemPreviewRowViewSpec?,
        val title: Text?,
        val itemPreviews: List<ItemPreviewViewState<Any>>,
        val currentIndex: Int,
        val onSelectedChanged: (Int) -> Unit,
        val scrolling: Boolean = false,
        val edgeFade: EdgeFadeViewState? = null,
    ) : SettingViewState()
}

