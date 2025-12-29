package wallapp.content.state.account

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import wallapp.pixel.shape.ShapeSpec
import wallapp.pixel.view.ViewSpec
import wallapp.unit.Padding

@Immutable
data class AccountOverviewViewSpec(
    val padding: Padding,
    val paddingDefault: Dp,
    val loadingSize: Dp,
    val backgroundShapeSpec: ShapeSpec,
): ViewSpec
