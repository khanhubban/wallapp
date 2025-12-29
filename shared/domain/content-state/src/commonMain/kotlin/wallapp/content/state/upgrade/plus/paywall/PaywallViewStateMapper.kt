package wallapp.content.state.upgrade.plus.paywall

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import wallapp.content.state.upgrade.PaywallViewEvent
import wallapp.content.state.upgrade.PaywallViewEventSink
import wallapp.content.state.upgrade.PlusPlan
import wallapp.content.state.upgrade.SubscriptionPlan
import wallapp.content.state.upgrade.plus.plans.SubscriptionPlanViewState
import wallapp.content.state.upgrade.useOrangeHighlightColor
import wallapp.data.purchase.Purchasable
import wallapp.graphics.Colors
import wallapp.image.ImageViewSpecFactory
import wallapp.license.state.LicenseStateRepository
import wallapp.pixel.button.ButtonAppearance
import wallapp.pixel.image.ImageViewState
import wallapp.pixel.menu.MenuItem
import wallapp.pixel.menu.MenuItemViewState
import wallapp.pixel.pager.LastPagerStateUpdateSink
import wallapp.pixel.tab.TabViewState
import wallapp.pixel.tab.TabsViewState
import wallapp.pixel.text.TextStyleBody
import wallapp.pixel.text.TextStyleCallToAction
import wallapp.pixel.text.TextStyleCaption
import wallapp.pixel.text.TextStyleDisplay
import wallapp.pixel.text.TextStyleSubheading
import wallapp.pixel.text.TextStyleSubheadingActive
import wallapp.pixel.view.View
import wallapp.pixel.view.ViewEventHandler
import wallapp.resources.Url
import wallapp.resources.image.ImageRepository
import wallapp.resources.string.StringRepository
import wallapp.system.platform.PlatformFeature
import wallapp.text.TextAlign
import wallapp.text.TextButtonLabel
import wallapp.theme.ColorToken
import wallapp.theme.ThemeManager
import wallapp.unit.Alignment
import wallapp.unit.Width
import wallapp.view.ViewEventFactory
import wallapp.view.ViewSpecArbitrator
import wallapp.view.ViewSpecFactory
import wallapp.view.menu.MenuItemFactory
import wallapp.view.shape.ShapeSpecFactory


class PaywallViewStateMapper(
    private val viewSpecFactory: ViewSpecFactory,
    private val viewEventFactory: ViewEventFactory,
    private val viewSpecArbitrator: ViewSpecArbitrator,
    private val imageViewSpecFactory: ImageViewSpecFactory,
    private val imageRepository: ImageRepository,
    private val menuItemFactory: MenuItemFactory,
    private val strings: StringRepository,
    private val shapeSpecFactory: ShapeSpecFactory,
    private val licenseStateRepository: LicenseStateRepository,
    private val themeManager: ThemeManager,
) {
    private val windowIsCompact: Boolean
        get() = viewSpecArbitrator.windowIsCompact

    fun createMenuItemViewState(
        menuItems: List<MenuItem>,
        itemPadding: Dp,
    ) = MenuItemViewState(
        menuItems = menuItems,
        itemPadding = itemPadding,
    )

    fun mapPaywallViewState(
        purchasablePlusMonthly: Purchasable.SubscriptionUnlimitedMonthly?,
        purchasablePlusAnnual: Purchasable.SubscriptionUnlimitedAnnual?,
        purchasableAdFreeMonthly: Purchasable.SubscriptionStandardMonthly?,
        subscriptionExpired: Boolean,
        initialSubscriptionPlan: SubscriptionPlan,
        actionButtonViewEventHandler: ViewEventHandler,
        selectedPlan: PlusPlan,
        paywallViewEventSink: PaywallViewEventSink,
        planSelectionLastPagerStateUpdateSink: LastPagerStateUpdateSink?,
        showAsModalSheet: Boolean,
        subscribedToCurrentSelectedSubscription: Boolean,
        currentlySubscribedToAny: Boolean,
        selectedSubscriptionPlan: SubscriptionPlan,
    ): PaywallViewState {
        val plusPlans = mapPlusPlans(
            purchasablePlusMonthly = purchasablePlusMonthly,
            purchasablePlusAnnual = purchasablePlusAnnual,
            eventSink = paywallViewEventSink,
            selectedPlan = selectedPlan
        ) ?: return PaywallViewState.Loading

        val adFreePlan = mapAdFreePlan(
            purchasableAdFreeMonthly = purchasableAdFreeMonthly
        )

        return if (planSelectionLastPagerStateUpdateSink != null && adFreePlan != null) {
            mapPaywallViewStateNativeTabs(
                initialSubscriptionPlan = initialSubscriptionPlan,
                subscriptionExpired = subscriptionExpired,
                actionButtonViewEventHandler = actionButtonViewEventHandler,
                plusPlans = plusPlans,
                planSelectionLastPagerStateUpdateSink = planSelectionLastPagerStateUpdateSink,
                showAsModalSheet = showAsModalSheet,
                adFreePlan = adFreePlan,
                paywallViewEventSink = paywallViewEventSink,
                subscribedToCurrentSelectedSubscription = subscribedToCurrentSelectedSubscription,
                currentlySubscribedToAny = currentlySubscribedToAny,
                selectedSubscriptionPlan = selectedSubscriptionPlan,
            )
        } else {
            PaywallViewState.Loading
        }
    }

    private fun mapPaywallViewStateNativeTabs(
        initialSubscriptionPlan: SubscriptionPlan,
        subscriptionExpired: Boolean,
        actionButtonViewEventHandler: ViewEventHandler,
        plusPlans: List<Pair<SubscriptionPlanViewState, Purchasable>>,
        adFreePlan: SubscriptionPlanViewState,
        planSelectionLastPagerStateUpdateSink: LastPagerStateUpdateSink,
        paywallViewEventSink: PaywallViewEventSink,
        showAsModalSheet: Boolean,
        subscribedToCurrentSelectedSubscription: Boolean,
        currentlySubscribedToAny: Boolean,
        selectedSubscriptionPlan: SubscriptionPlan,
    ): PaywallViewState.NativeTabs {

        val actionButton = if (subscribedToCurrentSelectedSubscription) {
            menuItemFactory.createActionButton(
                image = null,
                text = TextButtonLabel(strings.subscribed, colorToken = ColorToken.ThemePrimary),
                buttonAppearance = ButtonAppearance.Disabled,
                eventHandler = ViewEventHandler.createOnClick { /* no-op */ },
                contentColorToken = ColorToken.ThemePrimary,
            )
        } else {
            val textString = if (currentlySubscribedToAny && PlatformFeature.NativeManageSubscriptionSupported) {
                strings.changeSubscription
            } else {
                strings.`continue`
            }
            menuItemFactory.createActionButton(
                image = null,
                text = TextButtonLabel(textString),
                buttonAppearance = ButtonAppearance.Highlight,
                eventHandler = actionButtonViewEventHandler,
                contentColorToken = ColorToken.ThemeSecondary,
            )
        }

        val theme = themeManager.theme.value

        val subscriptionTitle = menuItemFactory.createLabel(
            TextStyleDisplay(
                when {
                    subscriptionExpired -> strings.subscriptionExpiredTitle
                    currentlySubscribedToAny -> strings.manageSubscription
                    else -> strings.upgradeToPlus
                },
                textAlign = TextAlign.Center,
                ignoreLargeSystemFontScaling = true,
            ),
            contentAlignment = Alignment.Center,
            width = Width.WidthFillMax(),
        )

        val close = menuItemFactory.createHorizontalGroup(
            listOf(
                menuItemFactory.createSpacer(width = viewSpecArbitrator.paddingDefault),
                menuItemFactory.createCircularCloseButton(),
            )
        )

        val pillTabsViewSpec = viewSpecFactory.tabsPillViewSpec(viewSpecArbitrator.pillTabsWidth)
        val topTabSpacing = (viewSpecArbitrator.paddingDefault * 2) + pillTabsViewSpec.tabHeight

        val plusPaywallPlanView = PaywallPlanViewState(
            subscriptionFeatures = createPaywallFeatures(subscriptionPlan = SubscriptionPlan.PlusUnlimited),
            plusPlans = plusPlans.map { it.first },
            topPadding = topTabSpacing,
        ).let {
            View(it, null)
        }

        val adFreePaywallPlanView = PaywallPlanViewState(
            subscriptionFeatures = createPaywallFeatures(subscriptionPlan = SubscriptionPlan.PlusBasic),
            plusPlans = listOf(adFreePlan),
            topPadding = topTabSpacing,
        ).let {
            View(it, null)
        }

        val tab0 = TabViewState(
            headerSelected = menuItemFactory.createLabel(text = TextStyleSubheadingActive(strings.standard, colorToken = ColorToken.Custom(Colors.Black))),
            headerUnselected = menuItemFactory.createLabel(text = TextStyleSubheading(strings.standard, colorToken = ColorToken.ThemeOnPrimary)),
            view = adFreePaywallPlanView,
            viewEvent = PaywallViewEvent.SubscriptionPlanViewEvent(SubscriptionPlan.PlusBasic),
        )
        val tab1 = TabViewState(
            headerSelected = menuItemFactory.createLabel(text = TextStyleSubheadingActive(strings.unlimited, colorToken = ColorToken.ThemeOnPrimary)),
            headerUnselected = menuItemFactory.createLabel(text = TextStyleSubheading(strings.unlimited, colorToken = ColorToken.ThemeOnPrimary)),
            view = plusPaywallPlanView,
            viewEvent = PaywallViewEvent.SubscriptionPlanViewEvent(SubscriptionPlan.PlusUnlimited),
        )

        val planTabs = TabsViewState.Pill(
            viewSpec = pillTabsViewSpec,
            tabs = listOf(tab0, tab1),
            initialIndex = 0,
            containerColorToken = ColorToken.ThemePrimary,
            indicatorColorToken = if (selectedSubscriptionPlan == SubscriptionPlan.PlusBasic) {
                ColorToken.Custom(Colors.AdFree)
            } else {
                ColorToken.Custom(Colors.Accent)
            },
        )

        val initialTabPage = if (initialSubscriptionPlan == SubscriptionPlan.PlusBasic) 0 else 1

        val imageViewState = ImageViewState(
            image = if (selectedSubscriptionPlan == SubscriptionPlan.PlusBasic) {
                imageRepository.paywallPlusAdFree
            } else {
                imageRepository.paywallPlus
            },
            viewSpec = imageViewSpecFactory.plusHeroImageViewSpec,
            imageSize = null,
        )

        return PaywallViewState.NativeTabs(
            theme = theme,
            close = close,
            featureImageViewState = imageViewState,
            subscriptionTitle = subscriptionTitle,
            initialTabPage = initialTabPage,
            planSelectionTabs = planTabs,
            planSelectionLastPagerStateUpdateSink = planSelectionLastPagerStateUpdateSink,
            actionButton = actionButton,
            footerItem = createFooterItem(),
            showAsModalSheet = showAsModalSheet,
            paywallViewEventSink = paywallViewEventSink,
        )
    }

    fun mapSubscriptionPlan(
        subscriptionPlan: SubscriptionPlan,
        isSelected: Boolean,
        title: String,
        price: String,
        highlight: String?,
        event: PaywallViewEvent.PlusPlanViewEvent,
        eventSink: PaywallViewEventSink,
    ): SubscriptionPlanViewState {
        return SubscriptionPlanViewState(
            theme = if (isSelected) { themeManager.oppositeTheme.value } else { themeManager.theme.value },
            title = createPlanTitleText(
                title = title,
                isSelected = isSelected,
                useOrangeHighlightColor = subscriptionPlan.useOrangeHighlightColor,
            ),
            highlight = highlight?.let {
                TextStyleSubheadingActive(
                    string = it,
                    colorToken = if (isSelected) ColorToken.ThemeOnPrimary else ColorToken.ThemeOnSurfaceVariant
                )
            },
            price = TextStyleBody(
                price,
                colorToken = if (isSelected) ColorToken.ThemeOnPrimary else ColorToken.ThemeOnBackground
            ),
            shapeSpec = shapeSpecFactory.buttonShapeSpecDefault,
            isSelected = isSelected,
            eventSink = eventSink,
            event = event,
        )
    }

    fun mapPlusPlans(
        purchasablePlusMonthly: Purchasable.SubscriptionUnlimitedMonthly?,
        purchasablePlusAnnual: Purchasable.SubscriptionUnlimitedAnnual?,
        eventSink: PaywallViewEventSink,
        selectedPlan: PlusPlan,
    ): List<Pair<SubscriptionPlanViewState, Purchasable>>? {
        if (purchasablePlusMonthly == null) {
            return null
        }

        val annualPriceLabel = purchasablePlusAnnual?.let {
            strings.plusUnlimitedForPricePerPeriod(
                purchasablePlusAnnual.pricePerYearLabel,
            )
        }
        val annualViewState = purchasablePlusAnnual?.let {
            mapSubscriptionPlan(
                subscriptionPlan = SubscriptionPlan.PlusUnlimited,
                isSelected = selectedPlan == PlusPlan.ANNUAL,
                title = strings.annual,
                highlight = purchasablePlusAnnual.annualDiscountLabel,
                price = annualPriceLabel!!,
                eventSink = eventSink,
                event = PaywallViewEvent.PlusPlanViewEvent(plusPlan = PlusPlan.ANNUAL),
            )
        }

        val monthlyPriceLabel = strings.plusUnlimitedForPricePerPeriod(
            purchasablePlusMonthly.pricePerMonthLabel,
        )
        val monthlyViewState = mapSubscriptionPlan(
            subscriptionPlan = SubscriptionPlan.PlusUnlimited,
            isSelected = selectedPlan == PlusPlan.MONTHLY,
            title = strings.month,
            highlight = null,
            price = monthlyPriceLabel,
            eventSink = eventSink,
            event = PaywallViewEvent.PlusPlanViewEvent(plusPlan = PlusPlan.MONTHLY),
        )

        return mutableListOf<Pair<SubscriptionPlanViewState, Purchasable>>().apply {
            add(monthlyViewState to purchasablePlusMonthly)
            if (annualViewState != null) {
                add(annualViewState to purchasablePlusAnnual)
            }
        }
    }

    private fun mapAdFreePlan(
        purchasableAdFreeMonthly: Purchasable.SubscriptionStandardMonthly?
    ): SubscriptionPlanViewState? {
        if (purchasableAdFreeMonthly == null) {
            return null
        }

        val monthlyPriceLabel = strings.plusStandardForPricePerPeriod(
            purchasableAdFreeMonthly.pricePerMonthLabel,
        )
        return mapSubscriptionPlan(
            subscriptionPlan = SubscriptionPlan.PlusBasic,
            isSelected = true,
            title = strings.month,
            highlight = null,
            price = monthlyPriceLabel,
            eventSink = { /* no-op */ },
            event = PaywallViewEvent.PlusPlanViewEvent(PlusPlan.MONTHLY),
        )
    }

    private fun createPaywallFeatures(
        subscriptionPlan: SubscriptionPlan,
    ): List<PaywallFeatureViewState> {
        return listOfNotNull(
            createPaywallFeature(subscriptionPlan, strings.paywallFeature1),
            createPaywallFeature(subscriptionPlan, strings.paywallFeature2),
            if (windowIsCompact) {
                null
            } else {
                createPaywallFeature(subscriptionPlan, strings.paywallFeature3)
            },
            createPaywallFeature(subscriptionPlan, strings.paywallFeature4, enabled = subscriptionPlan == SubscriptionPlan.PlusUnlimited),
            createPaywallFeature(subscriptionPlan, strings.paywallFeature5, enabled = subscriptionPlan == SubscriptionPlan.PlusUnlimited),
        )
    }

    private val showTermsOfUseUela: Boolean
        get() = PlatformFeature.IsIos
    private val termsLabel: String
        get() = if (showTermsOfUseUela) {
            strings.termsOfUseEula
        } else {
            strings.terms
        }
    private val termsUrl: String
        get() = if (showTermsOfUseUela) {
            Url.TermsOfUseEulaApple
        } else {
            Url.TermsOfService
        }

    private fun createFooterItem(): MenuItem.MenuItemGroupHorizontal {
        return menuItemFactory.createHorizontalGroup(
            items = listOf(
                menuItemFactory.createLabel(
                    TextStyleCaption(strings.restore),
                    onClick = ViewEventHandler.createOnClick { handleRestorePurchases() },
                ),
                menuItemFactory.createSpacer(viewSpecArbitrator.paddingSmall),
                menuItemFactory.createLabel(
                    TextStyleCaption(strings.dotCharacter),
                ),
                menuItemFactory.createSpacer(viewSpecArbitrator.paddingSmall),
                menuItemFactory.createLabel(
                    TextStyleCaption(termsLabel),
                    onClick = createUrlOnClick(termsUrl),
                ),
                menuItemFactory.createSpacer(viewSpecArbitrator.paddingSmall),
                menuItemFactory.createLabel(
                    TextStyleCaption(strings.dotCharacter),
                ),
                menuItemFactory.createSpacer(viewSpecArbitrator.paddingSmall),
                menuItemFactory.createLabel(
                    TextStyleCaption(strings.privacy),
                    onClick = createUrlOnClick(Url.PrivacyPolicy),
                ),
            ),
            height = viewSpecArbitrator.unlockWallpaperMenuItemHeight,
        )
    }

    private fun createUrlOnClick(url: String): ViewEventHandler =
        viewEventFactory.createNavigateToUrl(url)

    private fun handleRestorePurchases() {
        licenseStateRepository.checkLicenseState(forceUpdate = true)
    }

    private fun createPlanTitleText(
        title: String,
        isSelected: Boolean,
        useOrangeHighlightColor: Boolean,
    ): MenuItem.MenuItemGroupHorizontal {
        return menuItemFactory.createHorizontalGroup(
            items = listOf(
                menuItemFactory.createImage(
                    image = if (isSelected) {
                        if (useOrangeHighlightColor) {
                            imageRepository.checkOrange
                        } else {
                            imageRepository.checkYellow
                        }
                    } else {
                        imageRepository.unselected
                    },
                    size = viewSpecArbitrator.iconSize,
                ),
                menuItemFactory.createSpacer(width = viewSpecArbitrator.paddingSmall),
                menuItemFactory.createLabel(
                    TextStyleCallToAction(
                        title,
                        textAlign = TextAlign.Start,
                        colorToken = if (isSelected) ColorToken.ThemeOnPrimary else ColorToken.ThemeOnBackground,
                    )
                ),
            ),
            height = viewSpecArbitrator.unlockWallpaperMenuItemHeight,
        )
    }

    private fun createCrownWithText(title: String): MenuItem {
        val iconSize = viewSpecArbitrator.paywallFeatureIconSize
        val iconImage = menuItemFactory.createImage(
            imageRepository.plusIndicator,
            size = iconSize - 8.dp,
        )
        val icon = menuItemFactory.createCircularButton(
            iconImage,
            eventHandler = null,
            containerColor = ColorToken.ThemeSecondary,
            buttonSize = iconSize,
        )

        return menuItemFactory.createHorizontalGroup(
            items = listOf(
                icon,
                menuItemFactory.createSpacer(width = viewSpecArbitrator.paddingSmall),
                menuItemFactory.createLabel(
                    TextStyleBody(title, textAlign = TextAlign.Start),
                ),
            ),
            height = viewSpecArbitrator.paywallFeatureItemHeight,
        )
    }

    private fun createPaywallFeature(
        subscriptionPlan: SubscriptionPlan,
        title: String,
        enabled: Boolean = true,
    ): PaywallFeatureViewState {
        val iconSize = viewSpecArbitrator.paywallFeatureIconSize
        val iconImage = if (!enabled) {
            imageRepository.crossGray
        } else if (subscriptionPlan == SubscriptionPlan.PlusUnlimited) {
            imageRepository.checkOrange
        } else {
            imageRepository.checkYellow
        }

        val icon = menuItemFactory.createIcon(
            icon = iconImage,
            size = iconSize,
        )

        val colorToken = if (!enabled) {
            ColorToken.ThemeTertiary
        } else {
            null
        }

        return PaywallFeatureViewState(
            icon,
            TextStyleSubheading(
                title,
                textAlign = TextAlign.Start,
                maxLines = 2,
                colorToken = colorToken,
                ignoreLargeSystemFontScaling = true,
            ),
        )
    }

}