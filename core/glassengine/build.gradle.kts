plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.newlauncher.core.glassengine"
}

dependencies {
    api(project(":core:graphics"))
    api(project(":core:designsystem"))
    api(project(":core:performance"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
}
