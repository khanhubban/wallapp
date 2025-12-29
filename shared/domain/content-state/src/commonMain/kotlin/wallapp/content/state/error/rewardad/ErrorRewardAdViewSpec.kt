package wallapp.content.state.error.rewardad

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import wallapp.pixel.view.ViewSpec

@Immutable
data class ErrorRewardAdViewSpec(
    val unlockContainerSize: DpSize,
    val upgradeContainerSize: DpSize,
    val unlockWallpaperContentPadding: Dp,
    val spaceAboveUpgradeToUnlock: Dp,
    val spaceBetweenUpgradeContent: Dp,
    val paddingBottom: Dp,
) : ViewSpec
