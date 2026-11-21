pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "new-launcher-android"

include(":app")

include(":core:common")
include(":core:designsystem")
include(":core:glassengine")
include(":core:graphics")
include(":core:performance")
include(":core:storage")

include(":feature:home")
include(":feature:appdrawer")
include(":feature:widgets")
include(":feature:dynamicisland")
include(":feature:controlcenter")
include(":feature:customization")
