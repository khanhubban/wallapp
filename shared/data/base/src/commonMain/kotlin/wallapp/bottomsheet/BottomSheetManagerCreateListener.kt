package wallapp.bottomsheet

/**
 * (Hopefully) short-lived interface to to notify a class about the creation of a
 * [BottomSheetManager].
 */
interface BottomSheetManagerCreateListener {

    fun onBottomSheetManagerCreate(bottomSheetManager: Any)
}