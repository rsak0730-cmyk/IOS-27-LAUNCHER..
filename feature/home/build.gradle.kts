plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.newlauncher.feature.home"

    testOptions {
        unitTests {
            // Required so Robolectric can access app resources (themes, drawables,
            // Compose test manifest) when running Compose UI tests on the JVM.
            isIncludeAndroidResources = true
        }
    }
}

dependencies {
    api(project(":core:common"))
    api(project(":core:designsystem"))
    api(project(":core:glassengine"))
    api(project(":core:performance"))
    api(project(":core:storage"))

    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose)

    testImplementation(libs.bundles.test)
    testImplementation(libs.bundles.composeTest)
    testImplementation(libs.androidx.test.core)
    testImplementation(libs.androidx.test.ext.junit)
}
