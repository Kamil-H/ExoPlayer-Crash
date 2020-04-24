package com.nomtek.exoplayercrash.models

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar

data class SnackbarMessage(
    val text: String,
    val buttonText: String,
    val dismissTime: Long,
    val buttonCallback: () -> Unit
)

fun AppCompatActivity.show(message: SnackbarMessage, anchorView: View) {
    val view = window.decorView.rootView
    Snackbar.make(view, message.text, message.dismissTime.toInt()).apply {
        setAction(message.buttonText) {  }

        addCallback(object : Snackbar.Callback() {
            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                if (event == DISMISS_EVENT_ACTION) {
                    message.buttonCallback()
                }
                removeCallback(this)
            }
        })

        this.anchorView = anchorView
        show()
    }
}
