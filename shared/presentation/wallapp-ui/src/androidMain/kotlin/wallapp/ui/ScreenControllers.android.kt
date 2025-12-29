package wallapp.ui

import androidx.navigation.NavHostController
import wallapp.pixel.bottomsheet.BottomSheetDescriptor
import wallapp.pixel.bottomsheet.BottomSheetManagerCompose
import wallapp.screen.Screen
import wallapp.screen.ScreenManager
import wallapp.ui.screen.ScreenNavigator
import wallapp.viewmodel.ViewModelFactory
import wallapp.viewmodel.ViewModelProviderFactory

class ScreenControllersAndroid(
    override val viewModelFactory: ViewModelFactory,
    val viewModelProviderFactory: ViewModelProviderFactory,
    override val screenNavigator: ScreenNavigator,
    override val screenManager: ScreenManager,
    val screenNavHostController: NavHostController,
    val initialScreen: Screen,
    override val bottomSheetManager: BottomSheetManagerCompose,
    override val bottomSheetDescriptor: BottomSheetDescriptor,
) : ScreenControllers
