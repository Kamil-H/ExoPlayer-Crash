package com.nomtek.exoplayercrash.utils

import android.content.Context
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.upstream.ResolvingDataSource
import com.google.android.exoplayer2.util.Util
import com.nomtek.exoplayercrash.MainActivity
import com.nomtek.exoplayercrash.PlayerViewModel
import com.nomtek.exoplayercrash.data.AssetsFileLoader
import com.nomtek.exoplayercrash.data.DataRepository
import com.nomtek.exoplayercrash.media.*
import com.nomtek.exoplayercrash.models.mappers.MediaItemToMediaMetadataCompat
import kotlinx.serialization.json.Json

object Injector {

    private var exoPlayer: ExoPlayer? = null
    private var mediaSessionConnection: MediaSessionConnection? = null

    fun providePlayerViewModel(context: Context): PlayerViewModel.Factory {
        return PlayerViewModel.Factory(
            dataRepository = dataRepository(context),
            playbackController = playbackController(context),
            mediaItemToMediaMetadataCompat = MediaItemToMediaMetadataCompat()
        )
    }

    fun inject(mediaService: MediaService) {
        with(mediaService) {
            exoPlayer = provideExoPlayer(this)
            mediaSessionConnection = mediaSessionConnection(this)
        }
    }

    fun inject(mainActivity: MainActivity) {
        with(mainActivity) {
            exoPlayer = provideExoPlayer(this)
        }
    }

    private fun playbackController(context: Context) = PlaybackController(
        mediaPlayer = provideMediaPlayer(context),
        mediaSessionConnection = mediaSessionConnection(context)
    )

    private fun provideMediaPlayer(context: Context) = MediaPlayer(
        exoPlayer = provideExoPlayer(context),
        httpDataSourceFactory = httpDataSourceFactory(context),
        advertisementManager = AdvertisementManager(
            dataRepository = dataRepository(context),
            advertisementSetter = AdvertisementSetter(
                exoPlayer = provideExoPlayer(context)
            )
        )
    )

    private fun httpDataSourceFactory(context: Context) =
        ResolvingDataSource.Factory(DefaultHttpDataSourceFactory(provideUserAgent(context)), CustomResolver())

    private fun provideUserAgent(context: Context) = Util.getUserAgent(context, "APPLICATION_NAME")

    private fun dataRepository(context: Context) = DataRepository(
        json = Json {  },
        assetsFileLoader = AssetsFileLoader(context)
    )

    fun mediaSessionConnection(context: Context): MediaSessionConnection {
        return mediaSessionConnection ?: MediaSessionConnection(context).also {
            mediaSessionConnection = it
        }
    }

    private fun provideExoPlayer(context: Context): ExoPlayer =
        exoPlayer ?: SimpleExoPlayer.Builder(context).build().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(C.CONTENT_TYPE_MUSIC)
                    .setUsage(C.USAGE_MEDIA)
                    .build(), true
            )
        }.also { exoPlayer = it }
}
