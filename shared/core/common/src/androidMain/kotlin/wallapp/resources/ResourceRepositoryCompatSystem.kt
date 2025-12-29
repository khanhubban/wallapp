package wallapp.resources

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import androidx.annotation.ArrayRes
import androidx.annotation.AttrRes
import androidx.annotation.BoolRes
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.core.content.ContextCompat
import wallapp.annotation.ColorInt
import wallapp.bitmap.decodeSampledBitmapFromResource
import wallapp.context.resolveAttributeColor


class ResourceRepositoryCompatSystem(val context: Context): ResourceRepositoryCompat {
    private val resources by lazy { context.resources }

    override val deviceHeight: Int
        get() =  resources.displayMetrics.heightPixels

    override val deviceWidth: Int
        get() =  resources.displayMetrics.widthPixels

    @ColorInt override fun getColor(@ColorRes colorRes: Int): Int {
        return ContextCompat.getColor(context, colorRes)
    }

    @ColorInt override fun resolveAttributeColor(
        @AttrRes attrRes: Int,
        @ColorInt fallbackColor: Int,
    ): Int =  context.resolveAttributeColor(attrRes, fallbackColor = fallbackColor)

    override fun getStringArray(@ArrayRes arrayRes: Int): Array<String> = resources.getStringArray(arrayRes)

    override fun getIntArray(@ArrayRes arrayRes: Int): IntArray = resources.getIntArray(arrayRes)

    override fun getBoolean(@BoolRes boolRes: Int): Boolean = resources.getBoolean(boolRes)

    override fun getDimenPixelSize(dimenRes: Int): Int = resources.getDimensionPixelSize(dimenRes)

    override fun getDimension(dimenRes: Int): Float = resources.getDimension(dimenRes)

    override fun getDrawable(drawableRes: Int): Drawable? = ContextCompat.getDrawable(context, drawableRes)

    override suspend fun getBitmap(drawableRes: Int, @ColorInt color: Int?): Bitmap? {
        return BitmapFactory.decodeResource(resources, drawableRes) ?: getBitmapFromVectorDrawable(
            drawableRes,
            color = color
        )
    }

    override suspend fun getBitmap(drawableRes: Int, reqWidth: Int, reqHeight: Int, @ColorInt color: Int?): Bitmap? {
        return decodeSampledBitmapFromResource(resources, drawableRes, reqWidth, reqHeight)
            ?: getBitmapFromVectorDrawable(drawableRes, reqWidth, reqHeight, color)
    }

    private fun getBitmapFromVectorDrawable(
        @DrawableRes drawableId: Int,
        reqWidth: Int? = null,
        reqHeight: Int? = null,
        color: Int?
    ): Bitmap? {
        val drawable = getDrawable(drawableId) ?: return null
        color?.let {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
//                drawable.colorFilter = BlendModeColorFilter(color, BlendMode.SRC_ATOP)
//            else
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
        }
        val bitmap = Bitmap.createBitmap(
            reqWidth ?: drawable.intrinsicWidth,
            reqHeight ?: drawable.intrinsicHeight, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    override fun getResourceEntryName(@IdRes resId: Int): String? {
        return resources.getResourceEntryName(resId)
    }

    override fun getAssetFileDescriptor(rawRes: Int): AssetFileDescriptor {
        return resources.openRawResourceFd(rawRes)
    }

    override fun dpToPx(dp: Float): Float = dp * (resources.displayMetrics.densityDpi / 160f)
    override fun dpToPx(dp: Int): Float = dpToPx(dp.toFloat())

    override fun pxToDp(px: Float): Float = px / (resources.displayMetrics.densityDpi / 160f)
    override fun pxToDp(px: Int): Float = pxToDp(px.toFloat())
}
