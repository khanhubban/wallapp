package wallapp.content.state.index

import wallapp.image.Image
import wallapp.pixel.shape.ShapeSpec
import wallapp.pixel.text.Text

data class IndexTabSpec(
    val label: Text,
    val selectedIcon: Image,
    val unselectedIcon: Image = selectedIcon,
    val imageShapeSpec: ShapeSpec? = null,
)
