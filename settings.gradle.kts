pluginManagement {
    repositories {
        google()
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

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "GSYGithubAppCompose"
include(":app")
include(":core:network")
include(":core:database")
include(":core:common")
include(":core:ui")
include(":data")
include(":feature:welcome")
include(":feature:login")
include(":feature:home")
include(":feature:dynamic")
include(":feature:profile")
include(":feature:trending")
include(":feature:search")
include(":feature:detail")
include(":feature:code")
 