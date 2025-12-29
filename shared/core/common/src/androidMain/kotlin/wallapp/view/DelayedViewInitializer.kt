package wallapp.view

import android.view.View

fun interface DelayedViewInitializer {
    fun initialize(view: View?)
}