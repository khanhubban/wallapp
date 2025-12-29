package wallapp.media.network.repository

import kotlinx.coroutines.flow.Flow

interface NetworkMediaMapRepositoryConfig {

    val endpointUrl: Flow<String>
}