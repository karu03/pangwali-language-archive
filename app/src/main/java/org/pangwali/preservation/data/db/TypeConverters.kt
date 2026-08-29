package org.pangwali.preservation.data.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromStringList(value: List<String>): String = gson.toJson(value)

    @TypeConverter
    fun toStringList(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun fromMarkerList(value: List<Marker>): String = gson.toJson(value)

    @TypeConverter
    fun toMarkerList(value: String): List<Marker> {
        val listType = object : TypeToken<List<Marker>>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun fromAgeGroup(value: AgeGroup): String = value.name

    @TypeConverter
    fun toAgeGroup(value: String): AgeGroup = AgeGroup.valueOf(value)

    @TypeConverter
    fun fromRecordingStatus(value: RecordingStatus): String = value.name

    @TypeConverter
    fun toRecordingStatus(value: String): RecordingStatus = RecordingStatus.valueOf(value)

    @TypeConverter
    fun fromDatasetCategory(value: DatasetCategory): String = value.name

    @TypeConverter
    fun toDatasetCategory(value: String): DatasetCategory = DatasetCategory.valueOf(value)

    @TypeConverter
    fun fromPangwaliVariant(value: PangwaliVariant): String = value.name

    @TypeConverter
    fun toPangwaliVariant(value: String): PangwaliVariant = PangwaliVariant.valueOf(value)
}
