package wallapp.content.state.wallpaper

import androidx.compose.runtime.Immutable
import co.touchlab.skie.configuration.annotations.SealedInterop
import wallapp.graphics.Color
import wallapp.pixel.button.ButtonViewState
import wallapp.pixel.image.ImageViewState
import wallapp.pixel.menu.MenuItem
import wallapp.pixel.menu.MenuItem.MenuItemButton
import wallapp.pixel.screen.ScreenViewState
import wallapp.pixel.swipetodismiss.OnSwipeToDismiss
import wallapp.pixel.theme.ThemeColors

@Immutable
@SealedInterop.Enabled
sealed class WallpaperShowcaseViewState : ScreenViewState {

    @Immutable
    data object Loading : WallpaperShowcaseViewState()

    @Immutable
    data object Error : WallpaperShowcaseViewState()

    @Immutable
    data class Success(
        val viewSpec: WallpaperShowcaseViewSpec,
        val shadowStatusBar: ImageViewState?,
        val overlayScreen: ScreenViewState?,
        val wallpaperPreviews: List<WallpaperPreviewViewState>,
        val currentPreviewIndex: Int?,
        val onPageChangedWallpaperPreview: (Int) -> Unit,
        val wallpaperArtist: WallpaperArtistViewState,
        val detail: WallpaperDetailViewState,
        val actionButton: MenuItem,
        val title: MenuItem,
        val close: MenuItem,
        val actionItems: MenuItem,
        val previewActionButton: MenuItem?,
        val themeColors: ThemeColors?,
        val useBackgroundGradient: Boolean,
        val indicatorPillBackgroundColor: Color,
        val indicatorColor: Color,
        val onSwipeToDismiss: OnSwipeToDismiss?,
        val offsetForStatusBar: Boolean,
        val previousClickContentDescription: String,
        val nextClickContentDescription: String,
    ): WallpaperShowcaseViewState() {

        companion object {
            val Preset = Success(
                viewSpec = WallpaperShowcaseViewSpec.Preset,
                shadowStatusBar = null,
                overlayScreen = null,
                wallpaperPreviews = listOf(WallpaperPreviewViewState.Preset),
                currentPreviewIndex = 0,
                onPageChangedWallpaperPreview = {},
                wallpaperArtist = WallpaperArtistViewState.Preset,
                detail = WallpaperDetailViewState.Preset,
                actionButton = MenuItemButton(ButtonViewState.Preset),
                title = MenuItemButton(ButtonViewState.Preset),
                close = MenuItemButton(ButtonViewState.Preset),
                actionItems = MenuItemButton(ButtonViewState.Preset),
                previewActionButton = null,
                themeColors = ThemeColors.Preset,
                useBackgroundGradient = false,
                indicatorPillBackgroundColor = Color.White,
                indicatorColor = Color.Black,
                onSwipeToDismiss = null,
                offsetForStatusBar = true,
                previousClickContentDescription = "Previous",
                nextClickContentDescription = "Next",
            )
        }
    }
}
