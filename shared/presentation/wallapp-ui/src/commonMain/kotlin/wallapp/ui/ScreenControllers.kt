package wallapp.ui

import wallapp.pixel.bottomsheet.BottomSheetDescriptor
import wallapp.pixel.bottomsheet.BottomSheetManagerCompose
import wallapp.screen.ScreenManager
import wallapp.ui.screen.ScreenNavigator
import wallapp.viewmodel.ViewModelFactory


interface ScreenControllers {
    val viewModelFactory: ViewModelFactory
    val screenNavigator: ScreenNavigator
    val screenManager: ScreenManager
    val bottomSheetManager: BottomSheetManagerCompose
    val bottomSheetDescriptor: BottomSheetDescriptor
}