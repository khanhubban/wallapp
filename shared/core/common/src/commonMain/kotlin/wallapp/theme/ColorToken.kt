package wallapp.theme

import androidx.compose.runtime.Immutable
import co.touchlab.skie.configuration.annotations.SealedInterop
import wallapp.graphics.Color

@Immutable
@SealedInterop.Enabled
sealed interface ColorToken {

    @Immutable data object Transparent : ColorToken
    @Immutable data object LocalContent : ColorToken
    @Immutable data object ThemeBackground : ColorToken
    @Immutable data object ThemeOnBackground : ColorToken
    @Immutable data object ThemeSurface : ColorToken
    @Immutable data object ThemeOnSurface : ColorToken
    @Immutable data object ThemeSurfaceVariant : ColorToken
    @Immutable data object ThemeOnSurfaceVariant : ColorToken
    @Immutable data object ThemePrimary : ColorToken
    @Immutable data object ThemeOnPrimary : ColorToken
    @Immutable data object ThemeSecondary : ColorToken
    @Immutable data object ThemeOnSecondary : ColorToken
    @Immutable data object ThemeTertiary : ColorToken
    @Immutable data object ThemeOnTertiary : ColorToken
    @Immutable data object ThemeScrim : ColorToken

    @Immutable data class Custom(val color: Color) : ColorToken
}

val Color.customColorToken: ColorToken
    get() = ColorToken.Custom(this)