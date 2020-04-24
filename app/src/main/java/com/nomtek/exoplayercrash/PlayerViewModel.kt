package com.nomtek.exoplayercrash

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.nomtek.exoplayercrash.custom_views.PlaylistItem
import com.nomtek.exoplayercrash.data.DataRepository
import com.nomtek.exoplayercrash.media.PlaybackController
import com.nomtek.exoplayercrash.models.MediaItem
import com.nomtek.exoplayercrash.models.SnackbarMessage
import com.nomtek.exoplayercrash.models.mappers.MediaItemToMediaMetadataCompat
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val dataRepository: DataRepository,
    private val playbackController: PlaybackController,
    private val mediaItemToMediaMetadataCompat: MediaItemToMediaMetadataCompat
) : ViewModel() {

    val items = MediatorLiveData<List<PlaylistItem>>()
    val snackbarMessage = MediatorLiveData<SnackbarMessage>()

    init {
        playbackController.prepared
            .onEach { loadData() }
            .launchIn(viewModelScope)
        playbackController.currentPlaylist
            .map { it.map { id -> dataRepository.mediaItem(id) } }
            .onEach { items.value = items(it) }
            .launchIn(viewModelScope)
        playbackController.nowPlaying
            .onEach { updateItems(it) }
            .launchIn(viewModelScope)
    }

    fun loadData() {
        viewModelScope.launch {
            val mediaItems = dataRepository.mediaItems().map(mediaItemToMediaMetadataCompat::map)
            playbackController.prepareAndPlay(null, mediaItems)
        }
    }

    fun onClicked(position: Int) {
        playbackController.playAt(position)
    }

    private fun items(mediaItems: List<MediaItem>) = mediaItems.map { mediaItem ->
        PlaylistItem(
            id = mediaItem.id,
            title = mediaItem.title,
            subtitle = mediaItem.displayTitle,
            isPlaying = false,
            isRightText = mediaItem.advertisement != null
        )
    }

    private fun updateItems(nowPlayingId: String) {
        items.value = items.value?.map { playlistItem ->
            playlistItem.copy(isPlaying = playlistItem.id == nowPlayingId)
        }
    }

    fun swiped(position: Int) {
        viewModelScope.launch {
            val id = items.value?.get(position)?.id ?: return@launch
            if (!playbackController.removeAt(position)) {
                return@launch
            }
            val mediaItem = mediaItemToMediaMetadataCompat.map(dataRepository.mediaItem(id))
            snackbarMessage.value = SnackbarMessage(
                text = "Media item has been removed",
                buttonText = "Undo",
                dismissTime = 5000,
                buttonCallback = { playbackController.insert(position, mediaItem) }
            )
        }
    }

    class Factory(
        private val dataRepository: DataRepository,
        private val playbackController: PlaybackController,
        private val mediaItemToMediaMetadataCompat: MediaItemToMediaMetadataCompat
    ) : ViewModelProvider.NewInstanceFactory() {

        @Suppress("unchecked_cast")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return PlayerViewModel(
                dataRepository = dataRepository,
                playbackController = playbackController,
                mediaItemToMediaMetadataCompat = mediaItemToMediaMetadataCompat
            ) as T
        }
    }
}
