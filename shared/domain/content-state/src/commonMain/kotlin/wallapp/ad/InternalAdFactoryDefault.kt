package wallapp.ad

import wallapp.content.state.ad.AdViewSpec
import wallapp.content.state.ad.AdViewState
import wallapp.content.state.colors.ContentColorManager
import wallapp.content.state.upgrade.SubscriptionPlan
import wallapp.pixel.menu.MenuItem.MenuItemButton
import wallapp.pixel.text.TextStyleHeadline
import wallapp.pixel.text.TextStyleSubheadingActive
import wallapp.pixel.view.View
import wallapp.pixel.view.ViewEventHandler
import wallapp.pixel.view.ViewId
import wallapp.resources.image.ImageRepository
import wallapp.resources.string.Strings
import wallapp.text.TextAlign
import wallapp.theme.ColorToken
import wallapp.theme.ThemeManager
import wallapp.view.ViewEventFactory
import wallapp.view.ViewSpecFactory
import wallapp.view.menu.MenuItemFactory
import wallapp.view.shape.ShapeSpecFactory

class InternalAdFactoryDefault(
    private val viewSpecFactory: ViewSpecFactory,
    private val viewEventFactory: ViewEventFactory,
    private val shapeSpecFactory: ShapeSpecFactory,
    private val menuItemFactory: MenuItemFactory,
    private val strings: Strings,
    private val imageRepository: ImageRepository,
    private val contentColorManager: ContentColorManager,
    private val themeManager: ThemeManager,
) : InternalAdFactory {

    private fun createAdViewId(index: Int) = ViewId("ad~idx$index")

    private fun AdViewState.mapToView(fullWidth: Boolean) = View(
        viewState = this,
        viewSpec = if (fullWidth) {
            viewSpec
        } else {
            viewSpecFactory.feedContentPreviewViewSpecDefault
        },
    )

    private val presetAdViewSpec: AdViewSpec
        get() = viewSpecFactory.presetAdViewSpec

    private fun createCloseButton(
        viewEventHandler: ViewEventHandler,
        useLightTopControls: Boolean = themeManager.theme.value.isDark,
    ): MenuItemButton {
        val (controlButtonBackgroundColor, controlButtonOnBackgroundColor) = if (useLightTopControls) {
            contentColorManager.controlButtonBackgroundColorLight.value to
                    contentColorManager.controlButtonOnBackgroundColorLight.value
        } else {
            contentColorManager.controlButtonBackgroundColorDark.value to
                    contentColorManager.controlButtonOnBackgroundColorDark.value
        }

        return menuItemFactory.createCircularCloseButton(
            eventHandler = viewEventHandler,
            containerColor = ColorToken.Custom(controlButtonBackgroundColor),
            tintColor = ColorToken.Custom(controlButtonOnBackgroundColor),
        )
    }

    override fun createGetPlusAd(index: Int, fullWidth: Boolean): View {
        val viewEventHandler = viewEventFactory.createNavigateToPaywall(subscriptionPlan = SubscriptionPlan.PlusUnlimited)
        return AdViewState(
            viewId = createAdViewId(index),
            shapeSpec = shapeSpecFactory.defaultShapeSpec,
            viewSpec = presetAdViewSpec,
            image = imageRepository.plusHero,
            title = TextStyleHeadline(strings.plus, textAlign = TextAlign.Center),
            summary = TextStyleSubheadingActive(strings.getPlusAdSummary),
            heroButton = menuItemFactory.createPlusHeroButton(
                label = strings.getPlus,
                eventHandler = viewEventHandler,
            ),
            closeButton = createCloseButton(viewEventHandler),
            viewEventHandler = viewEventHandler,
        ).mapToView(fullWidth)
    }
}