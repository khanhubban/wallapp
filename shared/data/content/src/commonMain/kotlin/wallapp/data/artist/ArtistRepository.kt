package wallapp.data.artist

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import wallapp.content.model.Id
import wallapp.content.model.Id.ArtistId

interface ArtistRepository {

    val artists: StateFlow<List<Artist>>
    fun getArtist(id: ArtistId): Flow<Artist?>
    fun getArtists(ids: List<ArtistId>): Flow<List<Artist>?>
    fun getArtists(wallpaperId: Id): Flow<List<Artist>?>
}