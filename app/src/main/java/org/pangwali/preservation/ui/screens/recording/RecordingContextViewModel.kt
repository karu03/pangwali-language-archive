package org.pangwali.preservation.ui.screens.recording

import androidx.lifecycle.ViewModel
import org.pangwali.preservation.data.db.DatasetCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class RecordingContextViewModel : ViewModel() {
    private val _activePromptTitle = MutableStateFlow<String?>(null)
    val activePromptTitle = _activePromptTitle.asStateFlow()

    private val _activeCategory = MutableStateFlow(DatasetCategory.RAW_CONVERSATION)
    val activeCategory = _activeCategory.asStateFlow()

    fun setPrompt(title: String, category: DatasetCategory) {
        _activePromptTitle.value = title
        _activeCategory.value = category
    }

    fun clearPrompt() {
        _activePromptTitle.value = null
        _activeCategory.value = DatasetCategory.RAW_CONVERSATION
    }
}
