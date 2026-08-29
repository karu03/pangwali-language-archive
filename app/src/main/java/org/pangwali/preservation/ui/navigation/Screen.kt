package org.pangwali.preservation.ui.navigation

sealed class Screen(val route: String, val title: String) {
    object Home : Screen("home", "Preservation")
    object RawRecording : Screen("raw_recording", "Raw Recorder")
    object PromptRecording : Screen("prompt_recording", "Prompt Translation")
    object Recordings : Screen("recordings", "Recordings")
    object Speakers : Screen("speakers", "Speakers")
    object Lexicon : Screen("lexicon", "Lexicon")
    object Map : Screen("map", "Map")
    object Export : Screen("export", "Export")
}
