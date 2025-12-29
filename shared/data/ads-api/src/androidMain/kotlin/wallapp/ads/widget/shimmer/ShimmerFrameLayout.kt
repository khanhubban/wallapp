/**
 * Copyright (c) 2015-present, Facebook, Inc. All rights reserved.
 *
 *
 * This source code is licensed under the BSD-style license found in the LICENSE file in the root
 * directory of this source tree. An additional grant of patent rights can be found in the PATENTS
 * file in the same directory.
 */
package wallapp.ads.widget.shimmer

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.widget.FrameLayout
import wallapp.ads.widget.shimmer.Shimmer.AlphaHighlightBuilder
import wallapp.ads.widget.shimmer.Shimmer.ColorHighlightBuilder
import wallapp.resources.R

/**
 * Shimmer is an Android library that provides an easy way to add a shimmer effect to any [ ]. It is useful as an unobtrusive loading indicator, and was originally
 * developed for Facebook Home.
 *
 *
 * Find more examples and usage instructions over at: facebook.github.io/shimmer-android
 */
class ShimmerFrameLayout : FrameLayout {
    private val mContentPaint = Paint()
    private val mShimmerDrawable = ShimmerDrawable()

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context, attrs)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(
        context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        setWillNotDraw(false)
        mShimmerDrawable.callback = this
        if (attrs == null) {
            setShimmer(AlphaHighlightBuilder().build())
            return
        }
        val a = context.obtainStyledAttributes(attrs, R.styleable.ShimmerFrameLayout, 0, 0)
        try {
            val shimmerBuilder = if (a.hasValue(R.styleable.ShimmerFrameLayout_shimmer_colored)
                && a.getBoolean(R.styleable.ShimmerFrameLayout_shimmer_colored, false)
            ) ColorHighlightBuilder() else AlphaHighlightBuilder()
            setShimmer(shimmerBuilder.consumeAttributes(a)?.build())
        } finally {
            a.recycle()
        }
    }

    fun setShimmer(shimmer: Shimmer?): ShimmerFrameLayout {
        requireNotNull(shimmer) { "Given null shimmer" }
        mShimmerDrawable.setShimmer(shimmer)
        if (shimmer.clipToChildren) {
            setLayerType(LAYER_TYPE_HARDWARE, mContentPaint)
        } else {
            setLayerType(LAYER_TYPE_NONE, null)
        }
        return this
    }

    /** Starts the shimmer animation.  */
    fun startShimmer() {
        mShimmerDrawable.startShimmer()
    }

    /** Stops the shimmer animation.  */
    fun stopShimmer() {
        mShimmerDrawable.stopShimmer()
    }

    val isShimmerStarted: Boolean
        /** Return whether the shimmer animation has been started.  */
        get() = mShimmerDrawable.isShimmerStarted

    public override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        val width = width
        val height = height
        mShimmerDrawable.setBounds(0, 0, width, height)
    }

    public override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        mShimmerDrawable.maybeStartShimmer()
    }

    public override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopShimmer()
    }

    public override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        mShimmerDrawable.draw(canvas)
    }

    override fun verifyDrawable(who: Drawable): Boolean {
        return super.verifyDrawable(who) || who === mShimmerDrawable
    }
}