package wallapp.image

interface ImageVideoPlaybackCallbacks {

    // The video has completed playback. Note that if the video is looping, this will be
    // called each time the video loops.
    fun onPlaybackComplete()

    /**
     * Called with updates on the current playback position in milliseconds. Note that [position]
     * will be accurate at the time this function is called, updates are only provided every ~100 ms
     * or so on both Android and iOS.
     */
    fun onPlaybackPositionTick(position: Int)

    fun onPlaybackBufferingStart()
    fun onPlaybackRenderingStart()

    fun onPlaybackError(error: ImageVideoPlaybackError)
}