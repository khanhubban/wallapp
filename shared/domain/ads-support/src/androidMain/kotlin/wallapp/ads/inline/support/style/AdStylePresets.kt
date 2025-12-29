package wallapp.ads.inline.support.style

import android.content.res.Resources
import androidx.annotation.LayoutRes
import wallapp.ads.inline.style.AdStyle
import wallapp.ads.inline.style.AdStyleConfig
import wallapp.resources.R

fun createAdStyleBannerShort(
    adStyleConfig: AdStyleConfig,
    res: Resources,
    @LayoutRes layout: Int,
): AdStyle.Builder = AdStyle.Builder(
    adStyleConfig,
    res,
    "banner_short",
    layout,
    R.layout.view_ad_short_placeholder,
    res.getDimensionPixelSize(R.dimen.ad_height_short),
)


