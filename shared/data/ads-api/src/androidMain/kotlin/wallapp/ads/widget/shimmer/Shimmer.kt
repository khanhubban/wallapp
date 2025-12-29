/**
 * Copyright (c) 2015-present, Facebook, Inc. All rights reserved.
 *
 *
 * This source code is licensed under the BSD-style license found in the LICENSE file in the root
 * directory of this source tree. An additional grant of patent rights can be found in the PATENTS
 * file in the same directory.
 */
package wallapp.ads.widget.shimmer

import android.animation.ValueAnimator
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.RectF
import android.util.AttributeSet
import wallapp.annotation.ColorInt
import wallapp.annotation.FloatRange
import wallapp.annotation.Px
import wallapp.resources.R

/**
 * A Shimmer is an object detailing all of the configuration options available for [ ]
 */
class Shimmer internal constructor() {
    /** The shape of the shimmer's highlight. By default LINEAR is used.  */
    @Retention(AnnotationRetention.SOURCE)
//    @IntDef([Shape.LINEAR, Shape.RADIAL])
    annotation class Shape {
        companion object {
            /** Linear gives a ray reflection effect.  */
            var LINEAR = 0

            /** Radial gives a spotlight effect.  */
            var RADIAL = 1
        }
    }

    /** Direction of the shimmer's sweep.  */
    @Retention(AnnotationRetention.SOURCE)
//    @IntDef([Direction.LEFT_TO_RIGHT, Direction.TOP_TO_BOTTOM, Direction.RIGHT_TO_LEFT, Direction.BOTTOM_TO_TOP])
    annotation class Direction {
        companion object {
            var LEFT_TO_RIGHT = 0
            var TOP_TO_BOTTOM = 1
            var RIGHT_TO_LEFT = 2
            var BOTTOM_TO_TOP = 3
        }
    }

    val positions = FloatArray(COMPONENT_COUNT)
    val colors = IntArray(COMPONENT_COUNT)
    val bounds = RectF()

    @Direction
    var direction = Direction.LEFT_TO_RIGHT

    @ColorInt
    var highlightColor = Color.WHITE

    @ColorInt
    var baseColor = 0x4cffffff

    @Shape
    var shape = Shape.LINEAR
    var fixedWidth = 0
    var fixedHeight = 0
    var widthRatio = 1f
    var heightRatio = 1f
    var intensity = 0f
    var dropoff = 0.5f
    var tilt = 20f
    var clipToChildren = true
    var autoStart = true
    var alphaShimmer = true
    var repeatCount = ValueAnimator.INFINITE
    var repeatMode = ValueAnimator.RESTART
    var animationDuration = 1000L
    var repeatDelay: Long = 0
    fun width(width: Int): Int {
        return if (fixedWidth > 0) fixedWidth else Math.round(widthRatio * width)
    }

    fun height(height: Int): Int {
        return if (fixedHeight > 0) fixedHeight else Math.round(heightRatio * height)
    }

    fun updateColors() {
        when (shape) {
            Shape.LINEAR -> {
                colors[0] = baseColor
                colors[1] = highlightColor
                colors[2] = highlightColor
                colors[3] = baseColor
            }

            Shape.RADIAL -> {
                colors[0] = highlightColor
                colors[1] = highlightColor
                colors[2] = baseColor
                colors[3] = baseColor
            }

            else -> {
                colors[0] = baseColor
                colors[1] = highlightColor
                colors[2] = highlightColor
                colors[3] = baseColor
            }
        }
    }

    fun updatePositions() {
        when (shape) {
            Shape.LINEAR -> {
                positions[0] = Math.max((1f - intensity - dropoff) / 2f, 0f)
                positions[1] = Math.max((1f - intensity - 0.001f) / 2f, 0f)
                positions[2] = Math.min((1f + intensity + 0.001f) / 2f, 1f)
                positions[3] = Math.min((1f + intensity + dropoff) / 2f, 1f)
            }

            Shape.RADIAL -> {
                positions[0] = 0f
                positions[1] = Math.min(intensity, 1f)
                positions[2] = Math.min(intensity + dropoff, 1f)
                positions[3] = 1f
            }

            else -> {
                positions[0] = Math.max((1f - intensity - dropoff) / 2f, 0f)
                positions[1] = Math.max((1f - intensity - 0.001f) / 2f, 0f)
                positions[2] = Math.min((1f + intensity + 0.001f) / 2f, 1f)
                positions[3] = Math.min((1f + intensity + dropoff) / 2f, 1f)
            }
        }
    }

    fun updateBounds(viewWidth: Int, viewHeight: Int) {
        val magnitude = Math.max(viewWidth, viewHeight)
        val rad = Math.PI / 2f - Math.toRadians((tilt % 90f).toDouble())
        val hyp = magnitude / Math.sin(rad)
        val padding = 3 * Math.round((hyp - magnitude).toFloat() / 2f)
        bounds[-padding.toFloat(), -padding.toFloat(), (width(viewWidth) + padding).toFloat()] =
            (height(viewHeight) + padding).toFloat()
    }

    abstract class Builder<T : Builder<T>?> {
        val mShimmer = Shimmer()
        protected abstract val getThis: T

        /** Applies all specified options from the [AttributeSet].  */
        fun consumeAttributes(context: Context, attrs: AttributeSet?): T {
            val a = context.obtainStyledAttributes(attrs, R.styleable.ShimmerFrameLayout, 0, 0)
            return consumeAttributes(a)
        }

        open fun consumeAttributes(a: TypedArray): T {
            if (a.hasValue(R.styleable.ShimmerFrameLayout_shimmer_clip_to_children)) {
                setClipToChildren(
                    a.getBoolean(
                        R.styleable.ShimmerFrameLayout_shimmer_clip_to_children,
                        mShimmer.clipToChildren
                    )
                )
            }
            if (a.hasValue(R.styleable.ShimmerFrameLayout_shimmer_auto_start)) {
                setAutoStart(
                    a.getBoolean(
                        R.styleable.ShimmerFrameLayout_shimmer_auto_start,
                        mShimmer.autoStart
                    )
                )
            }
            if (a.hasValue(R.styleable.ShimmerFrameLayout_shimmer_base_alpha)) {
                setBaseAlpha(a.getFloat(R.styleable.ShimmerFrameLayout_shimmer_base_alpha, 0.3f))
            }
            if (a.hasValue(R.styleable.ShimmerFrameLayout_shimmer_highlight_alpha)) {
                setHighlightAlpha(
                    a.getFloat(
                        R.styleable.ShimmerFrameLayout_shimmer_highlight_alpha,
                        1f
                    )
                )
            }
            if (a.hasValue(R.styleable.ShimmerFrameLayout_shimmer_duration)) {
                setDuration(
                    a.getInt(
                        R.styleable.ShimmerFrameLayout_shimmer_duration,
                        mShimmer.animationDuration.toInt()
                    ).toLong()
                )
            }
            if (a.hasValue(R.styleable.ShimmerFrameLayout_shimmer_repeat_count)) {
                setRepeatCount(
                    a.getInt(
                        R.styleable.ShimmerFrameLayout_shimmer_repeat_count,
                        mShimmer.repeatCount
                    )
                )
            }
            if (a.hasValue(R.styleable.ShimmerFrameLayout_shimmer_repeat_delay)) {
                setRepeatDelay(
                    a.getInt(
                        R.styleable.ShimmerFrameLayout_shimmer_repeat_delay,
                        mShimmer.repeatDelay.toInt()
                    ).toLong()
                )
            }
            if (a.hasValue(R.styleable.ShimmerFrameLayout_shimmer_repeat_mode)) {
                setRepeatMode(
                    a.getInt(
                        R.styleable.ShimmerFrameLayout_shimmer_repeat_mode,
                        mShimmer.repeatMode
                    )
                )
            }
            if (a.hasValue(R.styleable.ShimmerFrameLayout_shimmer_direction)) {
                val direction =
                    a.getInt(R.styleable.ShimmerFrameLayout_shimmer_direction, mShimmer.direction)
                when (direction) {
                    Direction.LEFT_TO_RIGHT -> setDirection(Direction.LEFT_TO_RIGHT)
                    Direction.TOP_TO_BOTTOM -> setDirection(Direction.TOP_TO_BOTTOM)
                    Direction.RIGHT_TO_LEFT -> setDirection(Direction.RIGHT_TO_LEFT)
                    Direction.BOTTOM_TO_TOP -> setDirection(Direction.BOTTOM_TO_TOP)
                    else -> setDirection(Direction.LEFT_TO_RIGHT)
                }
            }
            if (a.hasValue(R.styleable.ShimmerFrameLayout_shimmer_shape)) {
                val shape = a.getInt(R.styleable.ShimmerFrameLayout_shimmer_shape, mShimmer.shape)
                when (shape) {
                    Shape.LINEAR -> setShape(Shape.LINEAR)
                    Shape.RADIAL -> setShape(Shape.RADIAL)
                    else -> setShape(Shape.LINEAR)
                }
            }
            if (a.hasValue(R.styleable.ShimmerFrameLayout_shimmer_dropoff)) {
                setDropoff(
                    a.getFloat(
                        R.styleable.ShimmerFrameLayout_shimmer_dropoff,
                        mShimmer.dropoff
                    )
                )
            }
            if (a.hasValue(R.styleable.ShimmerFrameLayout_shimmer_fixed_width)) {
                setFixedWidth(
                    a.getDimensionPixelSize(
                        R.styleable.ShimmerFrameLayout_shimmer_fixed_width, mShimmer.fixedWidth
                    )
                )
            }
            if (a.hasValue(R.styleable.ShimmerFrameLayout_shimmer_fixed_height)) {
                setFixedHeight(
                    a.getDimensionPixelSize(
                        R.styleable.ShimmerFrameLayout_shimmer_fixed_height, mShimmer.fixedHeight
                    )
                )
            }
            if (a.hasValue(R.styleable.ShimmerFrameLayout_shimmer_intensity)) {
                setIntensity(
                    a.getFloat(R.styleable.ShimmerFrameLayout_shimmer_intensity, mShimmer.intensity)
                )
            }
            if (a.hasValue(R.styleable.ShimmerFrameLayout_shimmer_width_ratio)) {
                setWidthRatio(
                    a.getFloat(
                        R.styleable.ShimmerFrameLayout_shimmer_width_ratio,
                        mShimmer.widthRatio
                    )
                )
            }
            if (a.hasValue(R.styleable.ShimmerFrameLayout_shimmer_height_ratio)) {
                setHeightRatio(
                    a.getFloat(
                        R.styleable.ShimmerFrameLayout_shimmer_height_ratio,
                        mShimmer.heightRatio
                    )
                )
            }
            if (a.hasValue(R.styleable.ShimmerFrameLayout_shimmer_tilt)) {
                setTilt(a.getFloat(R.styleable.ShimmerFrameLayout_shimmer_tilt, mShimmer.tilt))
            }
            return getThis
        }

        /** Sets the direction of the shimmer's sweep. See [Direction].  */
        fun setDirection(@Direction direction: Int): T {
            mShimmer.direction = direction
            return getThis
        }

        /** Sets the shape of the shimmer. See [Shape].  */
        fun setShape(@Shape shape: Int): T {
            mShimmer.shape = shape
            return getThis
        }

        /** Sets the fixed width of the shimmer, in pixels.  */
        fun setFixedWidth(@Px fixedWidth: Int): T {
            require(fixedWidth >= 0) { "Given invalid width: $fixedWidth" }
            mShimmer.fixedWidth = fixedWidth
            return getThis
        }

        /** Sets the fixed height of the shimmer, in pixels.  */
        fun setFixedHeight(@Px fixedHeight: Int): T {
            require(fixedHeight >= 0) { "Given invalid height: $fixedHeight" }
            mShimmer.fixedHeight = fixedHeight
            return getThis
        }

        /** Sets the width ratio of the shimmer, multiplied against the total width of the layout.  */
        fun setWidthRatio(widthRatio: Float): T {
            require(widthRatio >= 0f) { "Given invalid width ratio: $widthRatio" }
            mShimmer.widthRatio = widthRatio
            return getThis
        }

        /** Sets the height ratio of the shimmer, multiplied against the total height of the layout.  */
        fun setHeightRatio(heightRatio: Float): T {
            require(heightRatio >= 0f) { "Given invalid height ratio: $heightRatio" }
            mShimmer.heightRatio = heightRatio
            return getThis
        }

        /** Sets the intensity of the shimmer. A larger value causes the shimmer to be larger.  */
        fun setIntensity(intensity: Float): T {
            require(intensity >= 0f) { "Given invalid intensity value: $intensity" }
            mShimmer.intensity = intensity
            return getThis
        }

        /**
         * Sets how quickly the shimmer's gradient drops-off. A larger value causes a sharper drop-off.
         */
        fun setDropoff(dropoff: Float): T {
            require(dropoff >= 0f) { "Given invalid dropoff value: $dropoff" }
            mShimmer.dropoff = dropoff
            return getThis
        }

        /** Sets the tilt angle of the shimmer in degrees.  */
        fun setTilt(tilt: Float): T {
            mShimmer.tilt = tilt
            return getThis
        }

        /**
         * Sets the base alpha, which is the alpha of the underlying children, amount in the range [0,
         * 1].
         */
        fun setBaseAlpha(@FloatRange(from = 0.0, to = 1.0) alpha: Float): T {
            val intAlpha = (clamp(0f, 1f, alpha) * 255f).toInt()
            mShimmer.baseColor = intAlpha shl 24 or (mShimmer.baseColor and 0x00FFFFFF)
            return getThis
        }

        /** Sets the shimmer alpha amount in the range [0, 1].  */
        fun setHighlightAlpha(@FloatRange(from = 0.0, to = 1.0) alpha: Float): T {
            val intAlpha = (clamp(0f, 1f, alpha) * 255f).toInt()
            mShimmer.highlightColor = intAlpha shl 24 or (mShimmer.highlightColor and 0x00FFFFFF)
            return getThis
        }

        /**
         * Sets whether the shimmer will clip to the childrens' contents, or if it will opaquely draw on
         * top of the children.
         */
        fun setClipToChildren(status: Boolean): T {
            mShimmer.clipToChildren = status
            return getThis
        }

        /** Sets whether the shimmering animation will start automatically.  */
        fun setAutoStart(status: Boolean): T {
            mShimmer.autoStart = status
            return getThis
        }

        /**
         * Sets how often the shimmering animation will repeat. See [ ][android.animation.ValueAnimator.setRepeatCount].
         */
        fun setRepeatCount(repeatCount: Int): T {
            mShimmer.repeatCount = repeatCount
            return getThis
        }

        /**
         * Sets how the shimmering animation will repeat. See [ ][android.animation.ValueAnimator.setRepeatMode].
         */
        fun setRepeatMode(mode: Int): T {
            mShimmer.repeatMode = mode
            return getThis
        }

        /** Sets how long to wait in between repeats of the shimmering animation.  */
        fun setRepeatDelay(millis: Long): T {
            require(millis >= 0) { "Given a negative repeat delay: $millis" }
            mShimmer.repeatDelay = millis
            return getThis
        }

        /** Sets how long the shimmering animation takes to do one full sweep.  */
        fun setDuration(millis: Long): T {
            require(millis >= 0) { "Given a negative duration: $millis" }
            mShimmer.animationDuration = millis
            return getThis
        }

        fun build(): Shimmer {
            mShimmer.updateColors()
            mShimmer.updatePositions()
            return mShimmer
        }

        companion object {
            private fun clamp(min: Float, max: Float, value: Float): Float {
                return Math.min(max, Math.max(min, value))
            }
        }
    }

    class AlphaHighlightBuilder : Builder<AlphaHighlightBuilder>() {
        init {
            mShimmer.alphaShimmer = true
        }

        override val getThis: AlphaHighlightBuilder
            protected get() = this
    }

    class ColorHighlightBuilder : Builder<ColorHighlightBuilder>() {
        init {
            mShimmer.alphaShimmer = false
        }

        /** Sets the highlight color for the shimmer.  */
        fun setHighlightColor(@ColorInt color: Int): ColorHighlightBuilder {
            mShimmer.highlightColor = color
            return getThis
        }

        /** Sets the base color for the shimmer.  */
        fun setBaseColor(@ColorInt color: Int): ColorHighlightBuilder {
            mShimmer.baseColor = mShimmer.baseColor and -0x1000000 or (color and 0x00FFFFFF)
            return getThis
        }

        override fun consumeAttributes(a: TypedArray): ColorHighlightBuilder {
            super.consumeAttributes(a)
            if (a.hasValue(R.styleable.ShimmerFrameLayout_shimmer_base_color)) {
                setBaseColor(
                    a.getColor(
                        R.styleable.ShimmerFrameLayout_shimmer_base_color,
                        mShimmer.baseColor
                    )
                )
            }
            if (a.hasValue(R.styleable.ShimmerFrameLayout_shimmer_highlight_color)) {
                setHighlightColor(
                    a.getColor(
                        R.styleable.ShimmerFrameLayout_shimmer_highlight_color,
                        mShimmer.highlightColor
                    )
                )
            }
            return getThis
        }

        override val getThis: ColorHighlightBuilder
            protected get() = this
    }

    companion object {
        private const val COMPONENT_COUNT = 4
    }
}