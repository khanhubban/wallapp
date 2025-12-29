package wallapp.content.state.appicon

import wallapp.appicon.AppIcon
import wallapp.content.state.upgrade.SubscriptionPlan
import wallapp.resources.image.ImageRepository
import wallapp.resources.string.Strings
import wallapp.theme.ThemeManager

class AppIconSpecFactory(
    private val strings: Strings,
    private val imageRepository: ImageRepository,
    private val themeManager: ThemeManager,
) {

    fun getSpec(appIcon: AppIcon): AppIconSpec = when (appIcon) {
        AppIcon.PlusStandardDark -> adFreeDark
        AppIcon.PlusStandardLight -> adFreeLight
        AppIcon.Celebration -> celebration
        AppIcon.Citrus -> citrus
        AppIcon.Ivory -> ivory
        AppIcon.Midnight -> midnight
        AppIcon.PlusUnlimitedDark -> plusUnlimitedDark
        AppIcon.PlusUnlimitedLight -> plusUnlimitedLight
        AppIcon.Slate -> slate
    }

    private val adFreeDark: AppIconSpec
        get() = AppIconSpec(
            label = strings.appIconPlusStandardDark,
            icon = imageRepository.appLogoPreviewPlusStandardDark,
            appIconPreviewSelected = if (themeManager.theme.value.isLight) imageRepository.appIconPreviewSelectedGreyTint else imageRepository.appIconPreviewSelected,
            subscriptionPlan = SubscriptionPlan.PlusBasic,
        )
    private val adFreeLight: AppIconSpec
        get() = AppIconSpec(
            label = strings.appIconPlusStandardLight,
            icon = imageRepository.appLogoPreviewPlusStandardLight,
            appIconPreviewSelected = imageRepository.appIconPreviewSelected,
            subscriptionPlan = SubscriptionPlan.PlusBasic,
        )

    private val celebration: AppIconSpec
        get() = AppIconSpec(
            label = strings.appIconCelebration,
            icon = imageRepository.appLogoPreviewCelebration,
            appIconPreviewSelected = imageRepository.appIconPreviewSelected,
        )

    private val citrus: AppIconSpec
        get() = AppIconSpec(
            label = strings.appIconCitrus,
            icon = imageRepository.appLogoPreviewCitrus,
            appIconPreviewSelected = imageRepository.appIconPreviewSelected,
        )

    private val ivory: AppIconSpec
        get() = AppIconSpec(
            label = strings.appIconIvory,
            icon = imageRepository.appLogoPreviewIvory,
            appIconPreviewSelected = if (themeManager.theme.value.isDark) imageRepository.appIconPreviewSelectedGreyTint else imageRepository.appIconPreviewSelected,
            appIconPreviewUnselected = if (themeManager.theme.value.isLight) imageRepository.appIconPreviewUnselected else null,
        )

    private val midnight: AppIconSpec
        get() = AppIconSpec(
            label = strings.appIconMidnight,
            icon = imageRepository.appLogoPreviewMidnight,
            appIconPreviewSelected = if (themeManager.theme.value.isLight) imageRepository.appIconPreviewSelectedGreyTint else imageRepository.appIconPreviewSelected,
        )

    private val plusUnlimitedDark: AppIconSpec
        get() = AppIconSpec(
            label = strings.appIconPlusUnlimitedDark,
            icon = imageRepository.appLogoPreviewPlusUnlimitedDark,
            appIconPreviewSelected = if (themeManager.theme.value.isLight) imageRepository.appIconPreviewSelectedGreyTint else imageRepository.appIconPreviewSelected,
            subscriptionPlan = SubscriptionPlan.PlusUnlimited,
        )

    private val plusUnlimitedLight: AppIconSpec
        get() = AppIconSpec(
            label = strings.appIconPlusUnlimitedLight,
            icon = imageRepository.appLogoPreviewPlusUnlimitedLight,
            appIconPreviewSelected = imageRepository.appIconPreviewSelected,
            subscriptionPlan = SubscriptionPlan.PlusUnlimited,
        )

    private val slate: AppIconSpec
        get() = AppIconSpec(
            label = strings.appIconSlate,
            icon = imageRepository.appLogoPreviewSlate,
            appIconPreviewSelected = if (themeManager.theme.value.isLight) imageRepository.appIconPreviewSelectedGreyTint else imageRepository.appIconPreviewSelected,
            appIconPreviewUnselected = if (themeManager.theme.value.isDark) imageRepository.appIconPreviewUnselected else null,
        )
}