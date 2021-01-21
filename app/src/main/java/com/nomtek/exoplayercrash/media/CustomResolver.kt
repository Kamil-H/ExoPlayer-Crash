package com.nomtek.exoplayercrash.media

import android.net.Uri
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.ResolvingDataSource

class CustomResolver : ResolvingDataSource.Resolver {
    override fun resolveDataSpec(dataSpec: DataSpec): DataSpec {
        return DataSpec(
            Uri.parse("https://storageaudiobursts.blob.core.windows.net/stream/4c473e62-524d-4482-b2e6-36983b2fa25e.m3u8")
//            Uri.parse("https://storage.googleapis.com/uamp/The_Kyoto_Connection_-_Wake_Up/01_-_Intro_-_The_Way_Of_Waking_Up_feat_Alan_Watts.mp3")
        )
    }
}