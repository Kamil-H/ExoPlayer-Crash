package com.nomtek.exoplayercrash

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import com.nomtek.exoplayercrash.utils.Injector

class Application : Application() {

    override fun onCreate() {
        super.onCreate()

        Injector.mediaSessionConnection(this).connect()

        AndroidThreeTen.init(this)
    }
}
