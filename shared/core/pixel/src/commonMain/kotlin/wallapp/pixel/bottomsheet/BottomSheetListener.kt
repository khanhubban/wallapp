package wallapp.pixel.bottomsheet

fun interface BottomSheetListener {

    fun onSlide(slideOffset: Float, fraction: Float)
}