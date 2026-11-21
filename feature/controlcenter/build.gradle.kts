plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.newlauncher.feature.controlcenter"
}

dependencies {
    api(project(":core:common"))
    api(project(":core:designsystem"))
    api(project(":core:glassengine"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose)
}
