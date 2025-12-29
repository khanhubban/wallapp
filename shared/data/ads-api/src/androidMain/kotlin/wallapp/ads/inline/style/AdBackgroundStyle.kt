package wallapp.ads.inline.style

import android.graphics.drawable.Drawable
import wallapp.annotation.ColorInt
import wallapp.annotation.Px


class AdBackgroundStyle internal constructor(
    @ColorInt val backgroundColor: Int?,
    val backgroundDrawable: Drawable?,
    @ColorInt val outlineColor: Int?,
    @Px val roundedCornerSize: Int?,
    val strokeWidth: Int,
    val verticalPadding: Int,
    val pressedElevation: Int
) {
    fun hasInfo(): Boolean {
        return backgroundColor != null || backgroundDrawable != null || outlineColor != null || roundedCornerSize != null
    }

    class Builder {
        @ColorInt private var backgroundColor: Int? = null
        private var backgroundDrawable: Drawable? = null

        @ColorInt private var outlineColor: Int? = null

        @Px private var roundedCornerSize: Int? = null
        private var strokeWidth = 0
        private var verticalPadding = 0
        private var pressedElevation = 0
        fun backgroundColor(@ColorInt backgroundColor: Int?): Builder {
            this.backgroundColor = backgroundColor
            return this
        }

        fun backgroundDrawable(background: Drawable?): Builder {
            backgroundDrawable = background
            return this
        }

        fun outlineColor(@ColorInt outlineColor: Int?): Builder {
            this.outlineColor = outlineColor
            return this
        }

        fun strokeWidth(@Px strokeWidth: Int): Builder {
            this.strokeWidth = strokeWidth
            return this
        }

        fun roundedCornerSize(@Px roundedCornerSize: Int): Builder {
            this.roundedCornerSize = roundedCornerSize
            return this
        }

        fun verticalPadding(verticalPadding: Int): Builder {
            this.verticalPadding = verticalPadding
            return this
        }

        fun pressedElevation(pressedElevation: Int): Builder {
            this.pressedElevation = pressedElevation
            return this
        }

        fun build(): AdBackgroundStyle {
            return AdBackgroundStyle(
                backgroundColor,
                backgroundDrawable,
                outlineColor,
                roundedCornerSize,
                strokeWidth,
                verticalPadding,
                pressedElevation,
            )
        }
    }

    companion object {
        var DEFAULT = AdBackgroundStyle(null, null, null, null, 0, 0, 0)
    }
}