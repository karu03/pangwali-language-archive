# Gson rules
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**
-keep class com.google.gson.reflect.TypeToken
-keep class com.google.gson.stream.**
-keep class * extends com.google.gson.TypeAdapter
-keep class com.google.gson.** { *; }

# Pangwali Entities
-keep class org.pangwali.preservation.data.db.** { *; }

# Room
-dontwarn androidx.room.paging.**
