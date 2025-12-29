package wallapp.app

import androidx.lifecycle.viewmodel.CreationExtras
import wallapp.screen.ScreenArgument.ArtistIdScreenArgument
import wallapp.screen.ScreenArgument.CollectionIdScreenArgument
import wallapp.screen.ScreenArgument.ConnectionsScreenArgument
import wallapp.screen.ScreenArgument.DesignIdScreenArgument
import wallapp.screen.ScreenArgument.ErrorScreenArgument
import wallapp.screen.ScreenArgument.FolderScreenArgument
import wallapp.screen.ScreenArgument.OssLicensesScreenArgument
import wallapp.screen.ScreenArgument.PaywallScreenArgument
import wallapp.screen.ScreenArgument.RemixIdScreenArgument
import wallapp.screen.ScreenArgument.RewardAdInternalScreenArgument
import wallapp.screen.ScreenArgument.SettingsArgument
import wallapp.screen.ScreenArgument.WallpaperShowcaseScreenArgument
import wallapp.screen.ScreenArgument.WallpaperSingleActionScreenArgument

object AppViewModelExtras {

    val ExtraKeyArtistId = object : CreationExtras.Key<ArtistIdScreenArgument> { }

    val ExtraKeyCollectionId = object : CreationExtras.Key<CollectionIdScreenArgument> { }

    val ExtraKeyConnectionsArguments = object : CreationExtras.Key<ConnectionsScreenArgument> { }

    val ExtraKeyDesignId = object : CreationExtras.Key<DesignIdScreenArgument> { }

    val ExtraKeyErrorScreen = object : CreationExtras.Key<ErrorScreenArgument> { }

    val ExtraKeyFolder = object : CreationExtras.Key<FolderScreenArgument> { }

    val ExtraKeyOssLicensesArguments = object : CreationExtras.Key<OssLicensesScreenArgument> { }

    val ExtraKeyPaywallScreenArgument = object : CreationExtras.Key<PaywallScreenArgument> { }

    val ExtraKeyRemixId = object : CreationExtras.Key<RemixIdScreenArgument> { }

    val ExtraKeyRewardAdInternalArguments = object : CreationExtras.Key<RewardAdInternalScreenArgument> { }

    val ExtraKeySettingsArguments = object : CreationExtras.Key<SettingsArgument> { }

    val ExtraKeyWallpaperSingleActionArguments = object : CreationExtras.Key<WallpaperSingleActionScreenArgument> { }

    val ExtraKeyWallpaperShowcaseArguments = object : CreationExtras.Key<WallpaperShowcaseScreenArgument> { }
}

val CreationExtras.mapToSingleExtra: Any?
    get() = get(AppViewModelExtras.ExtraKeyArtistId)
        ?: get(AppViewModelExtras.ExtraKeyCollectionId)
        ?: get(AppViewModelExtras.ExtraKeyConnectionsArguments)
        ?: get(AppViewModelExtras.ExtraKeyDesignId)
        ?: get(AppViewModelExtras.ExtraKeyErrorScreen)
        ?: get(AppViewModelExtras.ExtraKeyFolder)
        ?: get(AppViewModelExtras.ExtraKeyOssLicensesArguments)
        ?: get(AppViewModelExtras.ExtraKeyPaywallScreenArgument)
        ?: get(AppViewModelExtras.ExtraKeyRemixId)
        ?: get(AppViewModelExtras.ExtraKeyRewardAdInternalArguments)
        ?: get(AppViewModelExtras.ExtraKeySettingsArguments)
        ?: get(AppViewModelExtras.ExtraKeyWallpaperSingleActionArguments)
        ?: get(AppViewModelExtras.ExtraKeyWallpaperShowcaseArguments)
