package wallapp.content.state.settings

import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import wallapp.content.state.feed.FeedScrollStateController
import wallapp.pixel.screen.ScreenViewStateProvider
import wallapp.pixel.toolbar.ToolbarViewState
import wallapp.screen.ScreenArgument.SettingsArgument
import wallapp.settings.SettingManager
import wallapp.text.TextToolbarTitle
import wallapp.theme.ColorToken
import wallapp.util.combine
import wallapp.view.ViewStateRefresher
import wallapp.view.menu.MenuItemFactory
import wallapp.viewmodel.ViewModel

class SettingsViewModel(
    private val argument: SettingsArgument,
    private val settingViewStateFactory: SettingViewStateFactory,
    private val settingManager: SettingManager,
    menuItemFactory: MenuItemFactory,
    viewStateRefresher: ViewStateRefresher,
) : ViewModel(), ScreenViewStateProvider {

    val title: String
        get() = argument.title

    val toolbarViewState = ToolbarViewState(
        navigationIcon = menuItemFactory.createCloseButton(),
        title = menuItemFactory.centeredText(TextToolbarTitle(title)),
        containerColorOverride = ColorToken.Transparent,
    )

    private val feedScrollStateController = FeedScrollStateController(viewModelScope)

    private fun createViewState(): SettingsViewState {
        return SettingsViewState(
            toolbarViewState = toolbarViewState,
            settingViewStates = settingViewStateFactory.getByKeys(argument.settingKeys),
            fullScreenToolbarOffset = false,
            feedScrollStateWrapper = feedScrollStateController.scrollStateWrapper,
        )
    }

    override fun onCleared() { }

    private val settingKey1: String
        get() = argument.settingKeys[0]
    private val settingKey2: String?
        get() = argument.settingKeys.getOrNull(1)
    private val settingKey3: String?
        get() = argument.settingKeys.getOrNull(2)

    private val settingFlow1 = settingManager.onSettingChanged(settingKey1)
    private val settingFlow2 = settingKey2?.let { settingManager.onSettingChanged(it) } ?: flowOf(Unit)
    private val settingFlow3 = settingKey3?.let { settingManager.onSettingChanged(it) } ?: flowOf(Unit)

    override val viewState: StateFlow<SettingsViewState> =
        combine(settingFlow1, settingFlow2, settingFlow3, viewStateRefresher.refresh, feedScrollStateController.lastScrollStateUpdateFlow) {
          _, _, _, _, _ -> createViewState()
        }.stateIn(createViewState())

    init {
        require(argument.settingKeys.size <= 3) {
            "TODO: Implement support for more than 3 settings (currently ${argument.settingKeys.size})"
        }
    }
}