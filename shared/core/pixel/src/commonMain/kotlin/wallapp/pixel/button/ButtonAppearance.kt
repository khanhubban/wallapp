package wallapp.pixel.button

import co.touchlab.skie.configuration.annotations.EnumInterop

@EnumInterop.Enabled
enum class ButtonAppearance {
    None,
    Default,
    Disabled,
    DisabledWithClick,
    Outline,
    Highlight,
}