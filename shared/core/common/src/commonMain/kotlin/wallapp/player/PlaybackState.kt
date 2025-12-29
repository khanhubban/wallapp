package wallapp.player

enum class PlaybackState {
    Unknown,    /** The initial, uninitialized state. **/
    Idle,       /** The player does not have any media to play.     **/
    Buffering,  /** The player is not able to immediately play from its current position.
                    This state typically occurs when more data needs to be loaded.  */
    Ready,      /** The player is able to immediately play from its current position. */
    Playing,    /** Media is currently playing. */
    Ended,      /** The player has finished playing the media. */
    Error       /** An error has occurred. Is triggered in the event of a network error. */
}
