package wallapp.upsell

import android.view.View
import androidx.lifecycle.LifecycleOwner


interface UpsellSnackbarManager {

    fun configure(lifecycleOwner: LifecycleOwner, view: View, debugLabel: String)

}


class UpsellSnackbarManagerNoOp : UpsellSnackbarManager {

    override fun configure(lifecycleOwner: LifecycleOwner, view: View, debugLabel: String) { }
}