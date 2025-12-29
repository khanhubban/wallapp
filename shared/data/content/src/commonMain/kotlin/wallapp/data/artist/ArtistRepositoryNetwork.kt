package wallapp.data.artist

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import wallapp.content.model.Id
import wallapp.content.model.Id.ArtistId
import wallapp.data.model.ModelRepository
import wallapp.data.wallpaper.WallpaperRepository
import wallapp.util.combine

class ArtistRepositoryNetwork(
    private val modelRepository: ModelRepository,
    private val wallpaperRepository: WallpaperRepository,
    private val coroutineScopeMain: CoroutineScope,
) : ArtistRepository {

    override val artists: StateFlow<List<Artist>> = modelRepository.allArtists
        .map { allArtists -> allArtists.sortedBy { it.name } }
        .stateIn(coroutineScopeMain, SharingStarted.Eagerly, emptyList())

    override fun getArtist(id: ArtistId): Flow<Artist?> {
        return artists.map { artists ->
            artists.find { it.id == id }
        }
    }

    override fun getArtists(ids: List<ArtistId>): Flow<List<Artist>?> {
        return artists.map { artists ->
            artists.filter { ids.contains(it.id) }
        }
    }

    override fun getArtists(wallpaperId: Id): Flow<List<Artist>?> = combine(
        wallpaperRepository.getItem(wallpaperId),
        artists,
    ) { wallpaperItem, artists ->
        wallpaperItem?.artistId
            ?.let { artistId -> artists.find { it.id == artistId } }
            ?.let { listOf(it) } ?: emptyList()
    }

}