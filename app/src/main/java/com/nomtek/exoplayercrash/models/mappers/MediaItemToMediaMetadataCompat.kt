package com.nomtek.exoplayercrash.models.mappers

import android.support.v4.media.MediaMetadataCompat
import com.nomtek.exoplayercrash.extension.*
import com.nomtek.exoplayercrash.models.MediaItem

class MediaItemToMediaMetadataCompat {

    fun map(from: MediaItem): MediaMetadataCompat =
        MediaMetadataCompat.Builder().apply {
            putId(from.id)
            putTitle(from.title)
            putDisplayTitle(from.displayTitle)
            putMediaUr(from.mediaUri)
            putCustomProperty("VALUE")
        }.build()
}
