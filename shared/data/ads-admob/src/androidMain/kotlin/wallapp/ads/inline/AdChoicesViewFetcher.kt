package wallapp.ads.inline

import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.OnHierarchyChangeListener
import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.ads.nativead.NativeAdView

class AdChoicesViewFetcher internal constructor(
    adChoicesContainer: ViewGroup,
) {
    constructor(nativeAd: NativeAdView) : this(getAdChoicesContainer(nativeAd))

    private val _adChoicesView = MutableLiveData<View>()
    val adChoicesView: LiveData<View>
        get() = _adChoicesView

    init {
        adChoicesContainer.setOnHierarchyChangeListener(HierarchyChangeListener())
    }

    private inner class HierarchyChangeListener : OnHierarchyChangeListener {
        override fun onChildViewAdded(parent: View, child: View) {
            val adChoices = findAdChoicesImage(child)
            if (adChoices != null) {
                _adChoicesView.value = adChoices
            }
        }

        override fun onChildViewRemoved(parent: View, child: View) {}
    }

    companion object {
        private fun getAdChoicesContainer(viewGroup: ViewGroup): ViewGroup {
            //Ad choices container is always the last child
            //see overrides of bringChildToFront and addView methods
            return viewGroup.getChildAt(viewGroup.childCount - 1) as ViewGroup
        }

        private fun findAdChoicesImage(parent: View): View? {
            if (parent is ViewGroup) {
                val group = parent
                if (group.childCount == 1 && group.getChildAt(0) is ImageView) {
                    return group
                } else {
                    for (i in 0 until group.childCount) {
                        val result = findAdChoicesImage(group.getChildAt(i))
                        if (result != null) {
                            return result
                        }
                    }
                }
            }
            return null
        }
    }

}