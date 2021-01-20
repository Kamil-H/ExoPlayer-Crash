package com.nomtek.exoplayercrash.media

import com.google.android.exoplayer2.*
import com.nomtek.exoplayercrash.utils.AdvertisementManager
import kotlinx.coroutines.flow.MutableStateFlow

class MediaPlayer constructor(
    private val exoPlayer: ExoPlayer,
    private val advertisementManager: AdvertisementManager
) : Player.EventListener {

    val nowPlayingId = MutableStateFlow<String?>(null)
    val currentItems = MutableStateFlow<List<String>>(emptyList())

    init {
        exoPlayer.addListener(this)
    }

    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
        super.onMediaItemTransition(mediaItem, reason)
        nowPlayingId.value = mediaItem?.mediaId
    }

    override fun onTimelineChanged(timeline: Timeline, reason: Int) {
        super.onTimelineChanged(timeline, reason)
        val items = mutableListOf<String>()
        for (index in 0 until exoPlayer.mediaItemCount) {
            items.add(exoPlayer.getMediaItemAt(index).mediaId)
        }
        currentItems.value = items
    }

    fun play(list: List<MediaItem>, mediaId: String?, playWhenReady: Boolean = true) {
        if (list.isEmpty()) {
            return
        }

        val initialWindowIndex = if (mediaId == null) 0 else list.map { it.mediaId }.indexOf(mediaId)

        if (initialWindowIndex == -1) {
            return
        }

        exoPlayer.clearMediaItems()
        addAll(list)
        exoPlayer.playWhenReady = playWhenReady
        exoPlayer.prepare()
        exoPlayer.seekTo(initialWindowIndex, 0)
    }

    fun playAt(position: Int, time: Long = 0) {
        try {
            exoPlayer.seekTo(position, time)
        } catch (exception: IllegalSeekPositionException) {
            exoPlayer.seekTo(0, time)
        }
    }

    private fun addAll(list: List<MediaItem>) {
        exoPlayer.addMediaItems(list)
        advertisementManager.onPlay(list.map { it.mediaId })
    }

    fun removeAt(position: Int, onRemoveAction: (Boolean) -> Unit) {
        try {
            exoPlayer.removeMediaItem(position)
            onRemoveAction(true)
        } catch (exception: IndexOutOfBoundsException) {
            exception.printStackTrace()
            onRemoveAction(false)
        }
    }

    fun insert(position: Int, item: MediaItem) {
        try {
            exoPlayer.addMediaItem(position, item)
            advertisementManager.onInsert(position, item.mediaId)
        } catch (exception: IndexOutOfBoundsException) {
            exception.printStackTrace()
        }
    }
}
