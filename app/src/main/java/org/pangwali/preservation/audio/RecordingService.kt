package org.pangwali.preservation.audio

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import org.pangwali.preservation.R
import org.pangwali.preservation.utils.AudioProcessor
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

class RecordingService : Service() {

    private val binder = RecordingBinder()
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private var audioRecord: AudioRecord? = null
    private var recordingJob: Job? = null
    
    private val _isRecording = MutableStateFlow(false)
    val isRecording = _isRecording.asStateFlow()

    private val _isPaused = MutableStateFlow(false)
    val isPaused = _isPaused.asStateFlow()

    private val _timerMs = MutableStateFlow(0L)
    val timerMs = _timerMs.asStateFlow()

    private val _amplitude = MutableStateFlow(0f)
    val amplitude = _amplitude.asStateFlow()

    private val sampleRate = 48000
    private val channelConfig = AudioFormat.CHANNEL_IN_MONO
    private val audioFormat = AudioFormat.ENCODING_PCM_16BIT
    private val bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)

    private var currentOutputFile: File? = null
    private var startTime = 0L
    private var pausedTotalTime = 0L

    inner class RecordingBinder : Binder() {
        fun getService(): RecordingService = this@RecordingService
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    @SuppressLint("MissingPermission")
    fun startRecording(outputFile: File) {
        if (_isRecording.value) return
        
        android.util.Log.d("RecordingService", "Starting recording to: ${outputFile.absolutePath}")
        currentOutputFile = outputFile
        
        try {
            audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                sampleRate,
                channelConfig,
                audioFormat,
                bufferSize
            )

            if (audioRecord?.state != AudioRecord.STATE_INITIALIZED) {
                android.util.Log.e("RecordingService", "AudioRecord initialization failed")
                return
            }

            val fos = FileOutputStream(currentOutputFile)
            AudioProcessor.writeWavHeader(fos, sampleRate, 1, 16)

            _isRecording.value = true
            _isPaused.value = false
            _timerMs.value = 0L
            startTime = System.currentTimeMillis()
            pausedTotalTime = 0L
            
            startForeground(NOTIFICATION_ID, createNotification())
            audioRecord?.startRecording()
            
            if (audioRecord?.recordingState != AudioRecord.RECORDSTATE_RECORDING) {
                android.util.Log.e("RecordingService", "AudioRecord failed to start recording")
                _isRecording.value = false
                return
            }

            recordingJob = serviceScope.launch {
                val buffer = ShortArray(bufferSize)
                var totalBytesWritten = 0L
                try {
                    while (_isRecording.value) {
                        if (!_isPaused.value) {
                            val read = audioRecord?.read(buffer, 0, bufferSize) ?: 0
                            if (read > 0) {
                                val byteBuffer = ByteBuffer.allocate(read * 2).order(ByteOrder.LITTLE_ENDIAN)
                                for (i in 0 until read) {
                                    byteBuffer.putShort(buffer[i])
                                }
                                fos.write(byteBuffer.array())
                                totalBytesWritten += (read * 2)
                                _amplitude.value = (AudioProcessor.calculateRms(buffer) / 32767.0).toFloat()
                            }
                            _timerMs.value = System.currentTimeMillis() - startTime - pausedTotalTime
                        } else {
                            val pauseStart = System.currentTimeMillis()
                            while (_isPaused.value && _isRecording.value) {
                                delay(100)
                            }
                            pausedTotalTime += (System.currentTimeMillis() - pauseStart)
                        }
                    }
                } finally {
                    fos.flush()
                    fos.close()
                    android.util.Log.d("RecordingService", "Recording finished. Total bytes written: $totalBytesWritten")
                    currentOutputFile?.let { AudioProcessor.updateWavHeader(it) }
                    stopForeground(STOP_FOREGROUND_REMOVE)
                    stopSelf()
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("RecordingService", "Error during recording", e)
            _isRecording.value = false
        }
    }

    fun pauseRecording() {
        _isPaused.value = true
    }

    fun resumeRecording() {
        _isPaused.value = false
    }

    fun stopRecording() {
        _isRecording.value = false
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Field Recorder",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Recording Pangwali")
            .setContentText("Field recording in progress...")
            .setSmallIcon(android.R.drawable.ic_btn_speak_now)
            .setOngoing(true)
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        audioRecord?.release()
    }

    companion object {
        private const val CHANNEL_ID = "recording_channel"
        private const val NOTIFICATION_ID = 1
    }
}
