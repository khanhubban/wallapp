package wallapp.theme

import kotlinx.coroutines.flow.MutableStateFlow

class SystemThemeNoOp(
    override val isDarkTheme: Boolean = false,
    override val darkTheme: MutableStateFlow<Boolean> = MutableStateFlow(isDarkTheme),
) : SystemTheme
