package fantasy.weapons.definitions

import fantasy.gradle.buildTargets

val targets = buildTargets(releaseVersion = "1.0") {
    forge("1.20.1") {
        minecraftRange = "[1.20.1,1.21)"
    }
    fabric("1.20.1") {
        minecraftRange = "~1.20.1"
        fabricApi()
        clothConfig()
    }
    forge("1.21.1") {
        minecraftRange = "[1.21.1,1.21.2)"
    }
    neoforge("1.21.1") {
        minecraftRange = "[1.21.1,1.21.2)"
    }
    fabric("1.21.1") {
        minecraftRange = "~1.21.1"
        fabricApi()
        clothConfig()
    }
}
