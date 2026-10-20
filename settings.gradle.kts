pluginManagement {
    includeBuild("../fantasy-build")
    includeBuild("build-logic")
    repositories {
        gradlePluginPortal()
        maven("https://maven.minecraftforge.net/")
        maven("https://maven.fabricmc.net/")
        mavenCentral()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
    id("fantasy.weapons.settings")
}
