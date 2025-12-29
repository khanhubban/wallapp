package wallapp.ads.inline.native

import android.content.Context
import android.view.View
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.MediaContent
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import kotlinx.coroutines.CoroutineScope
import wallapp.ads.image.AdImage
import wallapp.ads.image.AdImageLoader
import wallapp.ads.inline.InlineAdConfigAndroid
import wallapp.ads.inline.InlineAdControllerAndroidAdMob
import wallapp.ads.inline.InlineAdHandleAndroid.OnAdLoadedListener
import wallapp.ads.inline.InlineAdViewAndroid
import wallapp.font.TextStyle
import wallapp.image.Image
import wallapp.log.Log
import wallapp.pixel.button.Button
import wallapp.pixel.button.ButtonAppearance
import wallapp.pixel.button.ButtonViewState
import wallapp.pixel.menu.MenuItem
import wallapp.pixel.render.LocalRenderCompat
import wallapp.pixel.render.Render
import wallapp.pixel.shape.ShapeSize
import wallapp.pixel.shape.ShapeSpec
import wallapp.pixel.shape.ShapeStyle
import wallapp.pixel.text.Text.Companion.presetText
import wallapp.pixel.view.ViewEventHandler
import wallapp.prefs.PreferenceStorage
import wallapp.theme.ColorToken
import wallapp.ui.Render


class InlineAdControllerAndroidAdMobNative(
    adConfig: InlineAdConfigAndroid,
    imageLoader: AdImageLoader,
    coroutineScopeMain: CoroutineScope,
    preferenceStorage: PreferenceStorage,
) : InlineAdControllerAndroidAdMob(adConfig, imageLoader, coroutineScopeMain, preferenceStorage) {

    override fun setAd(nativeAd: Any?) {
        adContent = InlineAdContentProviderAndroidAdMobNative(nativeAd as NativeAd)
    }

    public override fun inflateAdView(context: Context): InlineAdViewAndroid {
        return InlineAdViewAdMobNative((adConfig.inflateAd(context, null) as NativeAdView))
    }

    override fun configureMediaView(inlineAdView: InlineAdViewAndroid, mediaView: View?) {
        val adContent = adContent ?: return
        val mediaContent = adContent.mediaContent as MediaContent

        inlineAdView.setMediaView(mediaView)
        (mediaView as MediaView).setMediaContent(mediaContent)
    }

    override fun configureComposeView(
        inlineAdView: InlineAdViewAndroid,
        composeView: ComposeView?,
        title: String?
    ) {
        composeView?.let {
            if (title?.isNotEmpty() == true) {
                it.setContent {
                    val render: Render = remember { Render() }
                    Box(
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        Button(
                            render = render,
                            viewState = ButtonViewState.Preset.copy(
                                menuItem = MenuItem.MenuItemLabel(
                                    title.presetText,
                                    onClick = null,
                                    minTextStyle = TextStyle.Body,
                                ),
                                shapeSpec = ShapeSpec.Preset.copy(
                                    shapeStyle = ShapeStyle.RoundedCorners,
                                    shapeSize = ShapeSize.Small,
                                ),
                                buttonAppearance = ButtonAppearance.Highlight,
                                containerColorToken = ColorToken.ThemeSecondary,
                                eventHandler = ViewEventHandler.createOnClick {
                                    inlineAdView.callToActionView?.performClick()
                                },
                            ),
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }

        }
    }

    override fun configureComposeView(
        inlineAdView: InlineAdViewAndroid,
        composeView: ComposeView?,
        image: AdImage?,
    ) {
        val imageUrl = image?.let { getImageUrl(it) } ?: return
        composeView?.setContent {
            val render: Render = LocalRenderCompat.current
            Box(
                modifier = Modifier.fillMaxSize(),
            ) {
                Image(render = render, image = imageUrl)
            }
        }
    }

    private fun getImageUrl(image: AdImage): Image? {

        val uri = image.uri
        if (uri != null) {
            return Image.from(
                model = uri.toString(),
                contentDescription = null,
            )
        }

        val drawable = image.drawable
        if (drawable != null) {
            return Image.from(
                model = drawable,
                contentDescription = null,
            )
        }

        val resourceId = image.resourceId
        if (resourceId != null) {
            return Image.from(
                model = resourceId,
                contentDescription = null,
            )
        }

        return null
    }

    override fun build(
        builder: AdLoader.Builder,
        onLoadedListener: OnAdLoadedListener
    ): AdLoader.Builder {
        builder.forNativeAd { nativeAd: NativeAd? ->
            Log.d("[AdDebug] created AdMob native ad: %s", nativeAd)
            onLoadedListener.onAdLoaded(nativeAd)
        }
        return builder
    }
}