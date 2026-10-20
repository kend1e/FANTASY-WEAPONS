package fantasy.weapons.definitions

import fantasy.gradle.ForgeNames
import fantasy.gradle.buildTargets

private val minecraft = minecraftVersions.getValue("1.20.1").runtime
private val minecraft1211 = minecraftVersions.getValue("1.21.1").runtime

val targets = buildTargets(releaseVersion = "0.4") {
    forge(minecraft, "47.3.0") {
        minecraftRange = "[1.20.1,1.21)"
        names = ForgeNames.SRG
    }
    fabric(minecraft, "0.16.7") {
        minecraftRange = "~1.20.1"
        fabricApi("0.92.2+1.20.1")
        clothConfig("11.1.136")
    }
    forge(minecraft1211, "52.1.3") {
        minecraftRange = "[1.21.1,1.21.2)"
    }
    neoforge(minecraft1211, "21.1.206") {
        minecraftRange = "[1.21.1,1.21.2)"
    }
    fabric(minecraft1211, "0.18.2") {
        minecraftRange = "~1.21.1"
        fabricApi("0.116.7+1.21.1")
        clothConfig("15.0.140")
    }
}
