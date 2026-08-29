package org.pangwali.preservation.ui.screens.recording

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import org.pangwali.preservation.audio.RecordingService
import org.pangwali.preservation.data.db.DatasetCategory
import org.pangwali.preservation.data.db.Marker
import org.pangwali.preservation.data.db.PangwaliVariant
import org.pangwali.preservation.data.db.RecordingEntity
import org.pangwali.preservation.data.db.RecordingStatus
import org.pangwali.preservation.data.db.SpeakerEntity
import org.pangwali.preservation.data.repository.RecordingRepository
import org.pangwali.preservation.data.repository.SpeakerRepository
import org.pangwali.preservation.data.settings.SettingsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalCoroutinesApi::class)
class RawRecordingViewModel(
    private val recordingRepository: RecordingRepository,
    private val speakerRepository: SpeakerRepository,
    private val settingsRepository: SettingsRepository,
    private val cacheDir: File
) : ViewModel() {

    private var recordingService: RecordingService? = null
    private val _isBound = MutableStateFlow(false)

    val isRecording = _isBound.flatMapLatest { bound ->
        if (bound) recordingService?.isRecording ?: flowOf(false) else flowOf(false)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    val isPaused = _isBound.flatMapLatest { bound ->
        if (bound) recordingService?.isPaused ?: flowOf(false) else flowOf(false)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    val timerMs = _isBound.flatMapLatest { bound ->
        if (bound) recordingService?.timerMs ?: flowOf(0L) else flowOf(0L)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0L)

    val amplitude = _isBound.flatMapLatest { bound ->
        if (bound) recordingService?.amplitude ?: flowOf(0f) else flowOf(0f)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0f)

    val speakers: StateFlow<List<SpeakerEntity>> = speakerRepository.allSpeakers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedSpeakerId = MutableStateFlow<String?>(null)
    val selectedSpeakerId = _selectedSpeakerId.asStateFlow()

    private val markers = mutableListOf<Marker>()
    private var currentSpeakerId: String? = null
    private var currentCategory: DatasetCategory? = null
    private var currentTitle: String? = null
    private var currentFile: File? = null
    private var activeRecordingId: String? = null

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as RecordingService.RecordingBinder
            recordingService = binder.getService()
            _isBound.value = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            _isBound.value = false
            recordingService = null
        }
    }

    fun bindService(context: Context) {
        val intent = Intent(context, RecordingService::class.java)
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    fun unbindService(context: Context) {
        if (_isBound.value) {
            context.unbindService(serviceConnection)
            _isBound.value = false
        }
    }

    fun selectSpeaker(id: String) {
        _selectedSpeakerId.value = id
    }

    fun startRecording(speakerId: String, category: DatasetCategory = DatasetCategory.RAW_CONVERSATION, title: String? = null) {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val sessionId = "SESS_${SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())}"
        
        viewModelScope.launch {
            val speaker = speakerRepository.getSpeakerById(speakerId)
            val variant = speaker?.variant ?: PangwaliVariant.P_SACH
            val displayName = (speaker?.name ?: speakerId).replace(" ", "_")
            
            val typeCode = when(category) {
                DatasetCategory.RAW_CONVERSATION -> "P_SACH"
                DatasetCategory.HINDI_PROMPT_RESPONSE -> "HIN_PROM_P_SACH"
                DatasetCategory.FULL_ELICITATION -> "HIN_PROM_P_SACH_HIN_TRANS"
                else -> category.name
            }
            
            val fileName = "${displayName}_${sessionId}_${typeCode}_${variant.name}_$timestamp.wav"
            val outputFile = File(cacheDir, fileName)
            currentFile = outputFile
            
            currentSpeakerId = speakerId
            currentCategory = category
            val finalTitle = title ?: "Field Take $timestamp"
            currentTitle = finalTitle
            markers.clear()
            
            // PRE-SAVE: Create a draft entry in the DB
            val draftId = "REC_${System.currentTimeMillis()}"
            activeRecordingId = draftId
            
            val draftEntity = RecordingEntity(
                id = draftId,
                speakerId = speakerId,
                sessionId = sessionId,
                category = category,
                status = RecordingStatus.RAW,
                title = finalTitle,
                durationMs = 0,
                timestamp = System.currentTimeMillis(),
                audioPath = outputFile.absolutePath,
                verified = false
            )
            recordingRepository.insertRecording(draftEntity)
            
            if (recordingService == null) {
                android.util.Log.e("RawRecordingViewModel", "RecordingService is NULL!")
            }
            
            recordingService?.startRecording(outputFile)
        }
    }

    fun togglePause() {
        if (isPaused.value) {
            recordingService?.resumeRecording()
        } else {
            recordingService?.pauseRecording()
        }
    }

    fun addMarker(label: String = "Marker") {
        if (isRecording.value) {
            markers.add(Marker(timerMs.value, label))
        }
    }

    private val _reviewRecording = MutableStateFlow<RecordingEntity?>(null)
    val reviewRecording = _reviewRecording.asStateFlow()

    fun stopRecording() {
        val finalTimerMs = timerMs.value
        val finalSpeakerId = currentSpeakerId
        val finalCategory = currentCategory
        val finalTitle = currentTitle
        val finalFile = currentFile
        val finalRecordingId = activeRecordingId
        
        recordingService?.stopRecording()
        
        viewModelScope.launch {
            delay(1000) // Give service more time to close file and update header
            
            if (finalFile != null && finalSpeakerId != null && finalCategory != null && finalRecordingId != null) {
                val entity = RecordingEntity(
                    id = finalRecordingId,
                    speakerId = finalSpeakerId,
                    sessionId = "SESS_${SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())}",
                    category = finalCategory,
                    status = RecordingStatus.RECORDED,
                    title = finalTitle,
                    durationMs = finalTimerMs,
                    timestamp = System.currentTimeMillis(),
                    audioPath = finalFile.absolutePath,
                    audioFormat = "WAV",
                    sampleRate = 48000,
                    channels = 1,
                    markers = markers.toList(),
                    verified = false
                )
                
                // AUTOSAVE: Update the entry in repository
                recordingRepository.insertRecording(entity)
                
                if (!settingsRepository.isAutoSaveEnabled.value) {
                    _reviewRecording.value = entity
                }
            }
            activeRecordingId = null
        }
    }

    fun saveReview() {
        viewModelScope.launch {
            _reviewRecording.value?.let {
                recordingRepository.insertRecording(it)
                _reviewRecording.value = null
            }
        }
    }

    fun discardReview() {
        _reviewRecording.value?.let { recording ->
            viewModelScope.launch {
                recordingRepository.deleteRecording(recording)
                if (File(recording.audioPath).exists()) {
                    File(recording.audioPath).delete()
                }
            }
            _reviewRecording.value = null
        }
    }
}
