package org.pangwali.preservation.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.pangwali.preservation.data.db.PangwaliDatabase
import org.pangwali.preservation.data.repository.RecordingRepository
import org.pangwali.preservation.data.repository.SpeakerRepository
import org.pangwali.preservation.data.repository.PromptRepository
import org.pangwali.preservation.data.settings.SettingsRepository
import org.pangwali.preservation.ui.screens.home.HomeViewModel
import org.pangwali.preservation.ui.screens.lexicon.LexiconViewModel
import org.pangwali.preservation.ui.screens.recording.PromptRecordingViewModel
import org.pangwali.preservation.ui.screens.recording.RecordingContextViewModel
import org.pangwali.preservation.ui.screens.recording.RawRecordingViewModel
import org.pangwali.preservation.ui.screens.export.ExportViewModel
import org.pangwali.preservation.ui.screens.prompts.PromptsViewModel
import org.pangwali.preservation.ui.screens.recordings.RecordingsViewModel
import org.pangwali.preservation.ui.screens.speakers.SpeakersViewModel
import org.pangwali.preservation.ui.screens.wordlist.WordlistViewModel
import java.io.File

class PangwaliViewModelFactory(
    private val speakerRepository: SpeakerRepository,
    private val recordingRepository: RecordingRepository,
    private val promptRepository: PromptRepository,
    private val settingsRepository: SettingsRepository,
    private val cacheDir: File,
    private val database: PangwaliDatabase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(speakerRepository, recordingRepository, settingsRepository) as T
            }
            modelClass.isAssignableFrom(RecordingContextViewModel::class.java) -> {
                RecordingContextViewModel() as T
            }
            modelClass.isAssignableFrom(RawRecordingViewModel::class.java) -> {
                RawRecordingViewModel(recordingRepository, speakerRepository, settingsRepository, cacheDir) as T
            }
            modelClass.isAssignableFrom(SpeakersViewModel::class.java) -> {
                SpeakersViewModel(speakerRepository, recordingRepository) as T
            }
            modelClass.isAssignableFrom(RecordingsViewModel::class.java) -> {
                RecordingsViewModel(recordingRepository) as T
            }
            modelClass.isAssignableFrom(PromptsViewModel::class.java) -> {
                PromptsViewModel(promptRepository) as T
            }
            modelClass.isAssignableFrom(WordlistViewModel::class.java) -> {
                WordlistViewModel(database.wordlistDao(), recordingRepository) as T
            }
            modelClass.isAssignableFrom(ExportViewModel::class.java) -> {
                val exportDir = cacheDir.parentFile?.let { File(it, "exports") } ?: cacheDir
                ExportViewModel(speakerRepository, recordingRepository, exportDir) as T
            }
            modelClass.isAssignableFrom(PromptRecordingViewModel::class.java) -> {
                PromptRecordingViewModel(promptRepository, recordingRepository, cacheDir) as T
            }
            modelClass.isAssignableFrom(LexiconViewModel::class.java) -> {
                LexiconViewModel(database.lexiconDao()) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
