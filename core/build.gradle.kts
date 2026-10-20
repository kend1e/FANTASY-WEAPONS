plugins { id("fantasy.weapons.core") }

repositories { mavenCentral() }

dependencies {
    compileOnly(libs.gson)
    testImplementation(libs.gson)
    testImplementation(libs.junit)
}
