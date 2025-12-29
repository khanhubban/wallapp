package wallapp.pixel.navigationbar

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import wallapp.image.Image
import wallapp.pixel.shape.ShapeSpec
import wallapp.pixel.text.Text
import kotlin.experimental.ExperimentalObjCName
import kotlin.native.ObjCName

@OptIn(ExperimentalObjCName::class)
@ObjCName("NavBarItemState")
data class NavigationBarItem(
    val isSelected: Boolean,
    val onClick: () -> Unit,
    val text: Text?,
    val selectedImage: Image,
    val unselectedImage: Image = selectedImage,
    val imageSize: Dp = 28.dp,
    val imageShapeSpec: ShapeSpec? = null,
    val alwaysShowLabel: Boolean = true,
)
