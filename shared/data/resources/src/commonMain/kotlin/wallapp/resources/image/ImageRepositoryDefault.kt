package wallapp.resources.image

import wallapp.graphics.Color
import wallapp.graphics.Colors
import wallapp.image.Image
import wallapp.image.ImageOptions
import wallapp.resources.AnimatedImages
import wallapp.resources.Icon
import wallapp.resources.LocalImages
import wallapp.resources.string.Strings
import wallapp.system.platform.PlatformFeature
import wallapp.theme.ColorToken

class ImageRepositoryDefault(
    private val strings: Strings,
) : ImageRepository {

    private val animatedImagesSupported: Boolean
        get() = PlatformFeature.AnimatedImagesSupported
    private val videoPlaybackSupported: Boolean
        get() = PlatformFeature.VideoPlaybackSupported
    private val videoPlaybackBundledSupported: Boolean
        get() = PlatformFeature.VideoPlaybackBundledSupported

    private fun Image(
        model: Any,
        contentDescription: String? = null,
        tintColorToken: ColorToken? = null,
    ): Image = Image.from(
        model = model,
        contentDescription = contentDescription,
        imageOptions = ImageOptions {
            this.tintColorToken = tintColorToken
        },
    )

    private fun ImageOnBackground(
        model: Any,
        contentDescription: String? = null,
    ): Image = Image(model, contentDescription, tintColorToken = ColorToken.ThemeOnBackground)

    override val acceptInset: Image by lazy { ImageOnBackground(LocalImages.AcceptInset, contentDescription = null) }
    override val accountSelected: Image by lazy { ImageOnBackground(LocalImages.AccountSelected) }
    override val accountUnselected: Image by lazy { ImageOnBackground(LocalImages.AccountUnselected) }
    override val adFree: Image by lazy { ImageOnBackground(LocalImages.AdFree, contentDescription = strings.adFree) }
    override val aiEnhanced: Image by lazy { ImageOnBackground(LocalImages.AiEnhanced, contentDescription = null) }
    override val aiEnhancedCircleOutline: Image by lazy { ImageOnBackground(LocalImages.AiEnhancedCircleOutline, contentDescription = null) }
    override val appIconPreviewLocked: Image by lazy { Image.from(LocalImages.AppIconPreviewLocked, contentDescription = strings.locked) }
    override val appIconPreviewSelected: Image by lazy { ImageOnBackground(LocalImages.AppIconPreviewSelected, contentDescription = strings.currentAppIcon) }
    override val appIconPreviewSelectedGreyTint: Image by lazy { Image(LocalImages.AppIconPreviewSelected, contentDescription = strings.currentAppIcon, tintColorToken = ColorToken.Custom(Colors.GrayAlt)) }
    override val appIconPreviewUnselected: Image by lazy { ImageOnBackground(LocalImages.AppIconPreviewUnselected, contentDescription = strings.appName) }
    override val appLogoPreviewCelebration: Image by lazy { Image.from(LocalImages.AppIconPreviewCelebration, contentDescription = strings.appIconCelebration) }
    override val appLogoPreviewCitrus: Image by lazy { Image.from(LocalImages.AppIconPreviewCitrus, contentDescription = strings.appIconCitrus) }
    override val appLogoPreviewIvory: Image by lazy { Image.from(LocalImages.AppIconPreviewIvory, contentDescription = strings.appIconIvory) }
    override val appLogoPreviewMidnight: Image by lazy { Image.from(LocalImages.AppIconPreviewMidnight, contentDescription = strings.appIconMidnight) }
    override val appLogoPreviewPlusStandardDark: Image by lazy { Image.from(LocalImages.AppIconPreviewPlusStandardDark, contentDescription = strings.appIconPlusStandardDark) }
    override val appLogoPreviewPlusStandardLight: Image by lazy { Image.from(LocalImages.AppIconPreviewPlusStandardLight, contentDescription = strings.appIconPlusStandardLight) }
    override val appLogoPreviewPlusUnlimitedDark: Image by lazy { Image.from(LocalImages.AppIconPreviewPlusUnlimitedDark, contentDescription = strings.appIconPlusUnlimitedDark) }
    override val appLogoPreviewPlusUnlimitedLight: Image by lazy { Image.from(LocalImages.AppIconPreviewPlusUnlimitedLight, contentDescription = strings.appIconPlusUnlimitedLight)}
    override val appLogoPreviewSlate: Image by lazy { Image.from(LocalImages.AppIconPreviewSlate, contentDescription = strings.appIconSlate) }
    override val brokenImage: Image by lazy { ImageOnBackground(LocalImages.BrokenImage, contentDescription = null) }
    override val celebration: Image by lazy { Image.from(AnimatedImages.Celebration, contentDescription = null) }
    override val checkCircle: Image by lazy { ImageOnBackground(Icon.CheckCircle, contentDescription = null) }
    override val checkGray: Image get() = followingProfile
    override val checkOrange: Image by lazy { Image.from(LocalImages.CheckOrange, contentDescription = null) }
    override val checkYellow: Image by lazy { Image.from(LocalImages.CheckYellow, contentDescription = null) }
    override val close: Image by lazy { ImageOnBackground(LocalImages.Close, contentDescription = strings.close) }
    override val closeInset: Image by lazy { ImageOnBackground(LocalImages.CloseInset, contentDescription = strings.close) }
    override val collection: Image by lazy { Image.from(LocalImages.Collection, strings.collection) }
    override val collectionStackShadowBottom: Image by lazy { Image.from(LocalImages.ShadowStackBottom, contentDescription = null) }
    override val collectionStackShadowMiddle: Image by lazy { Image.from(LocalImages.ShadowStackMiddle, contentDescription = null) }
    override val collectionsLarge: Image by lazy { Image.from(AnimatedImages.CollectionsLarge, contentDescription = strings.noPurchasesFoundTitle) }
    override val contentFooterOverlay: Image by lazy { Image.from(LocalImages.ContentFooterOverlay, contentDescription = null) }
    override val copyright: Image by lazy { ImageOnBackground(LocalImages.Copyright, contentDescription = null) }
    override val crop: Image by lazy { ImageOnBackground(LocalImages.Crop, contentDescription = null) }
    override val crossGray: Image by lazy { Image.from(LocalImages.CrossGray, contentDescription = null) }
    override val download: Image by lazy { ImageOnBackground(LocalImages.Download) }
    override val edit: Image by lazy { Image.from(LocalImages.Edit, strings.edit) }
    override val exploreSelected: Image by lazy { ImageOnBackground(LocalImages.ExploreSelected) }
    override val exploreUnselected: Image by lazy { ImageOnBackground(LocalImages.ExploreUnselected) }
    override val favoriteOff: Image by lazy { ImageOnBackground(LocalImages.FavoriteOff, contentDescription = strings.notAFavorite) }
    override val favoriteOffAnimated: Image by lazy { Image.from(AnimatedImages.FavoriteOff, contentDescription = strings.notAFavorite) }
    override val favoriteOn: Image by lazy { Image(LocalImages.FavoriteOn, tintColorToken = ColorToken.ThemeSecondary, contentDescription = strings.favoriteSet) }
    override val favoriteOnAnimated: Image by lazy { Image.from(AnimatedImages.FavoriteOn, contentDescription = strings.favoriteSet) }
    override val filter: Image by lazy { ImageOnBackground(LocalImages.Filter, contentDescription = strings.filter) }
    override val followOff: Image by lazy { Image.from(AnimatedImages.FollowOff, contentDescription = strings.unfollow) }
    override val followOn: Image by lazy { Image.from(AnimatedImages.FollowOn, contentDescription = strings.follow) }
    override val followProfile: Image by lazy { Image.from(LocalImages.Add, strings.follow) }
    override val followingProfile: Image by lazy { Image.from(LocalImages.Following, strings.following) }
    override val heartLarge: Image by lazy { Image.from(AnimatedImages.HeartLarge, contentDescription = strings.noFavoritesFoundTitle) }
    override val homeFilled: Image by lazy { ImageOnBackground(LocalImages.HomeSelected) }
    override val homeOutlined: Image by lazy { ImageOnBackground(LocalImages.HomeUnselected) }
    override val info: Image by lazy { ImageOnBackground(LocalImages.Info, contentDescription = null) }
    override val loading: Image by lazy { Image.from(AnimatedImages.Loading, contentDescription = strings.loading) }
    override val networkError: Image by lazy { ImageOnBackground(LocalImages.WifiOff, strings.networkError) }
    override val onboardingCheck: Image by lazy { Image.from(LocalImages.OnboardingCheck, contentDescription = null) }
    override val onboardingSelection: Image by lazy{ Image.from(AnimatedImages.OnboardingSelection, contentDescription = strings.homeOnboardingTitle) }
    override val paywallPlusAdFree: Image by lazy { Image.from(Color(0xfff7ac40), contentDescription = null) }
    override val paywallPlus: Image by lazy { Image.from(Color(0xffed2f10), contentDescription = null) }
    override val photosAppIcon: Image? by lazy { if (PlatformFeature.IsIos) { Image.from(LocalImages.PhotosAppIconApple, contentDescription = strings.photosApp) } else if (PlatformFeature.IsAndroid) { Image.from(LocalImages.PhotosAppIconGoogle, contentDescription = strings.photosApp) } else { null } }
    override val platformAppleLogoSquareBlack: Image by lazy { Image.from(LocalImages.PlatformAppleLogoSquareBlack, contentDescription = strings.apple) }
    override val platformAppleLogoSquareWhite: Image by lazy { Image.from(LocalImages.PlatformAppleLogoSquareWhite, contentDescription = strings.apple) }
    override val platformGoogleGSmall: Image by lazy { Image.from(LocalImages.PlatformGoogleGSmall, contentDescription = strings.google) }
    override val plusCircleOutline: Image by lazy { ImageOnBackground(LocalImages.PlusIconCircleOutline, contentDescription = strings.plus) }
    override val plusHero: Image by lazy { Image.from(LocalImages.PlusHero, contentDescription = null) }
    override val plusHighlightBackground: Image by lazy { Image.from(LocalImages.PlusHighlightBackground, contentDescription = null) }
    override val plusIndicator: Image by lazy { Image.from(LocalImages.PlusIcon, contentDescription = strings.plus) }
    override val plusIndicatorOnBackground: Image by lazy { Image.from(LocalImages.PlusIconOnBackground, contentDescription = strings.plus) }
    override val profile: Image by lazy { Image.from(LocalImages.Profile, strings.profile) }
    override val profileCuratorShadow: Image by lazy { Image.from(LocalImages.ProfileCuratorShadow, contentDescription = null) }
    override val profileShadow: Image by lazy { Image.from(LocalImages.ProfileShadow, contentDescription = null) }
    override val rewardAd: Image by lazy { Image.from(LocalImages.RewardAd, contentDescription = null) }
    override val sdCircleOutline: Image by lazy { ImageOnBackground(LocalImages.SdCircleOutline, contentDescription = null) }
    override val search: Image by lazy { ImageOnBackground(Icon.Search, contentDescription = strings.search) }
    override val shareInset: Image by lazy { ImageOnBackground(LocalImages.ShareInset, contentDescription = strings.share) }
    override val socialInstagram: Image by lazy { Image.from(LocalImages.SocialInstagram, contentDescription = strings.instagram) }
    override val socialTwitter: Image by lazy { Image.from(LocalImages.SocialX, contentDescription = strings.x) }
    override val splashScreen: Image by lazy { Image.from(Color.DarkGray, contentDescription = null) }
    override val statusBarShadow: Image by lazy { Image.from(LocalImages.WaterfallGradientBlack, contentDescription = null) }
    override val unselected: Image by lazy { Image.from(LocalImages.Unselected, contentDescription = null) }
    override val wallpaperGet: Image by lazy { ImageOnBackground(LocalImages.WallpaperGet, contentDescription = strings.get) }
    override val wallpaperSet: Image by lazy { ImageOnBackground(LocalImages.WallpaperSet, contentDescription = strings.set) }
    override val warning: Image by lazy { ImageOnBackground(LocalImages.Warning, contentDescription = strings.warning) }
    override val waterfallGradientBlack: Image by lazy { Image.from(LocalImages.WaterfallGradientBlack, contentDescription = null) }
    override val waterfallGradientBlackVertical: Image by lazy { Image.from(LocalImages.WaterfallGradientBlackVertical, contentDescription = null) }
    override val waterfallGradientWhite: Image by lazy { Image.from(LocalImages.WaterfallGradientWhite, contentDescription = null) }
    override val waterfallGradientWhiteVertical: Image by lazy { Image.from(LocalImages.WaterfallGradientWhiteVertical, contentDescription = null) }
}