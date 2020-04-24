package com.nomtek.exoplayercrash.utils

import android.util.Log
import com.nomtek.exoplayercrash.data.DataRepository
import com.nomtek.exoplayercrash.models.Advertisement
import com.nomtek.exoplayercrash.models.MediaItem
import com.nomtek.exoplayercrash.models.ReportingData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class AdvertisementManager constructor(
    private val dataRepository: DataRepository,
    private val advertisementSetter: AdvertisementSetter
) {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    init {
        observeListenedAds()
    }

    private fun observeListenedAds() {
        advertisementSetter.listenedAdvertisement
            .onEach(::sendListenedAdvertisementEvent)
            .launchIn(scope)
    }

    fun onPlay(itemIds: List<String>) {
        scope.launch { onNewPlaylist(itemIds) }
    }

    fun onInsert(position: Int, id: String) {
        scope.launch {
            val mediaItem = dataRepository.mediaItem(id)
            tryAddAdvertisement(position, mediaItem)
        }
    }

    private suspend fun sendListenedAdvertisementEvent(data: Pair<Advertisement, ReportingData>) {
        Log.i("AdvertisementManager", "delivered text: ${data.second.text}")
    }

    private suspend fun onNewPlaylist(ids: List<String>) {
        ids
            .map { dataRepository.mediaItem(it) }
            .forEachIndexed { index, mediaItem -> tryAddAdvertisement(index, mediaItem) }
    }

    private suspend fun tryAddAdvertisement(index: Int, mediaItem: MediaItem) {
        val advertisement = mediaItem.advertisement ?: return
        advertisement.reportingData.forEach {
            advertisementSetter.set(advertisement, it, index)
        }
    }
}

