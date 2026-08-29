package org.pangwali.preservation.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import org.pangwali.preservation.data.repository.RecordingRepository
import org.pangwali.preservation.data.repository.SpeakerRepository
import org.pangwali.preservation.data.settings.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class SpeakerSummary(
    val id: String,
    val name: String?,
    val durationMs: Long
)

data class HomeUiState(
    val speakerCount: Int = 0,
    val recordingCount: Int = 0,
    val totalDurationMs: Long = 0,
    val hindiPromptProgress: Float = 0f,
    val naturalSpeechProgress: Float = 0f,
    val recentSpeakers: List<SpeakerSummary> = emptyList(),
    val isAutoSaveEnabled: Boolean = true,
    val isOnline: Boolean = false
)

class HomeViewModel(
    private val speakerRepository: SpeakerRepository,
    private val recordingRepository: RecordingRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                speakerRepository.allSpeakers,
                recordingRepository.allRecordings,
                recordingRepository.getTotalDurationMs(),
                settingsRepository.isAutoSaveEnabled
            ) { speakers, recordings, totalDuration, autoSave ->
                val promptCount = recordings.count { it.category == org.pangwali.preservation.data.db.DatasetCategory.HINDI_PROMPT_RESPONSE || it.category == org.pangwali.preservation.data.db.DatasetCategory.FULL_ELICITATION }
                val naturalCount = recordings.count { 
                    it.category == org.pangwali.preservation.data.db.DatasetCategory.STORYTELLING || 
                    it.category == org.pangwali.preservation.data.db.DatasetCategory.RAW_CONVERSATION 
                }
                
                val recentSpeakers = speakers.take(3).map { speaker ->
                    val speakerRecordings = recordings.filter { it.speakerId == speaker.id }
                    SpeakerSummary(
                        id = speaker.id,
                        name = speaker.name,
                        durationMs = speakerRecordings.sumOf { it.durationMs }
                    )
                }
                
                HomeUiState(
                    speakerCount = speakers.size,
                    recordingCount = recordings.size,
                    totalDurationMs = totalDuration ?: 0L,
                    hindiPromptProgress = if (recordings.isNotEmpty()) promptCount.toFloat() / recordings.size else 0f,
                    naturalSpeechProgress = if (recordings.isNotEmpty()) naturalCount.toFloat() / recordings.size else 0f,
                    recentSpeakers = recentSpeakers,
                    isAutoSaveEnabled = autoSave,
                    isOnline = true
                )
            }.collect {
                _uiState.value = it
            }
        }
    }

    fun toggleAutoSave(enabled: Boolean) {
        settingsRepository.setAutoSaveEnabled(enabled)
    }
}
