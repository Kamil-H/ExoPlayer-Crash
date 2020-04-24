package com.nomtek.exoplayercrash.extension

import android.support.v4.media.MediaDescriptionCompat
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.Timeline

@Suppress("UNCHECKED_CAST")
private fun <T : Any> Timeline.tags(shuffleModeEnabled: Boolean): List<T> {
    val tags = mutableListOf<T?>()
    val window = Timeline.Window()
    var windowIndex = getFirstWindowIndex(shuffleModeEnabled)
    while (windowIndex != C.INDEX_UNSET) {
        val tag = getWindow(windowIndex, window).tag as? T
        tags.add(tag)
        windowIndex = getNextWindowIndex(windowIndex, Player.REPEAT_MODE_OFF, shuffleModeEnabled)
    }

    return tags.filterNotNull()
}

fun Timeline.descriptionTags(shuffleModeEnabled: Boolean): List<MediaDescriptionCompat> =
    tags(shuffleModeEnabled)
