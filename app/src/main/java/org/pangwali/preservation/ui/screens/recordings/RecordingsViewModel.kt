package org.pangwali.preservation.ui.screens.recordings

import android.media.MediaPlayer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import org.pangwali.preservation.data.db.RecordingEntity
import org.pangwali.preservation.data.repository.RecordingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File

class RecordingsViewModel(
    private val recordingRepository: RecordingRepository
) : ViewModel() {

    val recordings: StateFlow<List<RecordingEntity>> = recordingRepository.allRecordings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _currentlyPlayingPath = MutableStateFlow<String?>(null)
    val currentlyPlayingPath = _currentlyPlayingPath.asStateFlow()

    private var mediaPlayer: MediaPlayer? = null

    fun playRecording(path: String) {
        if (_currentlyPlayingPath.value == path) {
            stopPlayback()
            return
        }

        stopPlayback()
        
        try {
            mediaPlayer = MediaPlayer().apply {
                setDataSource(path)
                prepare()
                start()
                setOnCompletionListener {
                    _currentlyPlayingPath.value = null
                }
            }
            _currentlyPlayingPath.value = path
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun stopPlayback() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        _currentlyPlayingPath.value = null
    }

    override fun onCleared() {
        super.onCleared()
        stopPlayback()
    }
}
