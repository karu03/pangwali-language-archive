package org.pangwali.preservation.utils

import java.io.File
import java.io.FileOutputStream
import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.log10
import kotlin.math.sqrt

object AudioProcessor {

    /**
     * Writes the RIFF/WAVE header for a 16-bit PCM Mono file.
     */
    fun writeWavHeader(out: FileOutputStream, sampleRate: Int, channels: Int, bitsPerSample: Int) {
        val header = ByteBuffer.allocate(44).apply {
            order(ByteOrder.LITTLE_ENDIAN)
            put("RIFF".toByteArray())
            putInt(0) // ChunkSize (placeholder)
            put("WAVE".toByteArray())
            put("fmt ".toByteArray())
            putInt(16) // Subchunk1Size
            putShort(1.toShort()) // AudioFormat (PCM)
            putShort(channels.toShort())
            putInt(sampleRate)
            putInt(sampleRate * channels * (bitsPerSample / 8)) // ByteRate
            putShort((channels * (bitsPerSample / 8)).toShort()) // BlockAlign
            putShort(bitsPerSample.toShort())
            put("data".toByteArray())
            putInt(0) // Subchunk2Size (placeholder)
        }
        out.write(header.array())
    }

    /**
     * Updates the RIFF/WAVE header with correct file size after recording.
     */
    fun updateWavHeader(file: File) {
        if (!file.exists()) {
            android.util.Log.e("AudioProcessor", "File does not exist: ${file.absolutePath}")
            return
        }
        val fileSize = file.length()
        if (fileSize < 44) {
            android.util.Log.e("AudioProcessor", "File is too small for a WAV: $fileSize bytes")
            return
        }
        val dataSize = fileSize - 44
        val raf = RandomAccessFile(file, "rw")
        
        raf.seek(4)
        val riffSize = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt((fileSize - 8).toInt())
        raf.write(riffSize.array())
        
        raf.seek(40)
        val dataSizeBuf = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(dataSize.toInt())
        raf.write(dataSizeBuf.array())
        
        raf.close()
        android.util.Log.d("AudioProcessor", "WAV header updated for ${file.name}. Data size: $dataSize")
    }

    fun calculateRms(buffer: ShortArray): Double {
        var sum = 0.0
        for (sample in buffer) {
            sum += (sample.toDouble() * sample.toDouble())
        }
        return sqrt(sum / buffer.size)
    }

    fun calculateDb(rms: Double): Double {
        return if (rms > 0) 20 * log10(rms / 32767.0) else -60.0
    }
}
