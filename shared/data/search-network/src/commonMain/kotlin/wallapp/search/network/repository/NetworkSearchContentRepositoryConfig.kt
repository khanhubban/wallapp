package wallapp.search.network.repository

import kotlinx.coroutines.flow.Flow

interface NetworkSearchContentRepositoryConfig {

    val endpointUrl: Flow<String>
}