package org.pangwali.preservation.data.storage

import android.content.Context
import android.util.Log
import org.pangwali.preservation.data.db.SpeakerEntity
import java.io.File

object SpeakerFolderManager {
    private const val TAG = "SpeakerFolderManager"

    fun createSpeakerFolderStructure(context: Context, speaker: SpeakerEntity): File? {
        return try {
            val baseDir = context.getExternalFilesDir("speakers") ?: context.filesDir
            val speakerDir = File(baseDir, speaker.id)
            
            if (!speakerDir.exists()) {
                speakerDir.mkdirs()
            }

            File(speakerDir, "audio/raw").mkdirs()
            File(speakerDir, "audio/prompts").mkdirs()
            File(speakerDir, "transcripts").mkdirs()

            Log.i(TAG, "Created dedicated speaker folder: ${speakerDir.absolutePath}")
            speakerDir
        } catch (e: Exception) {
            Log.e(TAG, "Failed to create speaker folder", e)
            null
        }
    }
}
