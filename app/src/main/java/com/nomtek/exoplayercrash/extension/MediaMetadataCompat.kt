package com.nomtek.exoplayercrash.extension

import android.net.Uri
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import androidx.core.net.toUri
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource

inline val MediaMetadataCompat.id: String?
    get() = getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)

inline val MediaMetadataCompat.title: String?
    get() = getString(MediaMetadataCompat.METADATA_KEY_TITLE)

inline val MediaMetadataCompat.displayTitle: String?
    get() = getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE)

inline val MediaMetadataCompat.mediaUri: Uri?
    get() = this.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI)?.toUri()

inline val MediaMetadataCompat.customProperty: String?
    get() = this.getString(CUSTOM_PROPERTY)

fun MediaMetadataCompat.Builder.putId(value: String): MediaMetadataCompat.Builder =
    putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, value)

fun MediaMetadataCompat.Builder.putTitle(value: String): MediaMetadataCompat.Builder =
    putString(MediaMetadataCompat.METADATA_KEY_TITLE, value)

fun MediaMetadataCompat.Builder.putMediaUr(value: String): MediaMetadataCompat.Builder =
    putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, value)

fun MediaMetadataCompat.Builder.putDisplayTitle(value: String): MediaMetadataCompat.Builder =
    putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, value)

fun MediaMetadataCompat.Builder.putCustomProperty(value: String): MediaMetadataCompat.Builder =
    putString(CUSTOM_PROPERTY, value)

fun MediaMetadataCompat.toMediaSource(dataSourceFactory: DataSource.Factory): MediaSource =
    ProgressiveMediaSource.Factory(dataSourceFactory)
        .setTag(fullDescription)
        .createMediaSource(getString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI).toUri())

inline val MediaMetadataCompat.fullDescription: MediaDescriptionCompat?
    get() = description.also {
        it.extras?.putAll(bundle)
    }

val CUSTOM_PROPERTY = "CUSTOM_PROPERTY"