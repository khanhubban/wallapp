package wallapp.ads.inline.support

import android.content.Context
import android.view.View
import wallapp.ads.inline.InlineAdConfigAndroid
import wallapp.ads.inline.InlineAdDescriptor
import wallapp.ads.inline.InlineAdInitDescriptorsAdMob
import wallapp.ads.inline.InlineAdSource
import wallapp.ads.inline.native.InlineAdDescriptorAdMobNative
import wallapp.ads.inline.style.AdStyleConfig
import wallapp.ads.inline.support.style.AdStylePresets
import wallapp.di.Lazy
import wallapp.graphics.Colors
import wallapp.log.Log

class InlineAdConfigFactoryAdMob(
    private val context: Context,
    private val adInitDescriptors: InlineAdInitDescriptorsAdMob,
    private val inlineAdConfigFactory: Lazy<InlineAdConfigFactory>,
) : InlineAdConfigFactory {

    val color: Int
        get() = Colors.Accent.value.toInt()
    val adStyleConfig = AdStyleConfig(color)

    private fun createInlineAdConfigAdMobNative(
        adDescriptor: InlineAdDescriptorAdMobNative,
        fallbackAdDescriptor: InlineAdDescriptor?,
    ) : InlineAdConfigAndroid {
        val fallbackAdConfig = fallbackAdDescriptor?.let {
            inlineAdConfigFactory.get().createAdConfig(fallbackAdDescriptor, null)
        }
//        val navigationArguments = BuyLicenseUpsellNavigationArguments(
//            featureMeterState.buyLicenseUpsellAddsFeatureMeterToStack,
//            adDescriptor.appUiLocation,
//        )

        val adInitDescriptor = adInitDescriptors.getByInlineAdDescriptor(adDescriptor)

//        val toBuyLicenseUpsell: ((view: View) -> Unit) = { view ->
//            TODO("Culled")
//            navigationActions.navigateToBuyLicenseUpsell(
//                view.findFragment(),
//                navigationController,
//                navigationArguments,
//            )
//        }

        val onCloseClickListener = View.OnClickListener {
            Log.d("[AdDebug] onCloseClickListener()")
//            toBuyLicenseUpsell.invoke(it)
        }

        return InlineAdConfigAndroid.Builder(
            InlineAdSource.AdMob,
            adDescriptor,
            adInitDescriptor,
            AdStylePresets.adStyleAdmob(adStyleConfig, context.resources)
                .noTextAndActionStyle()
                .create(),
        ).apply {
            fallbackAdConfig(fallbackAdConfig)
            onPlaceholderClickListener {
//                toBuyLicenseUpsell.invoke(it)
            }
            onPlaceholderCloseClickListener(onCloseClickListener)
            onCloseClickListener(onCloseClickListener)
        }.create(context.resources)
    }

    override fun createAdConfig(
        adDescriptor: InlineAdDescriptor,
        fallbackAdDescriptor: InlineAdDescriptor?,
    ): InlineAdConfigAndroid? {
        if (adDescriptor is InlineAdDescriptorAdMobNative) {
            return createInlineAdConfigAdMobNative(adDescriptor, fallbackAdDescriptor)
        }
        return null
    }
}