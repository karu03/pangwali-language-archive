package org.pangwali.preservation.utils

import org.pangwali.preservation.data.db.RecordingEntity
import org.pangwali.preservation.data.db.SpeakerEntity
import java.text.SimpleDateFormat
import java.util.*

object ExportFormats {

    private val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.US)
    
    private fun getDateDisplayFormat(): SimpleDateFormat {
        return SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
    }

    /**
     * Generates a comprehensive CSV (Excel-friendly) linking speakers to their recordings.
     */
    fun generateMasterDatasetCsv(recordings: List<RecordingEntity>, speakers: List<SpeakerEntity>): String {
        val sb = StringBuilder()
        val dateFormat = getDateDisplayFormat()
        // Header
        sb.append("Recording_ID,Filename,Speaker_ID,Speaker_Name,Village,Age_Group,Variant,Category,Duration_Seconds,Date_Recorded,Transcript_Sach,Translation_Hindi,Notes,Audio_Link\n")
        
        val speakerMap = speakers.associateBy { it.id }
        
        recordings.forEach { rec ->
            val speaker = speakerMap[rec.speakerId]
            val durationSecs = String.format(Locale.US, "%.2f", rec.durationMs / 1000.0)
            val dateStr = dateFormat.format(Date(rec.timestamp))
            val audioFilename = rec.audioPath.substringAfterLast("/")
            
            // CSV Escaping
            val transcript = rec.transcriptSach?.replace("\"", "\"\"") ?: ""
            val translation = rec.translationHindi?.replace("\"", "\"\"") ?: ""
            val notes = rec.notes?.replace("\"", "\"\"") ?: ""
            val speakerName = speaker?.name?.replace("\"", "\"\"") ?: "N/A"
            
            sb.append("${rec.id},")
            sb.append("${audioFilename},")
            sb.append("${rec.speakerId},")
            sb.append("\"${speakerName}\",")
            sb.append("\"${speaker?.village ?: "N/A"}\",")
            sb.append("${speaker?.ageGroup?.name ?: "N/A"},")
            sb.append("${speaker?.variant?.name ?: "N/A"},")
            sb.append("${rec.category.name},")
            sb.append("${durationSecs},")
            sb.append("${dateStr},")
            sb.append("\"${transcript}\",")
            sb.append("\"${translation}\",")
            sb.append("\"${notes}\",")
            sb.append("./audio/${audioFilename}\n") // Relative link for the ZIP structure
        }
        return sb.toString()
    }

    /**
     * Generates a comprehensive JSON metadata file.
     */
    fun generateCompleteMetadataJson(recordings: List<RecordingEntity>, speakers: List<SpeakerEntity>): String {
        val data = mapOf(
            "export_date" to isoFormat.format(Date()),
            "total_recordings" to recordings.size,
            "total_speakers" to speakers.size,
            "speakers" to speakers,
            "recordings" to recordings
        )
        return com.google.gson.GsonBuilder().setPrettyPrinting().create().toJson(data)
    }

    fun generateManifestCsv(recordings: List<RecordingEntity>): String {
        val sb = StringBuilder()
        sb.append("id,speakerId,category,durationMs,timestamp,audioPath,transcriptSach,translationHindi,status,variant\n")
        recordings.forEach { rec ->
            sb.append("${rec.id},${rec.speakerId},${rec.category},${rec.durationMs},${rec.timestamp},${rec.audioPath.substringAfterLast("/")},\"${rec.transcriptSach ?: ""}\",\"${rec.translationHindi ?: ""}\",${rec.status},${rec.storyId ?: "N/A"}\n")
        }
        return sb.toString()
    }

    fun generateSpeakerJson(speakers: List<SpeakerEntity>): String {
        return com.google.gson.GsonBuilder().setPrettyPrinting().create().toJson(speakers)
    }
}
