package fantasy.weapons.definitions

import fantasy.gradle.buildTargets

val targets = buildTargets(releaseVersion = "1.0") {
    forge("1.19.4")
    forge("1.20.1")
    fabric("1.20.1") {
        fabricApi()
        clothConfig()
    }
    forge("1.21.1")
    neoforge("1.21.1")
    fabric("1.21.1") {
        fabricApi()
        clothConfig()
    }
    forge("1.21.10")
    neoforge("1.21.10")
    fabric("1.21.10") {
        fabricApi()
        clothConfig()
    }
    forge("1.21.11")
    neoforge("1.21.11")
    fabric("1.21.11") {
        fabricApi()
        clothConfig()
    }
}
