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
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RadialGradient
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.drawable.Drawable

class ShimmerDrawable : Drawable() {
    private val mUpdateListener = AnimatorUpdateListener { invalidateSelf() }
    private val mShimmerPaint = Paint()
    private val mDrawRect = RectF()
    private var mValueAnimator: ValueAnimator? = null
    private var mShimmer: Shimmer? = null

    init {
        mShimmerPaint.isAntiAlias = true
    }

    fun setShimmer(shimmer: Shimmer?) {
        requireNotNull(shimmer) { "Given null shimmer" }
        mShimmer = shimmer
        mShimmerPaint.xfermode = PorterDuffXfermode(
            if (mShimmer!!.alphaShimmer) PorterDuff.Mode.DST_IN else PorterDuff.Mode.SRC_IN
        )
        updateShader()
        updateValueAnimator()
        invalidateSelf()
    }

    /** Starts the shimmer animation.  */
    fun startShimmer() {
        if (mValueAnimator != null && !isShimmerStarted && callback != null) {
            mValueAnimator!!.start()
        }
    }

    /** Stops the shimmer animation.  */
    fun stopShimmer() {
        if (mValueAnimator != null && isShimmerStarted) {
            mValueAnimator!!.cancel()
        }
    }

    val isShimmerStarted: Boolean
        /** Return whether the shimmer animation has been started.  */
        get() = mValueAnimator != null && mValueAnimator!!.isStarted

    public override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)
        val width = bounds.width()
        val height = bounds.height()
        mDrawRect[(2 * -width).toFloat(), (2 * -height).toFloat(), (4 * width).toFloat()] =
            (4 * height).toFloat()
        updateShader()
        maybeStartShimmer()
    }

    override fun draw(canvas: Canvas) {
        if (mShimmer == null) {
            return
        }
        val bounds = bounds
        val width = bounds.width().toFloat()
        val height = bounds.height().toFloat()
        val dx: Float
        val dy: Float
        val animatedValue = if (mValueAnimator != null) mValueAnimator!!.animatedFraction else 0f
        when (mShimmer!!.direction) {
            Shimmer.Direction.LEFT_TO_RIGHT -> {
                dx = offset(-width, width, animatedValue)
                dy = 0f
            }

            Shimmer.Direction.RIGHT_TO_LEFT -> {
                dx = offset(width, -width, animatedValue)
                dy = 0f
            }

            Shimmer.Direction.TOP_TO_BOTTOM -> {
                dx = 0f
                dy = offset(-height, height, animatedValue)
            }

            Shimmer.Direction.BOTTOM_TO_TOP -> {
                dx = 0f
                dy = offset(height, -height, animatedValue)
            }

            else -> {
                dx = offset(-width, width, animatedValue)
                dy = 0f
            }
        }
        val saveCount = canvas.save()
        canvas.rotate(mShimmer!!.tilt, width / 2f, height / 2f)
        canvas.translate(dx, dy)
        canvas.drawRect(mDrawRect, mShimmerPaint)
        canvas.restoreToCount(saveCount)
    }

    override fun setAlpha(alpha: Int) {
        // No-op, modify the Shimmer object you pass in instead
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        // No-op, modify the Shimmer object you pass in instead
    }

    override fun getOpacity(): Int {
        return if (mShimmer != null && (mShimmer!!.clipToChildren || mShimmer!!.alphaShimmer)) PixelFormat.TRANSLUCENT else PixelFormat.OPAQUE
    }

    private fun offset(start: Float, end: Float, percent: Float): Float {
        return start + (end - start) * percent
    }

    private fun updateValueAnimator() {
        if (mShimmer == null) {
            return
        }
        val started: Boolean
        if (mValueAnimator != null) {
            started = mValueAnimator!!.isStarted
            mValueAnimator!!.cancel()
            mValueAnimator!!.removeAllUpdateListeners()
        } else {
            started = false
        }
        mValueAnimator = ValueAnimator.ofFloat(
            0f,
            1f + (mShimmer!!.repeatDelay / mShimmer!!.animationDuration).toFloat()
        ).apply {
            repeatMode = mShimmer!!.repeatMode
            repeatCount = mShimmer!!.repeatCount
            duration = mShimmer!!.animationDuration + mShimmer!!.repeatDelay
            addUpdateListener(mUpdateListener)
            if (started) {
                start()
            }
        }

    }

    fun maybeStartShimmer() {
        if (mValueAnimator != null && !mValueAnimator!!.isStarted && mShimmer != null && mShimmer!!.autoStart && callback != null) {
            mValueAnimator!!.start()
        }
    }

    private fun updateShader() {
        val bounds = bounds
        val boundsWidth = bounds.width()
        val boundsHeight = bounds.height()
        if (boundsWidth == 0 || boundsHeight == 0 || mShimmer == null) {
            return
        }
        val width = mShimmer!!.width(boundsWidth)
        val height = mShimmer!!.height(boundsHeight)
        val shader: Shader
        shader = when (mShimmer!!.shape) {
            Shimmer.Shape.LINEAR -> {
                val vertical = (mShimmer!!.direction == Shimmer.Direction.TOP_TO_BOTTOM
                        || mShimmer!!.direction == Shimmer.Direction.BOTTOM_TO_TOP)
                val endX = if (vertical) 0 else width
                val endY = if (vertical) height else 0
                LinearGradient(
                    0f,
                    0f,
                    endX.toFloat(),
                    endY.toFloat(),
                    mShimmer!!.colors,
                    mShimmer!!.positions,
                    Shader.TileMode.CLAMP
                )
            }

            Shimmer.Shape.RADIAL -> RadialGradient(
                width / 2f,
                height / 2f, (Math.max(width, height) / Math.sqrt(2.0)).toFloat(),
                mShimmer!!.colors,
                mShimmer!!.positions,
                Shader.TileMode.CLAMP
            )

            else -> {
                val vertical = (mShimmer!!.direction == Shimmer.Direction.TOP_TO_BOTTOM
                        || mShimmer!!.direction == Shimmer.Direction.BOTTOM_TO_TOP)
                val endX = if (vertical) 0 else width
                val endY = if (vertical) height else 0
                LinearGradient(
                    0f,
                    0f,
                    endX.toFloat(),
                    endY.toFloat(),
                    mShimmer!!.colors,
                    mShimmer!!.positions,
                    Shader.TileMode.CLAMP
                )
            }
        }
        mShimmerPaint.shader = shader
    }
}