package com.nomtek.exoplayercrash.data

import com.nomtek.exoplayercrash.models.Advertisement
import com.nomtek.exoplayercrash.models.MediaItem
import com.nomtek.exoplayercrash.models.ReportingData
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class DataRepository(
    private val json: Json,
    private val assetsFileLoader: AssetsFileLoader
) {

    private var data: List<MediaItem>? = null

    suspend fun mediaItems(): List<MediaItem> =
        data ?:
        assetsFileLoader.load(FILE_NAME)?.let {
            json.decodeFromString(MusicData.serializer(), it).music.map { data ->
                MediaItem(
                    id = data.id,
                    title = data.title,
                    displayTitle = data.artist,
                    mediaUri = data.source,
                    advertisement = data.adData?.let { adData ->
                        Advertisement(
                            id = adData.id,
                            reportingData = adData.reportingData.map { reportData ->
                                ReportingData(
                                    text = reportData.text,
                                    position = reportData.position
                                )
                            }
                        )
                    }
                )
            }
        }?.apply {
            data = this
        } ?: emptyList()

    suspend fun mediaItem(id: String): MediaItem = mediaItems().first { it.id == id }

    companion object {
        private const val FILE_NAME = "data.json"
    }
}

@Serializable
data class MusicData(
    val music: List<Data>
)

@Serializable
data class Data(
    val id: String,
    val title: String,
    val album: String,
    val artist: String,
    val genre: String,
    val source: String,
    val image: String,
    val trackNumber: Long,
    val totalTrackCount: Long,
    val duration: Long,
    val site: String,
    val adData: AdData?
)

@Serializable
data class AdData(
    val id: String,
    val reportingData: List<ReportData>
)

@Serializable
data class ReportData(
    val text: String,
    val position: Double
)
