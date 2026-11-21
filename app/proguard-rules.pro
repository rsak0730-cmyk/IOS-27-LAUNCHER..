# NEW-LAUNCHER ProGuard / R8 rules.

# Keep Kotlin metadata for reflection-free serialization (kotlinx.serialization).
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.**

# kotlinx.serialization: keep serializers and the @Serializable model classes.
-keepclassmembers class com.newlauncher.**$$serializer { *; }
-keepclasseswithmembers class com.newlauncher.core.storage.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Keep the launcher application and activity (referenced by the manifest).
-keep class com.newlauncher.app.** { *; }

# Keep the glass engine shader bridge and AGSL uniform names.
-keepclassmembers class com.newlauncher.core.glassengine.** { *; }

# Keep the notification listener service.
-keep class com.newlauncher.app.notification.** { *; }

# Compose / AndroidX generally handle their own rules; keep Compose UI entry points.
-keep class androidx.compose.** { *; }
