package wallapp.content.state.colors

import kotlinx.coroutines.flow.StateFlow
import wallapp.graphics.Color

interface ContentColorManager {

    val topBarContainer: StateFlow<Color>

    val profileTopBarContainer: StateFlow<Color>

    val indexBottomBarContainer: StateFlow<Color>
    val indexStatusBarContainer: StateFlow<Color>

    val controlButtonBackgroundColorLight: StateFlow<Color>
    val controlButtonOnBackgroundColorLight: StateFlow<Color>
    val controlButtonBackgroundColorDark: StateFlow<Color>
    val controlButtonOnBackgroundColorDark: StateFlow<Color>
}