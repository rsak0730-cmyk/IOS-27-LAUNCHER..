// Top-level build file.
// Plugin versions are declared here (apply false) and applied per-module so that
// every module uses mutually compatible, stable toolchain versions.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.serialization) apply false
}

// Project-wide conventions that are safe to share without referencing AGP DSL types.
allprojects {
    // Keep generated build artifacts out of source control and out of the diff.
    tasks.matching { it.name.startsWith("lint") }.configureEach {
        // lint is run explicitly in CI; nothing to configure globally here.
    }
}
