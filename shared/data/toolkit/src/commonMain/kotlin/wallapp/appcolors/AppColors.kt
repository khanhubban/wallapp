package wallapp.appcolors

import wallapp.annotation.ColorInt

interface AppColors {

    @get:ColorInt val accent: Int

    @get:ColorInt val surface: Int
    @get:ColorInt val onSurface: Int

    @get:ColorInt val toolbar: Int
    @get:ColorInt val onToolbar: Int

    @get:ColorInt val brandLight: Int

    @get:ColorInt val upgradeFromInstantButtonBackground: Int
    @get:ColorInt val checkLicenseButtonBackground: Int

    @get:ColorInt val itemHighlight: Int

    val animatingHighlights: Pair<Int, Int>
}
