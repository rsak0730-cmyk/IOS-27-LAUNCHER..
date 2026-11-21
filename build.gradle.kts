// Top-level build file.
//
// Plugin versions are declared here (apply false) and applied per-module so
// every module uses mutually compatible, stable toolchain versions. A shared
// convention block configures compileSdk / minSdk / Java 17 for every Android
// module so the project stays consistent without repeating it in each file.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.serialization) apply false
}

subprojects {
    // Application module SDK/Java convention. We configure the AGP DSL
    // interface types (which expose compileSdk / defaultConfig) rather than
    // the raw extension classes.
    plugins.withId("com.android.application") {
        extensions.configure<com.android.build.api.dsl.ApplicationExtension>("android") {
            compileSdk = 35

            defaultConfig {
                minSdk = 26
                targetSdk = 35
            }

            compileOptions {
                sourceCompatibility = JavaVersion.VERSION_17
                targetCompatibility = JavaVersion.VERSION_17
            }
        }
    }

    // Library module SDK/Java convention.
    plugins.withId("com.android.library") {
        extensions.configure<com.android.build.api.dsl.LibraryExtension>("android") {
            compileSdk = 35

            defaultConfig {
                minSdk = 26
                targetSdk = 35
            }

            compileOptions {
                sourceCompatibility = JavaVersion.VERSION_17
                targetCompatibility = JavaVersion.VERSION_17
            }
        }
    }

    // Kotlin jvmTarget 17 for every Android module.
    plugins.withId("org.jetbrains.kotlin.android") {
        extensions.configure<org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension>("kotlin") {
            compilerOptions {
                jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
            }
        }
    }
}
