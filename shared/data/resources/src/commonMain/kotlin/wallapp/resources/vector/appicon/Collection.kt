import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import wallapp.resources.vector.AppIcon


private var _Collection: ImageVector? = null

val AppIcon.Collection: ImageVector
	get() {
		if (_Collection != null) {
			return _Collection!!
		}
		_Collection = ImageVector.Builder(
			name = "Collection",
			defaultWidth = 24.dp,
			defaultHeight = 24.dp,
			viewportWidth = 24f,
			viewportHeight = 24f
		).apply {
			path(
				fill = SolidColor(Color.Black),
				fillAlpha = 1.0f,
				stroke = null,
				strokeAlpha = 1.0f,
				strokeLineWidth = 0f,
				strokeLineCap = StrokeCap.Butt,
				strokeLineJoin = StrokeJoin.Miter,
				strokeLineMiter = 1.0f,
				pathFillType = PathFillType.NonZero
			) {
				moveTo(22f, 2f)
				verticalLineToRelative(11.7f)
				horizontalLineTo(2f)
				verticalLineTo(2f)
				horizontalLineToRelative(20f)
				moveTo(23f, 0f)
				horizontalLineTo(1f)
				curveTo(0.45f, 0f, 0f, 0.45f, 0f, 1f)
				verticalLineToRelative(13.69f)
				curveTo(0f, 15.25f, 0.45f, 15.7f, 1f, 15.7f)
				horizontalLineToRelative(21.99f)
				curveToRelative(0.55f, 0f, 1f, -0.45f, 1f, -1f)
				verticalLineTo(1f)
				curveTo(24f, 0.45f, 23.55f, 0f, 23f, 0f)
				horizontalLineToRelative(0f)
				close()
				moveTo(22.12f, 18.72f)
				curveToRelative(0f, -0.55f, -0.45f, -1f, -1f, -1f)
				horizontalLineTo(2.88f)
				curveToRelative(-0.55f, 0f, -1f, 0.45f, -1f, 1f)
				reflectiveCurveToRelative(0.45f, 1f, 1f, 1f)
				horizontalLineToRelative(18.24f)
				curveToRelative(0.55f, 0f, 1f, -0.45f, 1f, -1f)
				close()
				moveTo(19.24f, 22.73f)
				curveToRelative(0f, -0.55f, -0.45f, -1f, -1f, -1f)
				horizontalLineTo(5.76f)
				curveToRelative(-0.55f, 0f, -1f, 0.45f, -1f, 1f)
				reflectiveCurveToRelative(0.45f, 1f, 1f, 1f)
				horizontalLineToRelative(12.48f)
				curveToRelative(0.55f, 0f, 1f, -0.45f, 1f, -1f)
				close()
			}
		}.build()
		return _Collection!!
	}

