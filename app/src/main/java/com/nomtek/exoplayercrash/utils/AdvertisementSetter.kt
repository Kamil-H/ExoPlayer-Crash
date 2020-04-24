package com.nomtek.exoplayercrash.utils

import android.os.Handler
import android.os.Looper
import com.google.android.exoplayer2.ExoPlayer
import com.nomtek.exoplayercrash.models.Advertisement
import com.nomtek.exoplayercrash.models.ReportingData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.withContext

class AdvertisementSetter constructor(private val exoPlayer: ExoPlayer) {

    private val channel = ConflatedBroadcastChannel<Pair<Advertisement, ReportingData>>()

    val listenedAdvertisement = channel.asFlow()

    suspend fun set(advertisement: Advertisement, reportingData: ReportingData, positionInPlaylist: Int) {
        withContext(Dispatchers.Main) {
            exoPlayer.createMessage { messageType, payload ->
                if (messageType == ADVERTISEMENT_MESSAGE_TYPE && payload is ReportingData) {
                    channel.offer(advertisement to payload)
                }
            }
                .setPayload(reportingData)
                .setPosition(positionInPlaylist, (reportingData.position * SECONDS_TO_MILLISECONDS_MULTIPLIER).toLong())
                .setHandler(Handler(Looper.getMainLooper()))
                .setDeleteAfterDelivery(false)
                .setType(ADVERTISEMENT_MESSAGE_TYPE)
                .send()
        }
    }

    companion object {
        private const val SECONDS_TO_MILLISECONDS_MULTIPLIER = 1000

        private const val ADVERTISEMENT_MESSAGE_TYPE = 0x1234
    }
}
