package com.nomtek.exoplayercrash.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.Okio
import java.nio.charset.Charset

class AssetsFileLoader constructor(private val context: Context) {

    suspend fun load(fileName: String): String? = withContext(Dispatchers.IO) {
        try {
            val input = context.assets.open(fileName)
            val source = Okio.buffer(Okio.source(input))
            source.readByteString().string(Charset.forName("utf-8"))
        } catch (exception: Exception) {
            null
        }
    }
}
