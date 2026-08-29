package org.pangwali.preservation.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class AgeGroup {
    UNDER_30, AGE_30_50, OVER_50, UNKNOWN
}

enum class RecordingStatus {
    RAW, RECORDED, NEEDS_REVIEW, READY_FOR_TRANSCRIPTION, TRANSCRIBED, TRANSLATED, VALIDATED, EXPORTED
}

enum class DatasetCategory {
    RAW_CONVERSATION,           // Raw Sach
    HINDI_PROMPT_RESPONSE,      // Hindi Prompt + Raw Sach Response
    FULL_ELICITATION,           // Hindi Prompt + Sach Response + Hindi Translation
    SCENE_DESCRIPTION,
    STORYTELLING,
    WORDLIST,
    OTHER
}

data class Marker(
    val timestampMs: Long,
    val label: String
)

enum class PangwaliVariant {
    P_SACH,
    P_KILLAR,
    P_PURTHI,
    P_DHARWASI
}

@Entity(tableName = "speakers")
data class SpeakerEntity(
    @PrimaryKey val id: String, // SPK_001 format
    val name: String?,
    val nativeName: String?,
    val isPrivate: Boolean = true,
    val village: String,
    val nativeLanguage: String = "Sach",
    val variant: PangwaliVariant = PangwaliVariant.P_SACH,
    val otherLanguages: List<String> = emptyList(),
    val ageGroup: AgeGroup,
    val gender: String?,
    val consentGiven: Boolean,
    val consentDate: String?,
    val notes: String?,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "recordings")
data class RecordingEntity(
    @PrimaryKey val id: String, // REC_TIMESTAMP format
    val speakerId: String,
    val sessionId: String,
    val category: DatasetCategory,
    val status: RecordingStatus = RecordingStatus.RAW,
    
    // Linkages
    val promptId: String? = null,
    val wordId: String? = null,
    val sceneId: String? = null,
    val storyId: String? = null,
    
    // Metadata
    val title: String?,
    val durationMs: Long,
    val timestamp: Long,
    val audioPath: String,
    val audioFormat: String = "WAV",
    val sampleRate: Int = 48000,
    val channels: Int = 1,
    
    // Annotations
    val transcriptSach: String? = null,
    val translationHindi: String? = null,
    val notes: String? = null,
    val markers: List<Marker> = emptyList(),
    
    val verified: Boolean = false
)

@Entity(tableName = "prompts")
data class PromptEntity(
    @PrimaryKey val id: String, // PRM_001
    val hindiText: String,
    val category: String,
    val topic: String?,
    val expectedSachConcept: String?,
    val status: String?
)

@Entity(tableName = "wordlist")
data class WordlistEntity(
    @PrimaryKey val id: String, // WRD_001
    val hindiWord: String,
    val hindiExample: String?,
    val targetConcept: String?,
    val status: String?
)

@Entity(tableName = "scenes")
data class SceneEntity(
    @PrimaryKey val id: String, // SCN_001
    val imagePath: String?,
    val hindiPrompt: String,
    val category: String?,
    val notes: String?
)

@Entity(tableName = "lexicon")
data class LexiconEntity(
    @PrimaryKey val id: String,
    val pangwali: String,
    val ipa: String,
    val hindi: String,
    val english: String,
    val partOfSpeech: String,
    val category: String,
    val dialectNotes: String,
    val villageOrigin: String?,
    val examplePangwali: String?,
    val exampleHindi: String?,
    val exampleEnglish: String?,
    val custom: Boolean = false
)
