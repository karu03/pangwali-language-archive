package org.pangwali.preservation.ui.screens.speakers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import org.pangwali.preservation.data.db.AgeGroup
import org.pangwali.preservation.data.db.PangwaliVariant
import org.pangwali.preservation.data.db.SpeakerEntity
import org.pangwali.preservation.data.repository.SpeakerRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.*

import org.pangwali.preservation.data.db.RecordingEntity
import org.pangwali.preservation.data.repository.RecordingRepository

class SpeakersViewModel(
    private val speakerRepository: SpeakerRepository,
    private val recordingRepository: RecordingRepository
) : ViewModel() {

    val speakers: StateFlow<List<SpeakerEntity>> = speakerRepository.allSpeakers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recordings: StateFlow<List<RecordingEntity>> = recordingRepository.allRecordings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addSpeaker(
        name: String?,
        village: String,
        ageGroup: AgeGroup,
        variant: PangwaliVariant,
        gender: String?,
        consentGiven: Boolean
    ) {
        viewModelScope.launch {
            val count = speakers.value.size + 1
            val id = "SPK_${String.format(Locale.getDefault(), "%03d", count)}"
            
            val newSpeaker = SpeakerEntity(
                id = id,
                name = name,
                nativeName = null,
                isPrivate = true,
                village = village,
                ageGroup = ageGroup,
                variant = variant,
                gender = gender,
                consentGiven = consentGiven,
                consentDate = if (consentGiven) System.currentTimeMillis().toString() else null,
                notes = null
            )
            speakerRepository.insertSpeaker(newSpeaker)
        }
    }
}
