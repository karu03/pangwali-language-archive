package org.pangwali.preservation.ui.screens.wordlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import org.pangwali.preservation.data.db.WordlistEntity
import org.pangwali.preservation.data.repository.RecordingRepository
import org.pangwali.preservation.data.db.RecordingDao
import org.pangwali.preservation.data.db.WordlistDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

import android.content.Context
import android.net.Uri
import org.pangwali.preservation.utils.CsvParser

class WordlistViewModel(
    private val wordlistDao: WordlistDao,
    private val recordingRepository: RecordingRepository
) : ViewModel() {

    val wordlist: StateFlow<List<WordlistEntity>> = wordlistDao.getAllWordlistItems()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _currentIndex = MutableStateFlow(0)
    val currentIndex = _currentIndex.asStateFlow()

    fun importCsv(context: Context, uri: Uri) {
        viewModelScope.launch {
            val newWords = CsvParser.parseWordlistCsv(context, uri)
            newWords.forEach { wordlistDao.insertWordlistItem(it) }
        }
    }

    fun nextWord() {
        if (_currentIndex.value < wordlist.value.size - 1) {
            _currentIndex.value++
        }
    }

    fun previousWord() {
        if (_currentIndex.value > 0) {
            _currentIndex.value--
        }
    }
    
    // Initial data seeding logic could go here
    fun seedInitialWords() {
        viewModelScope.launch {
            if (wordlist.value.isEmpty()) {
                val initialWords = listOf("पानी", "खाना", "घर", "पहाड़", "नदी").mapIndexed { i, hindi ->
                    WordlistEntity(
                        id = "WRD_${String.format("%03d", i + 1)}",
                        hindiWord = hindi,
                        hindiExample = null,
                        targetConcept = null,
                        status = "PENDING"
                    )
                }
                initialWords.forEach { wordlistDao.insertWordlistItem(it) }
            }
        }
    }
}
