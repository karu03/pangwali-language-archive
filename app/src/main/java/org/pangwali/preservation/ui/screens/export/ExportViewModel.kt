package org.pangwali.preservation.ui.screens.export

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import org.pangwali.preservation.data.repository.RecordingRepository
import org.pangwali.preservation.data.repository.SpeakerRepository
import org.pangwali.preservation.utils.ExportFormats
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.FileInputStream

import android.os.Environment
import java.text.SimpleDateFormat
import java.util.*

class ExportViewModel(
    private val speakerRepository: SpeakerRepository,
    private val recordingRepository: RecordingRepository,
    private val exportDir: File
) : ViewModel() {

    private val _isExporting = MutableStateFlow(false)
    val isExporting = _isExporting.asStateFlow()

    private val _exportStatus = MutableStateFlow<String?>(null)
    val exportStatus = _exportStatus.asStateFlow()

    fun generateExport() {
        viewModelScope.launch {
            _isExporting.value = true
            _exportStatus.value = "Starting export..."
            try {
                // Try to use public Downloads folder if available
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val targetDir = if (downloadsDir.exists()) downloadsDir else exportDir
                
                if (!targetDir.exists()) {
                    targetDir.mkdirs()
                }
                
                val speakers = speakerRepository.allSpeakers.first()
                val recordings = recordingRepository.allRecordings.first()
                
                val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss_SSS", Locale.getDefault()).format(Date())
                val zipFile = File(targetDir, "Pangwali_Dataset_$timestamp.zip")
                
                _exportStatus.value = "Preparing ${zipFile.name}..."
                
                val addedEntries = HashSet<String>()
                
                ZipOutputStream(FileOutputStream(zipFile)).use { zipOut ->
                    // Master Dataset
                    _exportStatus.value = "Writing Master Dataset..."
                    val masterCsvName = "Pangwali_Master_Dataset.csv"
                    addToZip(zipOut, masterCsvName, ExportFormats.generateMasterDatasetCsv(recordings, speakers).toByteArray())
                    addedEntries.add(masterCsvName)
                    
                    // Metadata JSON
                    _exportStatus.value = "Writing Metadata JSON..."
                    val metadataJsonName = "metadata.json"
                    addToZip(zipOut, metadataJsonName, ExportFormats.generateCompleteMetadataJson(recordings, speakers).toByteArray())
                    addedEntries.add(metadataJsonName)
                    
                    // Manifests
                    addToZip(zipOut, "manifest.csv", ExportFormats.generateManifestCsv(recordings).toByteArray())
                    addToZip(zipOut, "speakers.json", ExportFormats.generateSpeakerJson(speakers).toByteArray())
                    
                    // Audio Files
                    recordings.forEachIndexed { index, rec ->
                        val audioFile = File(rec.audioPath)
                        val entryName = "audio/${audioFile.name}"
                        
                        if (audioFile.exists() && !addedEntries.contains(entryName)) {
                            _exportStatus.value = "Adding audio ${index + 1}/${recordings.size}..."
                            val entry = ZipEntry(entryName)
                            zipOut.putNextEntry(entry)
                            FileInputStream(audioFile).use { input ->
                                input.copyTo(zipOut)
                            }
                            zipOut.closeEntry()
                            addedEntries.add(entryName)
                        }
                    }
                }
                _exportStatus.value = "Success! Saved to:\n${zipFile.absolutePath}"
            } catch (e: Exception) {
                e.printStackTrace()
                _exportStatus.value = "Export failed: ${e.message}"
            } finally {
                _isExporting.value = false
            }
        }
    }

    private fun addToZip(zipOut: ZipOutputStream, fileName: String, content: ByteArray) {
        val entry = ZipEntry(fileName)
        zipOut.putNextEntry(entry)
        zipOut.write(content)
        zipOut.closeEntry()
    }
}
