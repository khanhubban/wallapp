package wallapp.math.geometry


//data class RectDp(
//    var left: Dp = Dp(0f),
//    var top: Dp = Dp(0f),
//    var right: Dp = Dp(0f),
//    var bottom: Dp = Dp(0f),
//) {
//    fun set(left: Dp = this.left, top: Dp = this.top,
//            right: Dp = this.right, bottom: Dp = this.bottom) {
//        this.left = left
//        this.top = top
//        this.right = right
//        this.bottom = bottom
//    }
//
//    fun set(rect: RectDp) {
//        left = rect.left
//        top = rect.top
//        right = rect.right
//        bottom = rect.bottom
//    }
//
//    fun offset(dx: Dp, dy: Dp) {
//        left += dx
//        top += dy
//        right += dx
//        bottom += dy
//    }
//
//    val width: Dp
//        get() = right - left
//
//    val height: Dp
//        get() = bottom - top
//
//    val aspectRatio: Float
//        get() = width * 1f / height
//
//    val centerX: Dp
//        get() = (left + right) * .5f
//
//    val centerY: Dp
//        get() = (top + bottom) * .5f
//
//    val center: PointF by lazy {
//        PointF(centerX.value, centerY.value)
//    }
//
//    override fun toString(): String {
//        return "left=%.3f, top=%.3f, right=%.3f, bottom=%.3f".format(
//            left.value, top.value, right.value, bottom.value,
//        )
//    }
//
//    fun asExportString(): String = JSONObject().apply {
//        put("l", left)
//        put("t", top)
//        put("r", right)
//        put("b", bottom)
//    }.toString()
//
//    companion object {
//
//        fun fromExportString(exportString: String): Rect {
//            val jsonObject = JSONObject(exportString)
//            val left = jsonObject.getDouble("l").toFloat()
//            val right = jsonObject.getDouble("r").toFloat()
//            val top = jsonObject.getDouble("t").toFloat()
//            val bottom = jsonObject.getDouble("b").toFloat()
//            return Rect(left, top, right, bottom)
//        }
//
//        fun from(rect: android.graphics.Rect?): Rect? {
//            return if (rect != null) {
//                Rect(
//                    rect.left.toFloat(),
//                    rect.top.toFloat(),
//                    rect.right.toFloat(),
//                    rect.bottom.toFloat()
//                )
//            } else {
//                null
//            }
//        }
//    }
//}


