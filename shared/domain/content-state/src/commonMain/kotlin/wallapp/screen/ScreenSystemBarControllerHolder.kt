package wallapp.screen

import kotlinx.coroutines.flow.StateFlow

interface ScreenSystemBarControllerHolder {

    val screenSystemBarController: StateFlow<ScreenSystemBarController>

}