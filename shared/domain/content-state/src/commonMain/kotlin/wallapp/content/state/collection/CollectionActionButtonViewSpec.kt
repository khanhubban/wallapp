package wallapp.content.state.collection

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import co.touchlab.skie.configuration.annotations.SealedInterop
import wallapp.pixel.animation.AnimatedViewSpec
import wallapp.pixel.view.ViewSpec

@SealedInterop.Enabled
sealed class CollectionActionButtonViewSpec : ViewSpec {

    abstract val animatedViewSpec: AnimatedViewSpec

    @Immutable
    data class GetCollectionActionButtonViewSpec(
        override val animatedViewSpec: AnimatedViewSpec,
        val iconPaddingStartMin: Dp,
        val iconPaddingStartMax: Dp,
        val labelPaddingStartMin: Dp,
        val labelPaddingStartMax: Dp,
    ) : CollectionActionButtonViewSpec()

    @Immutable
    data class BuyCollectionActionButtonViewSpec(
        override val animatedViewSpec: AnimatedViewSpec,
        val item1PaddingStartMin: Dp,
        val item1PaddingStartMax: Dp,
        val item3PaddingEnd: Dp,
    ) : CollectionActionButtonViewSpec()

    @Immutable
    data class DownloadProgressButtonViewSpec(
        override val animatedViewSpec: AnimatedViewSpec,
        val minIconPaddingStart: Dp,
        val maxLabelPaddingStart: Dp,
        val countLabelPaddingEndMin: Dp,
        val countLabelPaddingEndMax: Dp,
    ) : CollectionActionButtonViewSpec()

}