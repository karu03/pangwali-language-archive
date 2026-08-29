package org.pangwali.preservation.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SpeakerDao {
    @Query("SELECT * FROM speakers")
    fun getAllSpeakers(): Flow<List<SpeakerEntity>>

    @Query("SELECT * FROM speakers WHERE id = :id")
    suspend fun getSpeakerById(id: String): SpeakerEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSpeaker(speaker: SpeakerEntity)

    @Delete
    suspend fun deleteSpeaker(speaker: SpeakerEntity)

    @Query("SELECT COUNT(*) FROM speakers")
    fun getSpeakerCount(): Flow<Int>
}

@Dao
interface RecordingDao {
    @Query("SELECT * FROM recordings ORDER BY timestamp DESC")
    fun getAllRecordings(): Flow<List<RecordingEntity>>

    @Query("SELECT * FROM recordings WHERE speakerId = :speakerId ORDER BY timestamp DESC")
    fun getRecordingsBySpeaker(speakerId: String): Flow<List<RecordingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecording(recording: RecordingEntity)

    @Delete
    suspend fun deleteRecording(recording: RecordingEntity)

    @Query("SELECT SUM(durationMs) FROM recordings")
    fun getTotalDurationMs(): Flow<Long?>

    @Query("SELECT COUNT(*) FROM recordings WHERE category = :category")
    fun getCountByCategory(category: DatasetCategory): Flow<Int>
}

@Dao
interface PromptDao {
    @Query("SELECT * FROM prompts")
    fun getAllPrompts(): Flow<List<PromptEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrompt(prompt: PromptEntity)

    @Query("SELECT * FROM prompts WHERE id = :id")
    suspend fun getPromptById(id: String): PromptEntity?
}

@Dao
interface WordlistDao {
    @Query("SELECT * FROM wordlist")
    fun getAllWordlistItems(): Flow<List<WordlistEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWordlistItem(item: WordlistEntity)

    @Query("SELECT COUNT(*) FROM wordlist")
    fun getWordlistCount(): Flow<Int>
}

@Dao
interface SceneDao {
    @Query("SELECT * FROM scenes")
    fun getAllScenes(): Flow<List<SceneEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScene(scene: SceneEntity)
}

@Dao
interface LexiconDao {
    @Query("SELECT * FROM lexicon")
    fun getAllEntries(): Flow<List<LexiconEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLexiconEntry(entry: LexiconEntity)

    @Delete
    suspend fun deleteLexiconEntry(entry: LexiconEntity)
}
