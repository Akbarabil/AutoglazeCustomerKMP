# Kotlinx Serialization (Wajib agar data API tidak null/crash)
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** { *** Companion; }
-keepclasseswithmembers class **$$serializer { *; }
-keep @kotlinx.serialization.Serializable class * { *; }

# Ktor & Coroutines (Mencegah crash saat request jaringan)
-keep class io.ktor.** { *; }
-keep class kotlinx.coroutines.** { *; }

# Koin (Mencegah Dependency Injection gagal memuat)
-keep class org.koin.** { *; }
-keepclassmembers class * { @org.koin.core.annotation.* *; }

# Application class (WAJIB: Mencegah ClassNotFoundException saat app dibuka)
-keep class com.example.autoglazecustomer.ui.MainApplication { *; }