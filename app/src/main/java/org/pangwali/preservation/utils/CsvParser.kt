package org.pangwali.preservation.utils

import android.content.Context
import android.net.Uri
import org.pangwali.preservation.data.db.PromptEntity
import org.pangwali.preservation.data.db.WordlistEntity
import java.io.BufferedReader
import java.io.InputStreamReader

object CsvParser {
    fun parseWordlistCsv(context: Context, uri: Uri): List<WordlistEntity> {
        val words = mutableListOf<String>()
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            BufferedReader(InputStreamReader(inputStream)).use { reader ->
                // Skip header if it exists
                val firstLine = reader.readLine()
                if (firstLine != null && !firstLine.contains("hindi", ignoreCase = true)) {
                    words.add(firstLine.trim())
                }
                
                var line: String? = reader.readLine()
                while (line != null) {
                    if (line.isNotBlank()) {
                        words.add(line.trim())
                    }
                    line = reader.readLine()
                }
            }
        }
        
        return words.mapIndexed { index, hindi ->
            WordlistEntity(
                id = "WRD_EXT_${System.currentTimeMillis()}_$index",
                hindiWord = hindi,
                hindiExample = null,
                targetConcept = null,
                status = "PENDING"
            )
        }
    }

    fun parsePromptsCsv(context: Context, uri: Uri): List<PromptEntity> {
        val prompts = mutableListOf<Pair<String, String>>() // Hindi, Category
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            BufferedReader(InputStreamReader(inputStream)).use { reader ->
                var line: String? = reader.readLine()
                while (line != null) {
                    if (line.isNotBlank()) {
                        val parts = line.split(",")
                        val hindi = parts[0].trim()
                        val category = if (parts.size > 1) parts[1].trim() else "General"
                        if (hindi.lowercase() != "hindi") { // Skip header if present
                            prompts.add(hindi to category)
                        }
                    }
                    line = reader.readLine()
                }
            }
        }
        
        return prompts.mapIndexed { index, pair ->
            PromptEntity(
                id = "PRM_EXT_${System.currentTimeMillis()}_$index",
                hindiText = pair.first,
                category = pair.second,
                topic = null,
                expectedSachConcept = null,
                status = "ACTIVE"
            )
        }
    }
}
