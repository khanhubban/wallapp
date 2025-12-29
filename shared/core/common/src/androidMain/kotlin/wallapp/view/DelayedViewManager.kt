package wallapp.view

import android.view.View
import wallapp.common.R

object DelayedViewManager {

    fun executeDelayedInitializer(v: View) {
        val initializer = v.getTag(R.id.key_delayed_initializer)
        if (initializer is DelayedViewInitializer) {
            initializer.initialize(v)
            v.setTag(R.id.key_delayed_initializer, null)
        }
    }

    @JvmStatic
    fun delayInitialization(v: View, initializer: DelayedViewInitializer?) {
        v.setTag(R.id.key_delayed_initializer, initializer)
    }
}