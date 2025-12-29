package wallapp.data.showcase

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import wallapp.content.model.Id.ArtistId
import wallapp.content.model.Id.CategoryId
import wallapp.content.model.Id.RemixId
import wallapp.coroutine.CoroutineScopeMain
import wallapp.license.state.LicenseState
import wallapp.license.state.isLicensedAny
import wallapp.remoteconfig.data.RemoteConfigData
import wallapp.remoteconfig.data.RemoteConfigDataDefaultsProviderDefault

class ShowcaseRepositoryHighlightsConfigDefault(
    val remoteConfigData: RemoteConfigData,
    private val licenseState: LicenseState,
    @CoroutineScopeMain private val coroutineScopeMain: CoroutineScope,
) : ShowcaseRepositoryHighlightsConfig {

    override val highlightArtist: StateFlow<ArtistId>
        get() = remoteConfigData.highlightArtist.map { ArtistId(it) }
            .stateIn(
                coroutineScopeMain,
                SharingStarted.Lazily,
                ArtistId(RemoteConfigDataDefaultsProviderDefault.highlightArtist)
            )

    override val highlightCollectionOfTheWeek: StateFlow<CategoryId>
        get() = remoteConfigData.highlightCollectionOfTheWeek.map { CategoryId(it) }
            .stateIn(
                coroutineScopeMain,
                SharingStarted.Lazily,
                CategoryId(RemoteConfigDataDefaultsProviderDefault.highlightCollectionOfTheWeek)
            )

    override val highlightJustAdded: StateFlow<CategoryId>
        get() = remoteConfigData.highlightJustAdded.map { CategoryId(it) }
            .stateIn(
                coroutineScopeMain,
                SharingStarted.Lazily,
                CategoryId(RemoteConfigDataDefaultsProviderDefault.highlightJustAdded)
            )

    override val highlightMostPopular: StateFlow<CategoryId>
        get() = remoteConfigData.highlightMostPopular.map { CategoryId(it) }
            .stateIn(
                coroutineScopeMain,
                SharingStarted.Lazily,
                CategoryId(RemoteConfigDataDefaultsProviderDefault.highlightMostPopular)
            )

    override val highlightWallpaperOfTheWeek: StateFlow<RemixId>
        get() = remoteConfigData.highlightWallpaperOfTheWeek.map { RemixId(it) }
            .stateIn(
                coroutineScopeMain,
                SharingStarted.Lazily,
                RemixId(RemoteConfigDataDefaultsProviderDefault.highlightWallpaperOfTheWeek)
            )

    override val showPlus: StateFlow<Boolean>
        get() = licenseState.licenseStateType
            .map { it.isLicensedAny() }
            .stateIn(
                coroutineScopeMain,
                SharingStarted.Lazily,
                false
            )
}