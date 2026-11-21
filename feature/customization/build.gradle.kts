plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.newlauncher.feature.customization"
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

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
}
