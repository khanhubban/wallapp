package wallapp.content.state.collection

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import co.touchlab.skie.configuration.annotations.SealedInterop
import wallapp.pixel.animation.AnimatedViewSpec
import wallapp.pixel.view.ViewSpec

@Immutable
@SealedInterop.Enabled
sealed class CollectionToolbarViewSpec : ViewSpec {

    @Immutable
    data class Unlocked(
        val minToolbarHeight: Dp,
        val maxToolbarHeight: Dp,
        val paddingDefault: Dp,
        val profileAnimatedViewSpec: AnimatedViewSpec,
        val artistNamePaddingTop: Dp?,
        val artistNameHeight: Dp?,
        val collectionNamePaddingTop: Dp,
        val collectionNameHeight: Dp,
    ) : CollectionToolbarViewSpec()

    @Immutable
    data class Locked(
        val minToolbarHeight: Dp,
        val maxToolbarHeight: Dp,
        val paddingDefault: Dp,
        val profileAnimatedViewSpec: AnimatedViewSpec,
        val artistNamePaddingTop: Dp?,
        val artistNameHeight: Dp?,
        val collectionNamePaddingTop: Dp,
        val collectionNameHeight: Dp,
        val adFreeCollectionLockedInfoTop: Dp,
        val adFreeCollectionLockedInfoHeight: Dp,
    ) : CollectionToolbarViewSpec()
}
