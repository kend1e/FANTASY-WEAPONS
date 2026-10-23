plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
    maven("https://maven.fabricmc.net/")
    maven("https://maven.minecraftforge.net/")
}

dependencies {
    implementation(libs.fantasy.build)
    implementation(libs.fantasy.fabric)
    implementation(libs.fantasy.forge)
    implementation(libs.fantasy.neoforge)
    implementation(libs.gson)
    testImplementation(libs.junit)
}

kotlin {
    jvmToolchain(25)
    sourceSets.main { kotlin.srcDir("../definitions") }
}

gradlePlugin {
    plugins {
        register("core") {
            id = "fantasy.weapons.core"
            implementationClass = "fantasy.weapons.build.WeaponsCorePlugin"
        }
        register("settings") {
            id = "fantasy.weapons.settings"
            implementationClass = "fantasy.weapons.build.WeaponsSettingsPlugin"
        }
        register("platform") {
            id = "fantasy.weapons.platform"
            implementationClass = "fantasy.weapons.build.WeaponsPlatformPlugin"
        }
    }
}

tasks.test {
    useJUnit()
    inputs.dir("../resources")
    systemProperty("staticResourcesDirectory", layout.projectDirectory.dir("../resources").asFile.absolutePath)
}
