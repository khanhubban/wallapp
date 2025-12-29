import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import wallapp.resources.vector.AppIcon


private var _Share: ImageVector? = null

val AppIcon.Share: ImageVector
	get() {
		if (_Share != null) {
			return _Share!!
		}
		_Share = ImageVector.Builder(
			name = "Share",
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
				moveTo(24f, 13.63f)
				verticalLineToRelative(9.36f)
				curveToRelative(0f, 0.55f, -0.45f, 1f, -1f, 1f)
				horizontalLineTo(1f)
				curveTo(0.45f, 23.99f, 0f, 23.55f, 0f, 22.99f)
				verticalLineToRelative(-9.36f)
				curveTo(0f, 13.08f, 0.45f, 12.63f, 1f, 12.63f)
				reflectiveCurveToRelative(1f, 0.45f, 1f, 1f)
				verticalLineToRelative(8.36f)
				horizontalLineToRelative(20f)
				verticalLineToRelative(-8.36f)
				curveToRelative(0f, -0.55f, 0.45f, -1f, 1f, -1f)
				reflectiveCurveToRelative(1f, 0.45f, 1f, 1f)
				close()
				moveTo(6.7f, 7.72f)
				lineToRelative(4.3f, -4.3f)
				verticalLineToRelative(12.62f)
				curveToRelative(0f, 0.55f, 0.45f, 1f, 1f, 1f)
				reflectiveCurveToRelative(1f, -0.45f, 1f, -1f)
				verticalLineTo(3.43f)
				lineToRelative(4.43f, 4.43f)
				curveToRelative(0.2f, 0.2f, 0.45f, 0.29f, 0.71f, 0.29f)
				reflectiveCurveToRelative(0.51f, -0.1f, 0.71f, -0.29f)
				curveToRelative(0.39f, -0.39f, 0.39f, -1.02f, 0f, -1.41f)
				lineTo(12.7f, 0.3f)
				curveToRelative(-0.39f, -0.39f, -1.02f, -0.39f, -1.41f, 0f)
				lineToRelative(-6.01f, 6f)
				curveToRelative(-0.39f, 0.39f, -0.39f, 1.02f, 0f, 1.41f)
				reflectiveCurveToRelative(1.02f, 0.39f, 1.41f, 0f)
				close()
			}
		}.build()
		return _Share!!
	}
