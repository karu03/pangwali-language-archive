package org.pangwali.preservation

import android.app.Application
import android.util.Log

class PangwaliApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "Language fieldwork engine initialized.")
        ensureArchivalStorageExists()
    }

    private fun ensureArchivalStorageExists() {
        val audioDir = getExternalFilesDir("audio/master")
        if (audioDir?.exists() == false) {
            audioDir.mkdirs()
        }
        val exportDir = getExternalFilesDir("exports")
        if (exportDir?.exists() == false) {
            exportDir.mkdirs()
        }
    }

    companion object {
        const val TAG = "Language Preservation"
    }
}
