package org.pangwali.preservation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import org.pangwali.preservation.data.db.PangwaliDatabase
import org.pangwali.preservation.data.repository.PromptRepository
import org.pangwali.preservation.data.repository.RecordingRepository
import org.pangwali.preservation.data.repository.SpeakerRepository
import org.pangwali.preservation.data.settings.SettingsRepository
import org.pangwali.preservation.ui.PangwaliViewModelFactory
import org.pangwali.preservation.ui.navigation.PangwaliNavGraph
import org.pangwali.preservation.ui.theme.PangwaliMinimalTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = PangwaliDatabase.getDatabase(this)
        val speakerRepository = SpeakerRepository(database.speakerDao())
        val recordingRepository = RecordingRepository(database.recordingDao())
        val promptRepository = PromptRepository(database.promptDao())
        val settingsRepository = SettingsRepository(this)
        val cacheDir = getExternalFilesDir("audio/master") ?: cacheDir
        val viewModelFactory = PangwaliViewModelFactory(speakerRepository, recordingRepository, promptRepository, settingsRepository, cacheDir, database)

        setContent {
            PangwaliMinimalTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    PangwaliNavGraph(
                        navController = navController,
                        viewModelFactory = viewModelFactory,
                        speakerRepository = speakerRepository,
                        recordingRepository = recordingRepository
                    )
                }
            }
        }
    }
}
