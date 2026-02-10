# Insight Launcher ProGuard Rules

# Keep Hilt generated classes
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }

# Keep Room entities
-keep class com.harmonic.insight.launcher.data.local.** { *; }

# Keep data models
-keep class com.harmonic.insight.launcher.data.model.** { *; }
