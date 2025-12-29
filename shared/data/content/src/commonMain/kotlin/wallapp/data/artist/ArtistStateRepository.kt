package wallapp.data.artist

import kotlinx.coroutines.flow.Flow

interface ArtistStateRepository {

    val artistStates: Flow<List<ArtistState>>
}