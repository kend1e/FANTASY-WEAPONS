package fantasy.weapons.build

import fantasy.gradle.GenerateFiles
import fantasy.gradle.PrepareTestRun
import fantasy.gradle.Target
import fantasy.gradle.Loader
import fantasy.weapons.definitions.*
import org.gradle.api.Project
import org.gradle.api.GradleException
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.kotlin.dsl.*
import javax.xml.parsers.DocumentBuilderFactory

data class WeaponsTestRun(val report: org.gradle.api.provider.Provider<org.gradle.api.file.RegularFile>)

fun Project.configureWeaponsTests() {
    val target = extensions.getByType<Target>()
    require(target.minecraft == "1.20.1") { "No test adapter for ${target.minecraft}" }
    val fabric = target.loader == Loader.FABRIC
    val profile = providers.gradleProperty("testProfile").getOrElse("defaults")
    require(profile in setOf("defaults", "configured")) { "Unknown test profile: $profile" }
    val changed = content.weapons.first().id
    val damage = 17.0
    val speed = -1.25
    val generated = tasks.register<GenerateFiles>("generateTestResources") {
        contents.set(buildMap {
            put("weapon_test_expectations.json", json(mapOf(
                "changed" to if (profile == "configured") changed else "",
                "damage" to damage, "speed" to speed,
                "addition" to content.addition, "weapons" to content.weapons,
                "materials" to content.materials,
            )))
            put("pack.mcmeta", json(mapOf("pack" to mapOf("pack_format" to 15, "description" to "Weapon tests"))))
            if (fabric) put("fabric.mod.json", json(mapOf(
                "schemaVersion" to 1, "id" to "fantasy_weapons_tests", "version" to "1.0",
                "entrypoints" to mapOf("fabric-gametest" to listOf("net.kenddie.fantasyweapons.test.WeaponTests")),
                "depends" to mapOf(mod.id to "*"),
            ))) else put("META-INF/mods.toml", """
                modLoader="javafml"
                loaderVersion="${target.loaderRange}"
                license="DSMSLv2"
                [[mods]]
                modId="fantasy_weapons_tests"
                version="1.0"
                displayName="Fantasy Weapons Tests"
            """.trimIndent())
        })
        outputDirectory.set(layout.buildDirectory.dir("generated/resources/test"))
    }
    val sources = tasks.register<GenerateFiles>("generateTestSources") {
        contents.set(buildMap {
            val methods = weaponTests.entries.joinToString("\n") { (id, owner) ->
                val annotation = if (fabric) "@net.minecraft.gametest.framework.GameTest(template = \"fabric-gametest-api-v1:empty\")"
                    else "@net.minecraft.gametest.framework.GameTest(template = \"empty\", templateNamespace = \"fantasy_weapons_tests\")"
                "    $annotation\n    public ${if (fabric) "" else "static "}void $id(net.minecraft.gametest.framework.GameTestHelper helper) { $owner.run(helper); }"
            }
            val holder = if (fabric) "" else "@net.minecraftforge.gametest.GameTestHolder(\"fantasy_weapons_tests\")\n@net.minecraftforge.gametest.PrefixGameTestTemplate(false)"
            put("net/kenddie/fantasyweapons/test/WeaponTests.java", "package net.kenddie.fantasyweapons.test;\n$holder\npublic final class WeaponTests {\n$methods\n}")
            if (!fabric) put("net/kenddie/fantasyweapons/test/TestMod.java", """
                package net.kenddie.fantasyweapons.test;
                @net.minecraftforge.fml.common.Mod("fantasy_weapons_tests")
                public final class TestMod {
                    public TestMod() throws Exception {
                        net.minecraft.gametest.framework.GlobalTestReporter.replaceWith(
                            new net.minecraft.gametest.framework.JUnitLikeTestReporter(
                                new java.io.File(System.getProperty("fantasy_weapons.test.report"))));
                    }
                }
            """.trimIndent())
        })
        outputDirectory.set(layout.buildDirectory.dir("generated/sources/test"))
    }
    extensions.configure<SourceSetContainer> {
        val main = getByName("main")
        named("test") {
            java.srcDir(rootProject.file("tests/minecraft/${target.minecraft}/java"))
            java.srcDir(sources.flatMap { it.outputDirectory })
            resources.srcDir(generated.flatMap { it.outputDirectory })
            resources.srcDir(rootProject.file("tests/resources"))
            compileClasspath += main.output + main.compileClasspath
            runtimeClasspath += main.output + main.runtimeClasspath
        }
    }
    val configPath = if (fabric) "config/fantasy_weapons.json" else "world/serverconfig/fantasy_weapons-server.toml"
    val prepare = tasks.register<PrepareTestRun>("prepareTestRun") {
        runDirectory.set(layout.buildDirectory.dir("run/test"))
        filesToRemove.set(setOf(configPath))
        contents.set(if (profile == "defaults") emptyMap() else mapOf(configPath to if (fabric)
            json(mapOf("weapons" to mapOf(changed to mapOf("damage" to damage, "speed" to speed))))
            else "[weapons.$changed]\ndamage = $damage\nspeed = $speed\n"))
    }
    val runName = if (fabric) "runGameTest" else "runTestGameTestServer"
    val report = layout.buildDirectory.file("reports/test/$profile/results.xml")
    extensions.add("weaponsTestRun", WeaponsTestRun(report))
    val expected = weaponTests.keys.map { if (fabric) "weapontests.$it" else it }.toSet()
    tasks.matching { it.name == runName }.configureEach {
        dependsOn(prepare, "testClasses")
        outputs.upToDateWhen { false }
        doFirst {
            report.get().asFile.apply {
                parentFile.mkdirs()
                if (exists() && !delete()) throw GradleException("Cannot clear $this")
            }
        }
        doLast {
            val file = report.get().asFile
            if (!file.isFile) throw GradleException("Missing test report: $file")
            val factory = DocumentBuilderFactory.newInstance()
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true)
            val doc = factory.newDocumentBuilder().parse(file)
            val nodes = doc.getElementsByTagName("testcase")
            val actual = (0 until nodes.length).map { (nodes.item(it) as org.w3c.dom.Element).getAttribute("name") }
            val failed = listOf("failure", "error", "skipped").sumOf { doc.getElementsByTagName(it).length }
            if (actual.toSet() != expected || actual.size != expected.size || failed != 0)
                throw GradleException("${target.name}: FAIL — expected $expected, got $actual; unsuccessful $failed. See $file")
            logger.lifecycle("${target.name} ($profile): PASS — ${actual.size}/${expected.size}")
        }
    }
    tasks.named<org.gradle.api.tasks.testing.Test>("test") {
        dependsOn(runName)
        exclude("net/kenddie/fantasyweapons/test/**")
        failOnNoDiscoveredTests = false
    }
}
