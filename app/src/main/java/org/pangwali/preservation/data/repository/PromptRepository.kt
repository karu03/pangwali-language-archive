package org.pangwali.preservation.data.repository

import org.pangwali.preservation.data.db.PromptDao
import org.pangwali.preservation.data.db.PromptEntity
import kotlinx.coroutines.flow.Flow

class PromptRepository(private val promptDao: PromptDao) {
    val allPrompts: Flow<List<PromptEntity>> = promptDao.getAllPrompts()

    suspend fun insertPrompt(prompt: PromptEntity) {
        promptDao.insertPrompt(prompt)
    }

    suspend fun getPromptById(id: String): PromptEntity? {
        return promptDao.getPromptById(id)
    }
    
    // Default research prompts
    suspend fun seedDefaultPrompts() {
        val defaults = listOf(
            PromptEntity("PRM_001", "तुम कहाँ जा रहे हो?", "Conversation", "Greeting", "तूँ काईं गोता असां?", "ACTIVE"),
            PromptEntity("PRM_002", "मैं खाना खा रहा हूँ।", "Daily Life", "Action", "मीं खाणा खादा असां।", "ACTIVE"),
            PromptEntity("PRM_003", "आपका नाम क्या है?", "Information", "Intro", "तेरा नां के असा?", "ACTIVE"),
            PromptEntity("PRM_004", "कल बारिश हुई थी।", "Weather", "Past", "काल झड़ी लगी थी।", "ACTIVE"),
            PromptEntity("PRM_005", "यह रास्ता कहाँ जाता है?", "Direction", "Travel", "यो पेड़ा काईं गोता?", "ACTIVE")
        )
        defaults.forEach { promptDao.insertPrompt(it) }
    }
}
