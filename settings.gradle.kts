rootProject.name = "CMP-9545-Reproducer"

/******************************************************************************************************************
Typesafe Accessors: 	  Access project deps via "projects.myprojectname" instead of via string declaration
https://docs.gradle.org/current/userguide/declaring_dependencies_basics.html#sec:type-safe-project-accessors
 ******************************************************************************************************************/
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
                includeGroupByRegex("android.*")
            }
        }
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
                includeGroupByRegex("android.*")
            }
        }
        mavenCentral()

        maven {
            url = uri("https://jitpack.io")
        }
    }
}

include("src:ui")
include("src:vs-code-extension")
