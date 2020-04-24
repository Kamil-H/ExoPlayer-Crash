package com.nomtek.exoplayercrash.media

import android.os.Handler
import android.os.Looper
import android.support.v4.media.MediaMetadataCompat
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.IllegalSeekPositionException
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.upstream.HttpDataSource
import com.nomtek.exoplayercrash.extension.id
import com.nomtek.exoplayercrash.extension.toMediaSource
import com.nomtek.exoplayercrash.utils.AdvertisementManager

class MediaPlayer constructor(
    private val exoPlayer: ExoPlayer,
    private val httpDataSourceFactory: HttpDataSource.Factory,
    private val advertisementManager: AdvertisementManager
) {

    private val currentMediaSource: ConcatenatingMediaSource = ConcatenatingMediaSource()

    fun play(list: List<MediaMetadataCompat>, mediaId: String?, playWhenReady: Boolean = true) {
        if (list.isEmpty()) {
            return
        }

        val initialWindowIndex = if (mediaId == null) 0 else list.map { it.id }.indexOf(mediaId)

        if (initialWindowIndex == -1) {
            return
        }

        currentMediaSource.clear()
        addAll(list)
        exoPlayer.playWhenReady = playWhenReady
        exoPlayer.prepare(currentMediaSource)
        exoPlayer.seekTo(initialWindowIndex, 0)
    }

    fun playAt(position: Int, time: Long = 0) {
        try {
            exoPlayer.seekTo(position, time)
        } catch (exception: IllegalSeekPositionException) {
            exoPlayer.seekTo(0, time)
        }
    }

    private fun addAll(list: List<MediaMetadataCompat>) {
        val mediaSource = list.map { it.toMediaSource(dataSourceFactory()) }
        currentMediaSource.addMediaSources(mediaSource)
        advertisementManager.onPlay(list.mapNotNull { it.id })
    }

    fun removeAt(position: Int, onRemoveAction: (Boolean) -> Unit) {
        try {
            currentMediaSource.removeMediaSource(position, Handler(Looper.getMainLooper())) {
                onRemoveAction(true)
            }
        } catch (exception: IndexOutOfBoundsException) {
            exception.printStackTrace()
            onRemoveAction(false)
        }
    }

    fun insert(position: Int, item: MediaMetadataCompat) {
        try {
            currentMediaSource.addMediaSource(position, item.toMediaSource(dataSourceFactory()), Handler(Looper.getMainLooper())) {
                advertisementManager.onInsert(position, item.id!!)
            }
        } catch (exception: IndexOutOfBoundsException) {
            exception.printStackTrace()
        }
    }

    private fun dataSourceFactory() = httpDataSourceFactory
}
