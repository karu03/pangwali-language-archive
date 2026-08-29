package org.pangwali.preservation.data.repository

import org.pangwali.preservation.data.db.DatasetCategory
import org.pangwali.preservation.data.db.RecordingDao
import org.pangwali.preservation.data.db.RecordingEntity
import kotlinx.coroutines.flow.Flow

class RecordingRepository(private val recordingDao: RecordingDao) {
    val allRecordings: Flow<List<RecordingEntity>> = recordingDao.getAllRecordings()

    suspend fun insertRecording(recording: RecordingEntity) {
        recordingDao.insertRecording(recording)
    }

    suspend fun deleteRecording(recording: RecordingEntity) {
        recordingDao.deleteRecording(recording)
    }

    fun getTotalDurationMs(): Flow<Long?> {
        return recordingDao.getTotalDurationMs()
    }

    fun getCountByCategory(category: DatasetCategory): Flow<Int> {
        return recordingDao.getCountByCategory(category)
    }

    fun getRecordingsBySpeaker(speakerId: String): Flow<List<RecordingEntity>> {
        return recordingDao.getRecordingsBySpeaker(speakerId)
    }
}
