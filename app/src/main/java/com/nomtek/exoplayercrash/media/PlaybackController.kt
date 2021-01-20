package com.nomtek.exoplayercrash.media

import android.support.v4.media.session.MediaControllerCompat
import com.google.android.exoplayer2.MediaItem
import com.nomtek.exoplayercrash.extension.*
import com.nomtek.exoplayercrash.models.PlaybackState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class PlaybackController(
    private val mediaPlayer: MediaPlayer,
    private val mediaSessionConnection: MediaSessionConnection
) {

    private val transportControls: MediaControllerCompat.TransportControls
        get() = mediaSessionConnection.transportControls

    val state: Flow<PlaybackState> = mediaSessionConnection.playbackState.map {
        PlaybackState(
            isPlaying = it.isPlaying,
            isPrepared = it.isPrepared,
            isLoading = it.isLoading,
            isPauseEnabled = it.isPauseEnabled,
            isSkipToNextEnabled = it.isSkipToNextEnabled,
            isSkipToPreviousEnabled = it.isSkipToPreviousEnabled,
            isPlayEnabled = it.isPlayEnabled
        )
    }

    val nowPlaying: Flow<String> = mediaPlayer.nowPlayingId.mapNotNull { it }
    val currentPlaylist: Flow<List<String>> = mediaPlayer.currentItems
    val prepared: Flow<Unit> = mediaSessionConnection.prepared

    fun playAt(position: Int) {
        mediaPlayer.playAt(position)
    }

    fun prepareAndPlay(id: String?, mediaItems: List<MediaItem>) {
        mediaPlayer.play(mediaItems, id)
        transportControls.play()
    }

    fun insert(position: Int, item: MediaItem) {
        mediaPlayer.insert(position, item)
    }

    suspend fun removeAt(position: Int): Boolean =
        suspendCancellableCoroutine { continuation ->
            mediaPlayer.removeAt(position) {
                continuation.resume(it)
            }
        }
}
