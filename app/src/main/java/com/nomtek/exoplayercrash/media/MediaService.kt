package com.nomtek.exoplayercrash.media

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.media.MediaBrowserServiceCompat
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.Timeline
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.nomtek.exoplayercrash.utils.Injector

class MediaService : MediaBrowserServiceCompat(), Player.EventListener {

    lateinit var exoPlayer: ExoPlayer
    lateinit var mediaSessionConnection: MediaSessionConnection

    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var mediaController: MediaControllerCompat
    private lateinit var mediaSessionConnector: MediaSessionConnector

    private val tag = this::class.java.simpleName

    override fun onCreate() {
        Injector.inject(this)
        super.onCreate()

        val activityPendingIntent = packageManager?.getLaunchIntentForPackage(packageName)?.let { sessionIntent ->
            PendingIntent.getActivity(this, 0, sessionIntent, 0)
        }

        exoPlayer.addListener(this)

        mediaSession = MediaSessionCompat(this, tag).apply {
            setSessionActivity(activityPendingIntent)
            isActive = true
        }

        mediaController = MediaControllerCompat(this, mediaSession)

        sessionToken = mediaSession.sessionToken

        mediaSessionConnector = MediaSessionConnector(mediaSession).apply {
            setPlayer(exoPlayer)
            setQueueNavigator(MediaQueueNavigator(mediaSession))
        }
    }

    override fun onTimelineChanged(timeline: Timeline, reason: Int) {
        super.onTimelineChanged(timeline, reason)
        mediaSessionConnection.onTimelineChanged(timeline, exoPlayer.shuffleModeEnabled)
    }

    override fun onLoadChildren(parentId: String, result: Result<List<MediaBrowserCompat.MediaItem>>) {
        result.sendResult(emptyList())
    }

    override fun onGetRoot(clientPackageName: String, clientUid: Int, rootHints: Bundle?): BrowserRoot? {
        return BrowserRoot("", null)
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        super.onTaskRemoved(rootIntent)
        exoPlayer.stop(true)
    }

    override fun onDestroy() {
        mediaSession.run {
            isActive = false
            release()
        }

        exoPlayer.stop(true)
    }
}
