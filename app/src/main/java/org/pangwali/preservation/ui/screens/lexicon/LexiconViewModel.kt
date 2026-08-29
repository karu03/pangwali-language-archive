package org.pangwali.preservation.ui.screens.lexicon

import androidx.lifecycle.ViewModel
import org.pangwali.preservation.data.db.LexiconDao
import org.pangwali.preservation.data.db.LexiconEntity
import kotlinx.coroutines.flow.Flow

class LexiconViewModel(private val lexiconDao: LexiconDao) : ViewModel() {
    val allEntries: Flow<List<LexiconEntity>> = lexiconDao.getAllEntries() // Need to add this to DAO
}
