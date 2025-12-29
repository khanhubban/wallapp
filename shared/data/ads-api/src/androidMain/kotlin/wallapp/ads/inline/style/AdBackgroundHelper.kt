package wallapp.ads.inline.style

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.StateListAnimator
import android.animation.TimeInterpolator
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.InsetDrawable
import android.graphics.drawable.RippleDrawable
import android.view.View
import android.widget.Button
import androidx.interpolator.view.animation.FastOutLinearInInterpolator
import wallapp.system.platform.PlatformFeature


class AdBackgroundHelper(
    private val adBackgroundInfo: AdBackgroundStyle,
    private val callToActionBackgroundInfo: AdBackgroundStyle?,
) {

    fun applyStyleTo(view: View) {
        if (adBackgroundInfo.hasInfo()) {
            view.background = Builder(adBackgroundInfo)
                .withOutline(true)
                .withBackground(true)
                .withRipple(true)
                .create()
            if (canUseForeground()) {
                view.foreground = Builder(
                    adBackgroundInfo
                )
                    .withOutline(true)
                    .create()
            }
        } else {
            view.background = null
            if (canUseForeground()) {
                view.foreground = null
            }
        }
    }

    fun applyStyleToCallToAction(button: Button) {
        if (callToActionBackgroundInfo != null) {
            val isOutlineStyle = callToActionBackgroundInfo.outlineColor != null
            button.background = Builder(
                callToActionBackgroundInfo
            )
                .withBackground(!isOutlineStyle)
                .withOutline(isOutlineStyle)
                .withRipple(true)
                .create()
            val elevationAnimator = createElevationAnimator(button, callToActionBackgroundInfo)
            button.stateListAnimator = elevationAnimator
        }
    }

    private fun createElevationAnimator(view: View, style: AdBackgroundStyle): StateListAnimator? {
        if (style.pressedElevation == 0) return null
        val animator = StateListAnimator()
        animator.addState(
            intArrayOf(android.R.attr.state_pressed),
            createElevationAnim(view, style.pressedElevation.toFloat())
        )
        animator.addState(IntArray(0), createElevationAnim(view, 0f))
        return animator
    }

    private fun createElevationAnim(view: View, target: Float): Animator {
        val animator: Animator = ObjectAnimator.ofFloat(view, View.TRANSLATION_Z, target)
        animator.duration = view.resources.getInteger(android.R.integer.config_shortAnimTime).toLong()
        animator.interpolator = ELEVATION_ANIM_INTERPOLATOR
        return animator
    }

    private fun canUseForeground(): Boolean {
        return PlatformFeature.CanUseForegroundService
    }

    internal class Builder(private val info: AdBackgroundStyle) {
        private var withOutline = false
        private var withBackground = false
        private var withRipple = false
        fun withOutline(withOutline: Boolean): Builder {
            this.withOutline = withOutline
            return this
        }

        fun withBackground(withBackground: Boolean): Builder {
            this.withBackground = withBackground
            return this
        }

        fun withRipple(withRipple: Boolean): Builder {
            this.withRipple = withRipple
            return this
        }

        private val rippleColor: Int
            get() {
                val outlineColor = info.outlineColor
                return if (outlineColor != null) Color.argb(
                    120,
                    Color.red(outlineColor),
                    Color.green(outlineColor),
                    Color.blue(outlineColor)
                ) else DEFAULT_RIPPLE_COLOR
            }

        fun create(): Drawable {
            if (withBackground && info.backgroundDrawable != null) {
                return info.backgroundDrawable
            }
            val content = createShapeDrawable()
            if (withBackground && info.backgroundColor != null) {
                content.setColor(info.backgroundColor)
            }
            if (withOutline && info.outlineColor != null) {
                content.setStroke(info.strokeWidth, info.outlineColor)
            }
            return if (withRipple) {
                val mask = createShapeDrawable()
                mask.setColor(Color.BLACK)
                withPadding(
                    RippleDrawable(
                        ColorStateList.valueOf(rippleColor),
                        content, mask
                    )
                )
            } else {
                withPadding(content)
            }
        }

        private fun withPadding(drawable: Drawable): Drawable {
            return if (info.verticalPadding != 0) {
                InsetDrawable(drawable, 0, info.verticalPadding, 0, info.verticalPadding)
            } else {
                drawable
            }
        }

        private fun createShapeDrawable(): GradientDrawable {
            val drawable = GradientDrawable()
            drawable.shape = GradientDrawable.RECTANGLE
            drawable.gradientType = GradientDrawable.LINEAR_GRADIENT
            if (info.roundedCornerSize != null) {
                drawable.cornerRadius = info.roundedCornerSize.toFloat()
            }
            return drawable
        }

        companion object {
            private const val DEFAULT_RIPPLE_COLOR = 0x33000000
        }
    }

    companion object {
        private val ELEVATION_ANIM_INTERPOLATOR: TimeInterpolator = FastOutLinearInInterpolator()
    }
}