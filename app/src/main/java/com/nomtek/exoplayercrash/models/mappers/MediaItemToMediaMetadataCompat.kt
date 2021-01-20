package com.nomtek.exoplayercrash.models.mappers

import com.nomtek.exoplayercrash.models.MediaItem

class MediaItemToMediaMetadataCompat {

    fun map(from: MediaItem): com.google.android.exoplayer2.MediaItem =
        com.google.android.exoplayer2.MediaItem.Builder()
            .setMediaId(from.id)
            .setUri(from.mediaUri)
            .build()
}
