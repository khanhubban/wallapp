package wallapp.ui

import moe.tlaster.precompose.navigation.Navigator
import wallapp.pixel.bottomsheet.BottomSheetDescriptor
import wallapp.pixel.bottomsheet.BottomSheetManagerCompose
import wallapp.screen.Screen
import wallapp.screen.ScreenManager
import wallapp.ui.screen.ScreenNavigator
import wallapp.viewmodel.ViewModelFactory


class ScreenControllersDesktop(
    override val viewModelFactory: ViewModelFactory,
    override val screenNavigator: ScreenNavigator,
    override val screenManager: ScreenManager,
    val navigator: Navigator,
    val initialScreen: Screen,
    override val bottomSheetManager: BottomSheetManagerCompose,
    override val bottomSheetDescriptor: BottomSheetDescriptor,
) : ScreenControllers
