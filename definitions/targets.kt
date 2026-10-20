package fantasy.weapons.definitions

import fantasy.gradle.ForgeNames
import fantasy.gradle.MinecraftRuntime
import fantasy.gradle.buildTargets

private val minecraft = MinecraftRuntime("1.20.1", java = 17)

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
}
