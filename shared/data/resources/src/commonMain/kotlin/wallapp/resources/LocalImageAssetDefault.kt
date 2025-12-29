package wallapp.resources

import org.jetbrains.compose.resources.DrawableResource
import wallapp.resource.LocalImageAsset
import wallapp.resource.Resource

sealed class LocalImageAssetDefault(val fileName: String) : LocalImageAsset {
    override fun toResource(): Resource {
        return this.toResourceNative()
    }

    data object AcceptInset: LocalImageAssetDefault("ic_accept_inset")
    data object AccountSelected: LocalImageAssetDefault("ic_account_selected")
    data object AccountUnselected: LocalImageAssetDefault("ic_account_unselected")
    data object AdFree: LocalImageAssetDefault("ic_ad_free")
    data object Add: LocalImageAssetDefault("ic_add")
    data object AiEnhanced: LocalImageAssetDefault("ic_ai_enhanced")
    data object AiEnhancedCircleOutline : LocalImageAssetDefault("ic_ai_enhanced_circle_outline")
    data object AppIconPreviewCelebration: LocalImageAssetDefault("app_icon_preview_celebration")
    data object AppIconPreviewCitrus : LocalImageAssetDefault("app_icon_preview_citrus")
    data object AppIconPreviewIvory: LocalImageAssetDefault("app_icon_preview_ivory")
    data object AppIconPreviewLocked: LocalImageAssetDefault("app_icon_preview_locked")
    data object AppIconPreviewMidnight : LocalImageAssetDefault("app_icon_preview_midnight")
    data object AppIconPreviewPlusStandardDark: LocalImageAssetDefault("app_icon_preview_plus_standard_dark")
    data object AppIconPreviewPlusStandardLight: LocalImageAssetDefault("app_icon_preview_plus_standard_light")
    data object AppIconPreviewPlusUnlimitedDark: LocalImageAssetDefault("app_icon_preview_plus_unlimited_dark")
    data object AppIconPreviewPlusUnlimitedLight: LocalImageAssetDefault("app_icon_preview_plus_unlimited_light")
    data object AppIconPreviewSelected: LocalImageAssetDefault("app_icon_preview_selected")
    data object AppIconPreviewSlate: LocalImageAssetDefault("app_icon_preview_slate")
    data object AppIconPreviewUnselected: LocalImageAssetDefault("app_icon_preview_unselected")
    data object BrokenImage: LocalImageAssetDefault("ic_broken_image")
    data object CheckOrange: LocalImageAssetDefault("ic_check_orange")
    data object CheckYellow: LocalImageAssetDefault("ic_check_yellow")
    data object Close: LocalImageAssetDefault("ic_close_cross")
    data object CloseInset: LocalImageAssetDefault("ic_close_inset")
    data object Collection: LocalImageAssetDefault("ic_collection")
    data object ContentFooterOverlay: LocalImageAssetDefault("shadow_text_bar")
    data object Copyright: LocalImageAssetDefault("ic_copyright")
    data object Crop: LocalImageAssetDefault("ic_crop")
    data object CrossGray: LocalImageAssetDefault("ic_cross_gray")
    data object Download: LocalImageAssetDefault("ic_download")
    data object Edit: LocalImageAssetDefault("ic_edit")
    data object ExploreSelected: LocalImageAssetDefault("ic_explore_selected")
    data object ExploreUnselected: LocalImageAssetDefault("ic_explore_unselected")
    data object FavoriteOff: LocalImageAssetDefault("ic_favorite_off")
    data object FavoriteOn: LocalImageAssetDefault("ic_favorite_on")
    data object Filter: LocalImageAssetDefault("ic_filter")
    data object Following: LocalImageAssetDefault("ic_following")
    data object HomeSelected: LocalImageAssetDefault("ic_home_selected")
    data object HomeUnselected: LocalImageAssetDefault("ic_home_unselected")
    data object Info: LocalImageAssetDefault("ic_info")
    data object OnboardingCheck: LocalImageAssetDefault("ic_onboarding_check")
    data object PlusIcon: LocalImageAssetDefault("ic_plus")
    data object PlusIconCircleOutline: LocalImageAssetDefault("ic_plus_circle_outline")
    data object PlusIconOnBackground: LocalImageAssetDefault("ic_plus_on_background")
    data object PhotosAppIconApple: LocalImageAssetDefault("photos_app_icon_apple")
    data object PhotosAppIconGoogle: LocalImageAssetDefault("photos_app_icon_google")
    data object PlatformAppleLogoSquareBlack: LocalImageAssetDefault("apple_logo_square_black")
    data object PlatformAppleLogoSquareWhite: LocalImageAssetDefault("apple_logo_square_white")
    data object PlatformGoogleGSmall: LocalImageAssetDefault("google_g_256")
    data object PlusHero: LocalImageAssetDefault("plus_hero.png")
    data object PlusHighlightBackground: LocalImageAssetDefault("plus_highlight_background.png")
    data object Profile: LocalImageAssetDefault("ic_profile")
    data object ProfileCuratorShadow: LocalImageAssetDefault("ic_profile_curator_shadow")
    data object ProfileShadow: LocalImageAssetDefault("ic_profile_shadow")
    data object RewardAd: LocalImageAssetDefault("ic_reward_ad")
    data object SdCircleOutline: LocalImageAssetDefault("ic_1080p_circle_outline")
    data object ShadowStackBottom: LocalImageAssetDefault("shadow_stack_bottom")
    data object ShadowStackMiddle: LocalImageAssetDefault("shadow_stack_middle")
    data object ShadowTextBar: LocalImageAssetDefault("shadow_text_bar")
    data object ShareInset: LocalImageAssetDefault("ic_share_inset")
    data object SocialInstagram: LocalImageAssetDefault("ic_social_instagram")
    data object SocialX: LocalImageAssetDefault("ic_social_x")
    data object Unselected: LocalImageAssetDefault("ic_unselected")
    data object WallpaperGet: LocalImageAssetDefault("ic_wallpaper_get")
    data object WallpaperSet: LocalImageAssetDefault("ic_wallpaper_set")
    data object Warning: LocalImageAssetDefault("ic_warning")
    data object WaterfallGradientBlack: LocalImageAssetDefault("waterfall_gradient_black")
    data object WaterfallGradientBlackVertical: LocalImageAssetDefault("waterfall_gradient_black_vertical")
    data object WaterfallGradientWhite: LocalImageAssetDefault("waterfall_gradient_white")
    data object WaterfallGradientWhiteVertical: LocalImageAssetDefault("waterfall_gradient_white_vertical")
    data object WifiOff: LocalImageAssetDefault("ic_wifi_off")
}

fun LocalImageAssetDefault.toDrawableResource(): DrawableResource {
    return when (this) {
        LocalImageAssetDefault.AcceptInset -> Res.drawable.ic_accept_inset
        LocalImageAssetDefault.AccountSelected -> Res.drawable.ic_account_selected
        LocalImageAssetDefault.AccountUnselected -> Res.drawable.ic_account_unselected
        LocalImageAssetDefault.AdFree -> Res.drawable.ic_ad_free
        LocalImageAssetDefault.Add -> Res.drawable.ic_add
        LocalImageAssetDefault.AiEnhanced -> Res.drawable.ic_ai_enhanced
        LocalImageAssetDefault.AiEnhancedCircleOutline -> Res.drawable.ic_ai_enhanced_circle_outline
        LocalImageAssetDefault.AppIconPreviewCelebration -> Res.drawable.app_icon_preview_celebration
        LocalImageAssetDefault.AppIconPreviewCitrus -> Res.drawable.app_icon_preview_citrus
        LocalImageAssetDefault.AppIconPreviewIvory -> Res.drawable.app_icon_preview_ivory
        LocalImageAssetDefault.AppIconPreviewLocked -> Res.drawable.app_icon_preview_locked
        LocalImageAssetDefault.AppIconPreviewMidnight -> Res.drawable.app_icon_preview_midnight
        LocalImageAssetDefault.AppIconPreviewPlusStandardDark -> Res.drawable.app_icon_preview_plus_standard_dark
        LocalImageAssetDefault.AppIconPreviewPlusStandardLight -> Res.drawable.app_icon_preview_plus_standard_light
        LocalImageAssetDefault.AppIconPreviewPlusUnlimitedDark -> Res.drawable.app_icon_preview_plus_unlimited_dark
        LocalImageAssetDefault.AppIconPreviewPlusUnlimitedLight -> Res.drawable.app_icon_preview_plus_unlimited_light
        LocalImageAssetDefault.AppIconPreviewSelected -> Res.drawable.app_icon_preview_selected
        LocalImageAssetDefault.AppIconPreviewSlate -> Res.drawable.app_icon_preview_slate
        LocalImageAssetDefault.AppIconPreviewUnselected -> Res.drawable.app_icon_preview_unselected
        LocalImageAssetDefault.BrokenImage -> Res.drawable.ic_broken_image
        LocalImageAssetDefault.CheckOrange -> Res.drawable.ic_check_orange
        LocalImageAssetDefault.CheckYellow -> Res.drawable.ic_check_yellow
        LocalImageAssetDefault.Close -> Res.drawable.ic_close_cross
        LocalImageAssetDefault.CloseInset -> Res.drawable.ic_close_inset
        LocalImageAssetDefault.Collection -> Res.drawable.ic_collection
        LocalImageAssetDefault.ContentFooterOverlay -> Res.drawable.shadow_text_bar
        LocalImageAssetDefault.Copyright -> Res.drawable.ic_copyright
        LocalImageAssetDefault.Crop -> Res.drawable.ic_crop
        LocalImageAssetDefault.CrossGray -> Res.drawable.ic_cross_gray
        LocalImageAssetDefault.Download -> Res.drawable.ic_download
        LocalImageAssetDefault.Edit -> Res.drawable.ic_edit
        LocalImageAssetDefault.ExploreSelected -> Res.drawable.ic_explore_selected
        LocalImageAssetDefault.ExploreUnselected -> Res.drawable.ic_explore_unselected
        LocalImageAssetDefault.FavoriteOff -> Res.drawable.ic_favorite_off
        LocalImageAssetDefault.FavoriteOn -> Res.drawable.ic_favorite_on
        LocalImageAssetDefault.Filter -> Res.drawable.ic_filter
        LocalImageAssetDefault.Following -> Res.drawable.ic_following
        LocalImageAssetDefault.HomeSelected -> Res.drawable.ic_home_selected
        LocalImageAssetDefault.HomeUnselected -> Res.drawable.ic_home_unselected
        LocalImageAssetDefault.Info -> Res.drawable.ic_info
        LocalImageAssetDefault.OnboardingCheck -> Res.drawable.ic_onboarding_check
        LocalImageAssetDefault.PhotosAppIconApple -> Res.drawable.photos_app_icon_apple
        LocalImageAssetDefault.PhotosAppIconGoogle -> Res.drawable.photos_app_icon_google
        LocalImageAssetDefault.PlatformAppleLogoSquareBlack -> Res.drawable.apple_logo_square_black
        LocalImageAssetDefault.PlatformAppleLogoSquareWhite -> Res.drawable.apple_logo_square_white
        LocalImageAssetDefault.PlatformGoogleGSmall -> Res.drawable.google_g_256
        LocalImageAssetDefault.PlusHero -> Res.drawable.plus_hero
        LocalImageAssetDefault.PlusHighlightBackground -> Res.drawable.plus_highlight_background
        LocalImageAssetDefault.PlusIcon -> Res.drawable.ic_plus
        LocalImageAssetDefault.PlusIconCircleOutline -> Res.drawable.ic_plus_circle_outline
        LocalImageAssetDefault.PlusIconOnBackground -> Res.drawable.ic_plus_on_background
        LocalImageAssetDefault.Profile -> Res.drawable.ic_profile
        LocalImageAssetDefault.ProfileCuratorShadow -> Res.drawable.ic_profile_curator_shadow
        LocalImageAssetDefault.ProfileShadow -> Res.drawable.ic_profile_shadow
        LocalImageAssetDefault.RewardAd -> Res.drawable.ic_reward_ad
        LocalImageAssetDefault.SdCircleOutline -> Res.drawable.ic_1080p_circle_outline
        LocalImageAssetDefault.ShadowStackBottom -> Res.drawable.shadow_stack_bottom
        LocalImageAssetDefault.ShadowStackMiddle -> Res.drawable.shadow_stack_middle
        LocalImageAssetDefault.ShadowTextBar -> Res.drawable.shadow_text_bar
        LocalImageAssetDefault.ShareInset -> Res.drawable.ic_share_inset
        LocalImageAssetDefault.SocialInstagram -> Res.drawable.ic_social_instagram
        LocalImageAssetDefault.SocialX -> Res.drawable.ic_social_x
        LocalImageAssetDefault.Unselected -> Res.drawable.ic_unselected
        LocalImageAssetDefault.WallpaperGet -> Res.drawable.ic_wallpaper_get
        LocalImageAssetDefault.WallpaperSet -> Res.drawable.ic_wallpaper_set
        LocalImageAssetDefault.Warning -> Res.drawable.ic_warning
        LocalImageAssetDefault.WaterfallGradientBlack -> Res.drawable.waterfall_gradient_black
        LocalImageAssetDefault.WaterfallGradientBlackVertical -> Res.drawable.waterfall_gradient_black_vertical
        LocalImageAssetDefault.WaterfallGradientWhite -> Res.drawable.waterfall_gradient_white
        LocalImageAssetDefault.WaterfallGradientWhiteVertical -> Res.drawable.waterfall_gradient_white_vertical
        LocalImageAssetDefault.WifiOff -> Res.drawable.ic_wifi_off
    }
}

expect fun LocalImageAssetDefault.toResourceNative(): Resource
