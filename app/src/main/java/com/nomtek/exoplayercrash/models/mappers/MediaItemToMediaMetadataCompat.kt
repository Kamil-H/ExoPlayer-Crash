package com.nomtek.exoplayercrash.models.mappers

import android.support.v4.media.MediaMetadataCompat
import com.nomtek.exoplayercrash.extension.putDisplayTitle
import com.nomtek.exoplayercrash.extension.putId
import com.nomtek.exoplayercrash.extension.putMediaUr
import com.nomtek.exoplayercrash.extension.putTitle
import com.nomtek.exoplayercrash.models.MediaItem

class MediaItemToMediaMetadataCompat {

    fun map(from: MediaItem): MediaMetadataCompat =
        MediaMetadataCompat.Builder().apply {
            putId(from.id)
            putTitle(from.title)
            putDisplayTitle(from.displayTitle)
            putMediaUr(from.mediaUri)
        }.build()
}
