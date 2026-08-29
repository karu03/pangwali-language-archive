package org.pangwali.preservation.ui.screens.recording

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import org.pangwali.preservation.data.db.PromptEntity
import org.pangwali.preservation.data.repository.PromptRepository
import org.pangwali.preservation.data.repository.RecordingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import java.io.File

class PromptRecordingViewModel(
    private val promptRepository: PromptRepository,
    private val recordingRepository: RecordingRepository,
    private val cacheDir: File
) : ViewModel() {

    val prompts: StateFlow<List<PromptEntity>> = promptRepository.allPrompts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _currentIndex = MutableStateFlow(0)
    val currentIndex = _currentIndex.asStateFlow()

    fun nextPrompt() {
        if (_currentIndex.value < prompts.value.size - 1) {
            _currentIndex.value++
        }
    }

    fun previousPrompt() {
        if (_currentIndex.value > 0) {
            _currentIndex.value--
        }
    }
}
