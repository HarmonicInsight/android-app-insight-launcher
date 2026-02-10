# Insight Launcher ProGuard Rules

# === Kotlin ===
-dontwarn kotlin.**
-keep class kotlin.Metadata { *; }
-keepclassmembers class kotlin.Metadata {
    public <methods>;
}

# === Jetpack Compose ===
-dontwarn androidx.compose.**
-keep class androidx.compose.** { *; }
-keepclassmembers class androidx.compose.** { *; }

# Keep Compose runtime
-keep class androidx.compose.runtime.** { *; }
-keep class androidx.compose.ui.** { *; }
-keep class androidx.compose.material3.** { *; }
-keep class androidx.compose.foundation.** { *; }
-keep class androidx.compose.animation.** { *; }

# === Hilt / Dagger ===
-dontwarn dagger.**
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager { *; }
-keepclasseswithmembers class * {
    @dagger.hilt.* <methods>;
}
-keepclasseswithmembers class * {
    @javax.inject.* <fields>;
}
-keepclasseswithmembers class * {
    @javax.inject.* <init>(...);
}

# Keep @AndroidEntryPoint annotated classes
-keep @dagger.hilt.android.AndroidEntryPoint class * { *; }

# === Room ===
-keep class * extends androidx.room.RoomDatabase { *; }
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao interface * { *; }
-keepclassmembers class * {
    @androidx.room.* <methods>;
}

# === App classes ===
# Keep all data model classes
-keep class com.harmonic.insight.launcher.data.model.** { *; }

# Keep Room entities and DAOs
-keep class com.harmonic.insight.launcher.data.local.** { *; }

# Keep BroadcastReceiver
-keep class com.harmonic.insight.launcher.data.receiver.** { *; }

# === Navigation ===
-keep class androidx.navigation.** { *; }

# === Lifecycle / ViewModel ===
-keep class * extends androidx.lifecycle.ViewModel { *; }
-keep class * extends androidx.lifecycle.AndroidViewModel { *; }

# === Coroutines ===
-dontwarn kotlinx.coroutines.**
-keepclassmembers class kotlinx.coroutines.** { *; }

# === General Android ===
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes InnerClasses
-keepattributes EnclosingMethod

# Keep R classes
-keepclassmembers class **.R$* {
    public static <fields>;
}
