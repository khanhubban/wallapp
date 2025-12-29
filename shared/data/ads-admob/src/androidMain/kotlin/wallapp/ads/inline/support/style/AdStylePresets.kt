package wallapp.ads.inline.support.style

import android.content.res.Resources
import android.view.ViewGroup
import wallapp.ads.inline.style.AdStyle
import wallapp.ads.inline.style.AdStyleConfig
import wallapp.resources.R

object AdStylePresets {

    fun adStyleAdmob(adStyleConfig: AdStyleConfig, res: Resources): AdStyle.Builder {
        return AdStyle.Builder(
            adStyleConfig,
            res,
            "banner_full",
            R.layout.view_layout_admob_ad,
            R.layout.view_layout_admob_ad_placeholder,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
}
