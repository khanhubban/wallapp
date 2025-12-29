package wallapp.image

import android.net.Uri
import androidx.annotation.OptIn
import androidx.annotation.RawRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import kotlinx.coroutines.delay
import wallapp.crashtracking.CrashTrackingHolder
import wallapp.crashtracking.NonFatalException
import wallapp.string.quote

@OptIn(UnstableApi::class)
@Composable
fun ImageVideoExoplayer(
    url: String,
    imageVideoState: ImageVideoState,
    modifier: Modifier = Modifier,
) {
    val localContext = LocalContext.current
    val exoPlayer: ExoPlayer = remember {
        ExoPlayer.Builder(localContext).build().apply {
            setMediaItem(MediaItem.fromUri(url))
            prepare()
        }
    }

    ImageVideoExoplayer(exoPlayer, imageVideoState, modifier, resourceId = url)
}

@OptIn(UnstableApi::class)
@Composable
fun ImageVideoExoplayer(
    @RawRes rawRes: Int,
    imageVideoState: ImageVideoState,
    modifier: Modifier = Modifier,
) {
    val localContext = LocalContext.current
    val resourceId = "android.resource://${localContext.packageName}/$rawRes"
    val exoPlayer = remember {
        ExoPlayer.Builder(localContext).build().apply {
            val videoUri = Uri.parse(resourceId)
            setMediaItem(MediaItem.fromUri(videoUri))
            prepare()
        }
    }

    ImageVideoExoplayer(exoPlayer, imageVideoState, modifier, resourceId)
}

@Composable
fun ImageVideoExoplayer(
    exoPlayer: ExoPlayer,
    imageVideoState: ImageVideoState,
    modifier: Modifier,
    resourceId: String,
) {
    val callbacks = imageVideoState.playbackCallbacks
    val pausePlayback = imageVideoState.pausePlayback

    var lastReportedPlaybackPosition by remember { mutableIntStateOf(-1) }

    DisposableEffect(exoPlayer) {
        onDispose {
            exoPlayer.release()
        }
    }

    val updatePlaybackPosition = {
        val currentPosition = exoPlayer.currentPosition.toInt()
        if (currentPosition != lastReportedPlaybackPosition) {
            callbacks?.onPlaybackPositionTick(currentPosition)
            lastReportedPlaybackPosition = currentPosition
        }
    }

    var playbackIsReady by remember { mutableStateOf(false) }
    val updatePlaybackIsReady: (Boolean) -> Unit = { isReady: Boolean ->
        playbackIsReady = isReady
    }

    LaunchedEffect(playbackIsReady, pausePlayback) {
        if (playbackIsReady) {
            exoPlayer.playWhenReady = !pausePlayback
        }
    }

    LaunchedEffect(exoPlayer) {
        while (true) {
            if (exoPlayer.isPlaying) {
                updatePlaybackPosition()
            }
            delay(100) // Update every 100 milliseconds
        }
    }

    AndroidView(
        factory = { context ->
            try {
                PlayerView(context).apply {
                    configure(
                        exoPlayer = exoPlayer,
                        callbacks = callbacks,
                        imageVideoState = imageVideoState,
                        updatePlaybackIsReady = updatePlaybackIsReady,
                        updatePlaybackPosition = updatePlaybackPosition,
                    )
                }
            } catch (e: Exception) {
                // Android being Android, we need to handle the case where Exoplayer fails to
                // be created for some reason. #2208.
                val message = "Error creating ExoPlayer view for resource: ${resourceId.quote()}"
                CrashTrackingHolder.crashTracking
                    .logNonFatalException(NonFatalException(message, e))
                callbacks?.onPlaybackError(
                    ImageVideoPlaybackError(
                        message = "$message: ${e.message}",
                        code = -1,
                        codeAdditional = null
                    )
                )
                ImageVideoErrorView(context)
            }
        },
        modifier = modifier,
    )
}

val ImageVideoState.repeatMode: Int
    get() = when (this) {
        /**
         * If [Player.REPEAT_MODE_ONE] is set, [Player.STATE_ENDED] will never be called, and this needs
         * to be called for the [seekToMillisOnLoop] functionality to work.
         */
        is ImageVideoState.Playback -> if (isLooping && seekToMillisOnLoop == 0) {
            Player.REPEAT_MODE_ONE
        } else {
            Player.REPEAT_MODE_OFF
        }
    }

val ImageVideoState.startPositionMillis: Int?
    get() = when (this) {
        is ImageVideoState.Playback -> startPositionMillis
    }

val ImageVideoState.seekToPositionOnEnded: Int?
    get() = when (this) {
        is ImageVideoState.Playback -> {
            // Note: this appears to create a visual artifact on subsequent loops on
            // an emulator. See #2113.
            val seekToMillisOnLoop = seekToMillisOnLoop
            if (isLooping && seekToMillisOnLoop != null && seekToMillisOnLoop > 0) {
                seekToMillisOnLoop
            } else {
                null
            }
        }
    }

@OptIn(UnstableApi::class)
private fun PlayerView.configure(
    exoPlayer: ExoPlayer,
    callbacks: ImageVideoPlaybackCallbacks?,
    imageVideoState: ImageVideoState,
    updatePlaybackIsReady: (Boolean) -> Unit,
    updatePlaybackPosition: () -> Unit,
) {
    useController = false // Call first to prevent the UI controls briefly appearing. See #2119.

    player = exoPlayer

    exoPlayer.repeatMode = imageVideoState.repeatMode

    // Defer playback until [STATE_READY] is received
    exoPlayer.playWhenReady = false
    
    imageVideoState.startPositionMillis?.also { exoPlayer.seekTo(it.toLong()) }

    setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM)

    if (imageVideoState.useAudio) {
        exoPlayer.setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(C.USAGE_MEDIA)
                .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
                .build(),
            true,  // Request audio focus and pause other audio
        )
    } else {
        exoPlayer.setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(C.USAGE_MEDIA)
                .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
                .build(),
            false,
        )
    }

    // Add event listeners to ExoPlayer
    exoPlayer.addListener(
        object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                updatePlaybackIsReady(playbackState == Player.STATE_READY)
                when (playbackState) {
                    Player.STATE_IDLE -> {
                        // No-op
                    }
                    Player.STATE_BUFFERING -> {
                        callbacks?.onPlaybackBufferingStart()
                    }
                    Player.STATE_READY -> {
                        exoPlayer.playWhenReady = true
                        callbacks?.onPlaybackRenderingStart()
                    }
                    Player.STATE_ENDED -> {
                        updatePlaybackPosition()
                        callbacks?.onPlaybackComplete()

                        // Note: this appears to create a visual artifact on subsequent loops on
                        // an emulator. See #2113.
                        imageVideoState.seekToPositionOnEnded?.also {
                            exoPlayer.seekTo(it.toLong())
                            exoPlayer.playWhenReady = true
                        }
                    }
                    else -> {
//                        Log.d("[exoplayer] Playback state: $playbackState")
                    }
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                callbacks?.onPlaybackError(
                    ImageVideoPlaybackError(
                        message = "Playback error: ${error.message}",
                        code = error.errorCode,
                        codeAdditional = null // error.errorCodeName
                    )
                )
            }
        }
    )
}