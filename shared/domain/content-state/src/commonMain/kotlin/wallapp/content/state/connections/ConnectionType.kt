package wallapp.content.state.connections

import kotlinx.serialization.Serializable

@Serializable
enum class ConnectionType {
    Following,
    Favorites,
    Wallpapers,
}
