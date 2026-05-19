# ProGuard / R8 rules
# Conserver les annotations Moshi (serialisation reseau)
-keepclassmembers class * {
    @com.squareup.moshi.FromJson <methods>;
    @com.squareup.moshi.ToJson <methods>;
}
-keep @com.squareup.moshi.JsonClass class *

# Hilt
-keep class dagger.hilt.** { *; }
-keep class * extends dagger.hilt.android.HiltAndroidApp

# Kotlin
-keepclassmembers class kotlin.Metadata { *; }
