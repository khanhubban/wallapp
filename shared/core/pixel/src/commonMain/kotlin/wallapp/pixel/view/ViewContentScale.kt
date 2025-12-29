package wallapp.pixel.view

import androidx.compose.runtime.Immutable
import co.touchlab.skie.configuration.annotations.SealedInterop

/**
 * See https://developer.android.com/develop/ui/compose/graphics/images/customize#content-scale
 */
@Immutable
@SealedInterop.Enabled
sealed class ViewContentScale {

    @Immutable
    data object Crop : ViewContentScale()

    @Immutable
    data object FillHeight : ViewContentScale()

    @Immutable
    data object FillWidth : ViewContentScale()

    @Immutable
    data object Fit : ViewContentScale()

    @Immutable
    data object FillBounds : ViewContentScale()
}
