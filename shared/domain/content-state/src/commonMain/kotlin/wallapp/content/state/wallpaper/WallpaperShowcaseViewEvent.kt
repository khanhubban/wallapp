package wallapp.content.state.wallpaper

import androidx.compose.runtime.Immutable
import co.touchlab.skie.configuration.annotations.SealedInterop
import wallapp.pixel.view.ViewEvent

@Immutable
@SealedInterop.Enabled
sealed class WallpaperShowcaseViewEvent : ViewEvent {

    @Immutable
    data object ToggleFollowArtist : WallpaperShowcaseViewEvent()

    @Immutable
    data object NavigateToArtist : WallpaperShowcaseViewEvent()

    @Immutable
    data object ShareWallpaper : WallpaperShowcaseViewEvent()

    @Immutable
    data object FollowIndicationAnimStarted : WallpaperShowcaseViewEvent()
}