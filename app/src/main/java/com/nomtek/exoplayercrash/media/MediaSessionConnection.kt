package com.nomtek.exoplayercrash.media

import android.content.ComponentName
import android.content.Context
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import com.google.android.exoplayer2.Timeline
import com.nomtek.exoplayercrash.extension.customProperty
import com.nomtek.exoplayercrash.extension.descriptionTags
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.asFlow

class MediaSessionConnection(context: Context) {

    private val playbackStateChannel = ConflatedBroadcastChannel<PlaybackStateCompat>().apply {
        offer(EMPTY_PLAYBACK_STATE)
    }
    val playbackState = playbackStateChannel.asFlow()

    private val nowPlayingChannel = ConflatedBroadcastChannel<MediaMetadataCompat>().apply {
        offer(NOTHING_PLAYING)
    }
    val nowPlaying = nowPlayingChannel.asFlow()

    private val currentPlaylistChannel = ConflatedBroadcastChannel<List<String>>().apply {
        offer(emptyList())
    }
    val currentPlaylist = currentPlaylistChannel.asFlow()

    private val preparedChannel = ConflatedBroadcastChannel<Unit>()
    val prepared = preparedChannel.asFlow()

    val transportControls: MediaControllerCompat.TransportControls
        get() = mediaController.transportControls

    private val mediaBrowserConnectionCallback = MediaBrowserConnectionCallback(context)
    private val serviceName = ComponentName(context, MediaService::class.java)
    private val mediaBrowser = MediaBrowserCompat(context, serviceName, mediaBrowserConnectionCallback, null)

    private lateinit var mediaController: MediaControllerCompat

    fun connect() {
        mediaBrowser.connect()
    }

    fun onTimelineChanged(timeline: Timeline?, shuffleModeEnabled: Boolean) {
        if (timeline == null) {
            currentPlaylistChannel.offer(emptyList())
            return
        }
        val tags = timeline.descriptionTags(shuffleModeEnabled)
        currentPlaylistChannel.offer(tags.mapNotNull { it.mediaId })
    }

    private inner class MediaBrowserConnectionCallback(
        private val context: Context
    ) : MediaBrowserCompat.ConnectionCallback() {

        override fun onConnected() {
            mediaController = MediaControllerCompat(context, mediaBrowser.sessionToken).apply {
                registerCallback(MediaControllerCallback())
                preparedChannel.offer(Unit)
            }
        }
    }

    private inner class MediaControllerCallback : MediaControllerCompat.Callback() {

        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            playbackStateChannel.offer(state ?: EMPTY_PLAYBACK_STATE)
        }

        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            Log.i("MediaMetadataCompat", "Custom property in onMetadataChanged callback: ${metadata?.customProperty}")
            nowPlayingChannel.offer(metadata ?: NOTHING_PLAYING)
        }

        override fun onSessionDestroyed() {
            mediaBrowserConnectionCallback.onConnectionSuspended()
        }
    }
}

val EMPTY_PLAYBACK_STATE: PlaybackStateCompat = PlaybackStateCompat.Builder()
    .setState(PlaybackStateCompat.STATE_NONE, 0, 0f)
    .build()

val NOTHING_PLAYING: MediaMetadataCompat = MediaMetadataCompat.Builder()
    .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, null)
    .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, 0)
    .build()
