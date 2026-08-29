package org.pangwali.preservation.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        SpeakerEntity::class,
        RecordingEntity::class,
        LexiconEntity::class,
        PromptEntity::class,
        WordlistEntity::class,
        SceneEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class PangwaliDatabase : RoomDatabase() {
    abstract fun speakerDao(): SpeakerDao
    abstract fun recordingDao(): RecordingDao
    abstract fun lexiconDao(): LexiconDao
    abstract fun promptDao(): PromptDao
    abstract fun wordlistDao(): WordlistDao
    abstract fun sceneDao(): SceneDao

    companion object {
        @Volatile
        private var INSTANCE: PangwaliDatabase? = null

        fun getDatabase(context: Context): PangwaliDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PangwaliDatabase::class.java,
                    "pangwali_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
