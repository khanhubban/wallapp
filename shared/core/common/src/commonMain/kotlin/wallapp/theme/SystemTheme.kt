package wallapp.theme

import kotlinx.coroutines.flow.StateFlow


/**
 * Does the system recommend the app use its dark theme?
 */
interface SystemTheme {
    val isDarkTheme: Boolean
    val darkTheme: StateFlow<Boolean>
}
