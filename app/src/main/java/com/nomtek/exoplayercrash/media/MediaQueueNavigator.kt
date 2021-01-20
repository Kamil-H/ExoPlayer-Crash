package com.nomtek.exoplayercrash.media

import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.session.MediaSessionCompat
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator

class MediaQueueNavigator(mediaSession: MediaSessionCompat) : TimelineQueueNavigator(mediaSession) {

    override fun getMediaDescription(player: Player, windowIndex: Int): MediaDescriptionCompat = MediaDescriptionCompat.Builder().build()
}
