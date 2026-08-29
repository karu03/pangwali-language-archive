package org.pangwali.preservation.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import org.pangwali.preservation.ui.PangwaliViewModelFactory
import org.pangwali.preservation.ui.screens.home.HomeScreen
import org.pangwali.preservation.ui.screens.home.HomeViewModel
import org.pangwali.preservation.ui.screens.prompts.PromptsScreen
import org.pangwali.preservation.ui.screens.prompts.PromptsViewModel
import org.pangwali.preservation.ui.screens.lexicon.LexiconBrowser
import org.pangwali.preservation.ui.screens.lexicon.LexiconViewModel
import org.pangwali.preservation.ui.screens.recording.PromptRecordingScreen
import org.pangwali.preservation.ui.screens.recording.PromptRecordingViewModel
import org.pangwali.preservation.ui.screens.recording.RawRecordingScreen
import org.pangwali.preservation.ui.screens.recording.RawRecordingViewModel
import org.pangwali.preservation.ui.screens.recording.RecordingContextViewModel

import org.pangwali.preservation.data.repository.RecordingRepository
import org.pangwali.preservation.data.repository.SpeakerRepository
import org.pangwali.preservation.ui.screens.recordings.RecordingsScreen
import org.pangwali.preservation.ui.screens.recordings.RecordingsViewModel
import org.pangwali.preservation.ui.screens.speakers.SpeakersScreen
import org.pangwali.preservation.ui.screens.speakers.SpeakersViewModel
import org.pangwali.preservation.ui.screens.wordlist.WordlistScreen
import org.pangwali.preservation.ui.screens.wordlist.WordlistViewModel

import org.pangwali.preservation.ui.screens.map.PangiValleyMap

import org.pangwali.preservation.ui.screens.export.ExportScreen
import org.pangwali.preservation.ui.screens.export.ExportViewModel

@Composable
fun PangwaliNavGraph(
    navController: NavHostController,
    viewModelFactory: PangwaliViewModelFactory,
    speakerRepository: SpeakerRepository,
    recordingRepository: RecordingRepository,
    modifier: Modifier = Modifier
) {
    val contextViewModel: RecordingContextViewModel = viewModel(factory = viewModelFactory)

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        composable(Screen.Home.route) {
            val homeViewModel: HomeViewModel = viewModel(factory = viewModelFactory)
            HomeScreen(
                onNavigateToRawRecording = { navController.navigate(Screen.RawRecording.route) },
                onNavigateToPromptRecording = { navController.navigate(Screen.PromptRecording.route) },
                onNavigateToRecordings = { navController.navigate(Screen.Recordings.route) },
                onNavigateToSpeakers = { navController.navigate(Screen.Speakers.route) },
                onNavigateToExport = { navController.navigate(Screen.Export.route) },
                viewModel = homeViewModel
            )
        }
        composable(Screen.RawRecording.route) {
            val rawViewModel: RawRecordingViewModel = viewModel(factory = viewModelFactory)
            RawRecordingScreen(
                onNavigateBack = { 
                    contextViewModel.clearPrompt()
                    navController.popBackStack() 
                },
                viewModel = rawViewModel,
                contextViewModel = contextViewModel
            )
        }
        composable(Screen.PromptRecording.route) {
            val promptsViewModel: PromptsViewModel = viewModel(factory = viewModelFactory)
            PromptsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToRecording = { promptId, hindiText ->
                    contextViewModel.setPrompt(hindiText, org.pangwali.preservation.data.db.DatasetCategory.HINDI_PROMPT_RESPONSE)
                    navController.navigate(Screen.RawRecording.route)
                },
                viewModel = promptsViewModel
            )
        }
        composable(Screen.Lexicon.route) {
            val wordlistViewModel: WordlistViewModel = viewModel(factory = viewModelFactory)
            WordlistScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToRecording = { wordId ->
                    // Navigate to recording with pre-filled word ID
                    navController.navigate(Screen.RawRecording.route) 
                },
                viewModel = wordlistViewModel
            )
        }
        composable(Screen.Recordings.route) {
            val recordingsViewModel: RecordingsViewModel = viewModel(factory = viewModelFactory)
            RecordingsScreen(
                onNavigateBack = { navController.popBackStack() },
                viewModel = recordingsViewModel
            )
        }
        composable(Screen.Speakers.route) {
            val speakersViewModel: SpeakersViewModel = viewModel(factory = viewModelFactory)
            SpeakersScreen(
                onNavigateBack = { navController.popBackStack() },
                viewModel = speakersViewModel
            )
        }
        composable(Screen.Map.route) {
            PangiValleyMap(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Export.route) {
            val exportViewModel: ExportViewModel = viewModel(factory = viewModelFactory)
            ExportScreen(
                onNavigateBack = { navController.popBackStack() },
                viewModel = exportViewModel
            )
        }
    }
}
