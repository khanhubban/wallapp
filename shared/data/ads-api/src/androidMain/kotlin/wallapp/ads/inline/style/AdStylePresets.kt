package wallapp.ads.inline.style

import android.content.res.Resources
import androidx.annotation.DimenRes
import androidx.annotation.LayoutRes
import wallapp.resources.R

@Suppress("unused")
object AdStylePresets {

    //  static final AdBackgroundHelper defaultBackground = new AdBackgroundHelper.Builder().create();
    fun adStyleBannerShortest(adStyleConfig: AdStyleConfig, res: Resources): AdStyle.Builder {
        return AdStyle.Builder(
            adStyleConfig,
            res,
            "banner_shortest",
            R.layout.view_ad_shortest,
            R.layout.view_ad_short_placeholder,
            res.getDimensionPixelSize(R.dimen.ad_height_shortest)
        )
    }

    fun adStyleBannerShort(adStyleConfig: AdStyleConfig, res: Resources): AdStyle.Builder {
        return AdStyle.Builder(
            adStyleConfig,
            res,
            "banner_short",
            R.layout.view_ad_short,
            R.layout.view_ad_short_placeholder,
            res.getDimensionPixelSize(R.dimen.ad_height_short),
        )
    }

    fun adStyleBannerMedium(adStyleConfig: AdStyleConfig, res: Resources): AdStyle.Builder {
        return AdStyle.Builder(
            adStyleConfig,
            res,
            "banner_medium",
            R.layout.view_ad_medium,
            R.layout.view_ad_medium_placeholder,
            res.getDimensionPixelSize(R.dimen.ad_height_medium),
        )
    }

    fun adStyleBannerFull(adStyleConfig: AdStyleConfig, res: Resources): AdStyle.Builder {
        return AdStyle.Builder(
            adStyleConfig,
            res,
            "banner_full",
            R.layout.view_ad_full,
            R.layout.view_ad_full_placeholder,
            res.getDimensionPixelSize(R.dimen.ad_height_tall),
        )
    }

    fun adStyleRoundedShort(adStyleConfig: AdStyleConfig, res: Resources): AdStyle.Builder {
        return AdStyle.Builder(
            adStyleConfig,
            res,
            "rounded_short",
            R.layout.view_ad_short_rounded,
            R.layout.view_ad_short_rounded,
            res.getDimensionPixelSize(R.dimen.ad_height_short_rounded),
        )
    }

    fun adStyleRoundedShort(
        adStyleConfig: AdStyleConfig,
        res: Resources,
        @LayoutRes layout: Int,
        @DimenRes height: Int
    ): AdStyle.Builder {
        return AdStyle.Builder(
            adStyleConfig,
            res,
            "rounded_short",
            layout,
            layout,
            res.getDimensionPixelSize(height),
        )
    }

    fun adStyleOutlineShort(adStyleConfig: AdStyleConfig, res: Resources): AdStyle.Builder {
        return AdStyle.Builder(
            adStyleConfig,
            res,
            "outline_short",
            R.layout.view_ad_short,
            R.layout.view_ad_short_placeholder,
            res.getDimensionPixelSize(R.dimen.ad_height_short),
        )
            .backgroundStyle(adOutlineBgStyleBuilder(adStyleConfig, res).build())
    }

    fun adStyleOutlineShortest(adStyleConfig: AdStyleConfig, res: Resources): AdStyle.Builder {
        return AdStyle.Builder(
            adStyleConfig,
            res,
            "outline_shortest",
            R.layout.view_ad_shortest,
            R.layout.view_ad_shortest_placeholder,
            res.getDimensionPixelSize(R.dimen.ad_height_shortest)
        )
            .backgroundStyle(adOutlineBgStyleBuilder(adStyleConfig, res).build())
    }

    fun adOutlineBgStyleBuilder(adStyleConfig: AdStyleConfig, res: Resources): AdBackgroundStyle.Builder {
        return AdBackgroundStyle.Builder()
            .roundedCornerSize(res.getDimensionPixelSize(R.dimen.ad_background_corner_radius))
            .strokeWidth(res.getDimensionPixelSize(R.dimen.ad_background_stroke_width))
            .outlineColor(adStyleConfig.accentColor)
    }

    fun callToActionOutlineBgStyleBuilder(adStyleConfig: AdStyleConfig, res: Resources): AdBackgroundStyle.Builder {
        return AdBackgroundStyle.Builder()
            .roundedCornerSize(res.getDimensionPixelSize(R.dimen.ad_call_to_action_outline_bg_corner_radius))
            .strokeWidth(res.getDimensionPixelSize(R.dimen.ad_call_to_action_stroke_width))
            .verticalPadding(res.getDimensionPixelSize(R.dimen.ad_call_to_action_outline_bg_vertical_padding))
            .outlineColor(adStyleConfig.accentColor)
    }

    @JvmStatic
    fun callToActionDefaultBgStyleBuilder(adStyleConfig: AdStyleConfig, res: Resources):
            AdBackgroundStyle.Builder = AdBackgroundStyle.Builder()
        .roundedCornerSize(res.getDimensionPixelSize(R.dimen.ad_call_to_action_default_bg_corner_radius))
        .backgroundColor(adStyleConfig.accentColor)
        .verticalPadding(res.getDimensionPixelSize(R.dimen.ad_call_to_action_default_bg_vertical_padding))
        .pressedElevation(res.getDimensionPixelSize(R.dimen.ad_call_to_action_default_bg_pressed_elevation))
}