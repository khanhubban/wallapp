package wallapp.player.metadata

/**
 * Houses data about the audio to play.
 */
data class AudioMetadata(
    var id: String,
    val mediaUri: String,
    var title: String?,
    var artist: String?,
    val album: String?,
    val albumArtUri: String?,
)