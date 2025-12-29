import androidx.compose.foundation.shape.CornerSize
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import wallapp.pixel.theme.shape.AppCornerBasedShape


class CustomContentShape(
    topStart: CornerSize,
    topEnd: CornerSize,
    bottomEnd: CornerSize,
    bottomStart: CornerSize,
    defaultCornerRadius: CornerSize,
) : AppCornerBasedShape(
    topStart = topStart,
    topEnd = topEnd,
    bottomEnd = bottomEnd,
    bottomStart = bottomStart,
    defaultCornerRadius = defaultCornerRadius
) {


    override fun copy(
        topStart: CornerSize,
        topEnd: CornerSize,
        bottomEnd: CornerSize,
        bottomStart: CornerSize,
        defaultCornerRadius: CornerSize,
    ) = CustomContentShape(
        topStart = topStart,
        topEnd = topEnd,
        bottomEnd = bottomEnd,
        bottomStart = bottomStart,
        defaultCornerRadius = defaultCornerRadius
    )


    override fun createOutline(
        size: Size,
        topStart: Float,
        topEnd: Float,
        bottomEnd: Float,
        bottomStart: Float,
        defaultCornerRadius: Float,
        layoutDirection: LayoutDirection
    ): Outline {

        return Outline.Generic(
            Path().apply {
                moveTo(0f, topStart)
                // Line to top end corner
                lineTo(topStart, 0f)

                if (topStart == 0f) {
                    /// top start
                    arcTo(
                        Rect(
                            0f,
                            0f,
                            defaultCornerRadius * 2f,
                            defaultCornerRadius * 2f,
                        ),
                        180f,
                        90f,
                        true
                    )

                } else {
                    lineTo(size.width - topEnd, 0f)
                }

                lineTo(size.width - topEnd, 0f)
                // Arc to bottom end corner

                if (topEnd == 0f) {
                    arcTo(
                        Rect(
                            size.width - defaultCornerRadius * 2f,
                            0f,
                            size.width,
                            defaultCornerRadius * 2f,
                        ),
                        270f,
                        90f,
                        false
                    )
                } else {
                    lineTo(size.width, topEnd)
                }

                lineTo(size.width, size.height - bottomEnd)

                if (bottomEnd == 0f) {
                    arcTo(
                        Rect(
                            size.width - defaultCornerRadius * 2f,
                            size.height - defaultCornerRadius * 2f,
                            size.width,
                            size.height
                        ),
                        0f,
                        90f,
                        false
                    )
                } else {
                    lineTo(size.width - bottomEnd, size.height)
                }
                lineTo(bottomStart, size.height)
                if (bottomStart == 0f) {
                    arcTo(
                        Rect(
                            0f,
                            size.height - defaultCornerRadius * 2f,
                            defaultCornerRadius * 2f,
                            size.height
                        ),
                        90f,
                        90f,
                        false
                    )
                } else {
                    lineTo(0f, size.height - bottomStart)
                }
                close()
            }
        )
    }

    override fun toString(): String {
        return "CustomContentShape(topStart = $topStart, topEnd = $topEnd, bottomEnd = " +
                "$bottomEnd, bottomStart = $bottomStart)"
    }


}


fun CustomContentShape(
    topStart: Dp = 0.dp,
    topEnd: Dp = 0.dp,
    bottomEnd: Dp = 0.dp,
    bottomStart: Dp = 0.dp,
    radius: Dp = 5.dp
) = CustomContentShape(
    topStart = CornerSize(topStart),
    topEnd = CornerSize(topEnd),
    bottomEnd = CornerSize(bottomEnd),
    bottomStart = CornerSize(bottomStart),
    defaultCornerRadius = CornerSize(radius),
)

