package org.pangwali.preservation.ui.screens.prompts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.content.Context
import android.net.Uri
import org.pangwali.preservation.data.db.PromptEntity
import org.pangwali.preservation.data.repository.PromptRepository
import org.pangwali.preservation.utils.CsvParser
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PromptsViewModel(
    private val promptRepository: PromptRepository
) : ViewModel() {

    val prompts: StateFlow<List<PromptEntity>> = promptRepository.allPrompts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            if (prompts.value.isEmpty()) {
                promptRepository.seedDefaultPrompts()
            }
        }
    }

    fun addPrompt(hindi: String, category: String) {
        viewModelScope.launch {
            val count = prompts.value.size + 1
            val id = "PRM_${String.format("%03d", count)}"
            val newPrompt = PromptEntity(id, hindi, category, null, null, "ACTIVE")
            promptRepository.insertPrompt(newPrompt)
        }
    }

    fun importCsv(context: Context, uri: Uri) {
        viewModelScope.launch {
            val newPrompts = CsvParser.parsePromptsCsv(context, uri)
            newPrompts.forEach { promptRepository.insertPrompt(it) }
        }
    }
}
