package org.pangwali.preservation.data.repository

import org.pangwali.preservation.data.db.SpeakerDao
import org.pangwali.preservation.data.db.SpeakerEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SpeakerRepository(private val speakerDao: SpeakerDao) {
    val allSpeakers: Flow<List<SpeakerEntity>> = speakerDao.getAllSpeakers()
    val speakerCount: Flow<Int> = speakerDao.getSpeakerCount()
    
    private val _activeSpeakerId = MutableStateFlow<String?>(null)
    val activeSpeakerId: StateFlow<String?> = _activeSpeakerId.asStateFlow()

    fun setActiveSpeaker(id: String) {
        _activeSpeakerId.value = id
    }

    suspend fun getSpeakerById(id: String): SpeakerEntity? {
        return speakerDao.getSpeakerById(id)
    }

    suspend fun insertSpeaker(speaker: SpeakerEntity) {
        speakerDao.insertSpeaker(speaker)
    }

    suspend fun deleteSpeaker(speaker: SpeakerEntity) {
        speakerDao.deleteSpeaker(speaker)
    }
}
