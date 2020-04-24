package com.nomtek.exoplayercrash.extension

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

fun <T : Any, L : LiveData<T>> AppCompatActivity.observeNotNull(liveData: L, body: (T) -> Unit) {
    liveData.observe(this, Observer {
        it?.let(body)
    })
}
