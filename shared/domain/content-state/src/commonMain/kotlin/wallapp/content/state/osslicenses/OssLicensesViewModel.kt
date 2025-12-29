package wallapp.content.state.osslicenses

import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import wallapp.content.state.feed.FeedScrollStateController
import wallapp.content.state.settings.SettingViewStateFactory
import wallapp.content.state.settings.SettingsViewState
import wallapp.data.osslicense.OssLicense
import wallapp.data.osslicense.OssLicenseRepository
import wallapp.pixel.screen.ScreenViewStateProvider
import wallapp.pixel.toolbar.ToolbarViewState
import wallapp.resources.string.StringRepository
import wallapp.text.TextToolbarTitle
import wallapp.theme.ColorToken
import wallapp.view.ViewStateRefresher
import wallapp.view.menu.MenuItemFactory
import wallapp.viewmodel.ViewModel

class OssLicensesViewModel(
    ossLicenseRepository: OssLicenseRepository,
    private val settingViewStateFactory: SettingViewStateFactory,
    menuItemFactory: MenuItemFactory,
    strings: StringRepository,
    viewStateRefresher: ViewStateRefresher,
) : ViewModel(), ScreenViewStateProvider {

    private val feedScrollStateController = FeedScrollStateController(viewModelScope)

    val toolbarViewState = ToolbarViewState(
        navigationIcon = menuItemFactory.back,
        title = menuItemFactory.centeredText(TextToolbarTitle(strings.licenses)),
        containerColorOverride = ColorToken.Transparent,
    )

    private fun createViewState(
        licenses: List<OssLicense>,
    ): SettingsViewState {
        return SettingsViewState(
            toolbarViewState,
            licenses.map { settingViewStateFactory.createOssLicenseViewState(it) },
            fullScreenToolbarOffset = true,
            feedScrollStateWrapper = feedScrollStateController.scrollStateWrapper,
        )
    }

    override val viewState: StateFlow<SettingsViewState> = combine(
        ossLicenseRepository.ossLicenses,
        viewStateRefresher.refresh,
    ) { ossLicenses, _ ->
        createViewState(ossLicenses)
    }.stateIn(initialValue = createViewState(emptyList()))
}