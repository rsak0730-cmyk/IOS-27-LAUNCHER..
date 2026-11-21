plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.newlauncher.app"

    defaultConfig {
        applicationId = "com.newlauncher.android"
        versionCode = 2
        versionName = "2.0"

        // Launcher target devices: Samsung Galaxy A14 5G (Android 13) and
        // OPPO K14x 5G (Android 13+). minSdk 26 keeps the launcher usable on
        // the broad Android 8.0+ population while Compose + glass features are
        // available through the AGSL/RenderEffect code paths.
        minSdk = 26
        targetSdk = 35
    }

    buildTypes {
        debug {
            // Debug builds stay debuggable and non-minified for development.
            isMinifyEnabled = false
            isDebuggable = true
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Feature modules
    implementation(project(":feature:home"))
    implementation(project(":feature:appdrawer"))
    implementation(project(":feature:widgets"))
    implementation(project(":feature:dynamicisland"))
    implementation(project(":feature:controlcenter"))
    implementation(project(":feature:customization"))

    // Core modules
    implementation(project(":core:common"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:glassengine"))
    implementation(project(":core:graphics"))
    implementation(project(":core:performance"))
    implementation(project(":core:storage"))

    // AndroidX / Compose
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.kotlinx.serialization.json)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose)
    debugImplementation(libs.androidx.compose.ui.tooling)

    // Unit tests
    testImplementation(libs.bundles.test)
    testImplementation(libs.androidx.test.core)

    // Instrumented / UI tests
    androidTestImplementation(libs.bundles.androidTest)
    androidTestImplementation(libs.bundles.composeTest)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
}
