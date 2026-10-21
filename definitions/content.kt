package fantasy.weapons.definitions

import fantasy.gradle.content.weapons.*
import fantasy.gradle.content.ShapedRecipe

val content = contentCatalog(
    namespace = mod.id,
    tab = "fw_tab",
    tabIcon = "weapon_eclipse_soldier_spear",
    base = "minecraft:netherite_sword",
    addition = "${mod.id}:blazing_stone",
) {
    material("blazing_stone", ShapedRecipe(
        pattern = listOf("Y#Y", "#X#", "Y#Y"),
        ingredients = mapOf('#' to "minecraft:blaze_powder", 'Y' to "minecraft:glowstone_dust", 'X' to "minecraft:stone"),
        count = 2,
    ))

    weapon("weapon_fates_beacon", template = "minecraft:glowstone_dust", largeModel = LargeModel.DOUBLE)
    weapon("weapon_dragonslayer_halberd", template = "minecraft:nether_wart")
    weapon("weapon_eclipse_soldier_spear", template = "minecraft:gold_nugget")
    weapon("weapon_hero_claymore", template = "minecraft:iron_ingot")
    weapon("weapon_thief_dagger", template = "minecraft:leather", largeModel = null)
    weapon("weapon_golden_horns_battleaxe", template = "minecraft:flint")
    weapon("weapon_wandering_wizard_sword", template = "minecraft:spider_eye")
    weapon("weapon_dark_lord_sword", template = "minecraft:bone")
    weapon("weapon_sunset_wings_claymore", template = "minecraft:gunpowder")
    weapon("weapon_chess_board_knight_polearm", template = "minecraft:honeycomb")
    weapon("weapon_hefty_club", template = "minecraft:oak_log", largeModel = LargeModel.DOUBLE)
    weapon("weapon_golden_halberd", template = "minecraft:gold_block")
    weapon("weapon_old_broadsword", template = "minecraft:granite", largeModel = LargeModel.DOUBLE)
    weapon("weapon_old_dagger", template = "minecraft:diorite", largeModel = null)
    weapon("weapon_old_blade", template = "minecraft:andesite", largeModel = LargeModel.DOUBLE)
    weapon("weapon_kingslayer", template = "minecraft:raw_copper")
    weapon("weapon_cursed_blade", template = "minecraft:echo_shard")
    weapon("weapon_fury_of_the_storm", template = "minecraft:nautilus_shell")
    weapon("weapon_dragons_doom", template = "minecraft:dragon_breath")
    weapon("weapon_crimson_reaper", template = "minecraft:nether_wart_block")
    weapon("weapon_sharp_sword", template = "minecraft:string")
    weapon("weapon_infected_sharp_sword", template = "minecraft:nether_wart", base = "${mod.id}:weapon_sharp_sword")
    weapon("weapon_sharp_curved_sword", template = "minecraft:rotten_flesh", largeModel = LargeModel.DOUBLE)
    weapon("weapon_infected_sharp_curved_sword", template = "minecraft:nether_wart", largeModel = LargeModel.DOUBLE, base = "${mod.id}:weapon_sharp_curved_sword")
    weapon("weapon_sharp_spear", template = "minecraft:ghast_tear")
    weapon("weapon_infected_sharp_spear", template = "minecraft:nether_wart", base = "${mod.id}:weapon_sharp_spear")
    weapon("weapon_black_katana", template = "minecraft:pufferfish")
    weapon("weapon_rivers_of_blood", template = "minecraft:magma_cream")
    weapon("weapon_wakizashi", template = "minecraft:cherry_log", largeModel = null)
    weapon("weapon_marai_executioner_sword", template = "minecraft:redstone_block")
    weapon("weapon_nagakiba", template = "minecraft:chorus_fruit")
    weapon("weapon_hand_of_malenia", template = "minecraft:golden_apple")
    weapon("weapon_spider_fang", template = "minecraft:fermented_spider_eye")
    weapon("weapon_volcano_flame", template = "minecraft:magma_block")
    weapon("weapon_savage_spear", template = "minecraft:bowl", largeModel = LargeModel.DOUBLE)
    weapon("weapon_savage_dagger", template = "minecraft:slime_ball", largeModel = null)
    weapon("weapon_savage_sword", template = "minecraft:stick", largeModel = LargeModel.DOUBLE)
    weapon("weapon_savage_hammer", template = "minecraft:rabbit_foot", largeModel = LargeModel.DOUBLE)
    weapon("weapon_fog_guard_sword", template = "minecraft:soul_lantern")
    weapon("weapon_dark_cover_sword", template = "minecraft:coal")
    weapon("weapon_golden_execution_battleaxe", template = "minecraft:ender_pearl")
    weapon("weapon_forgotten_trace_spear", template = "minecraft:prismarine_crystals")
    weapon("weapon_spark_of_dawn_polearm", template = "minecraft:amethyst_shard")
    weapon("weapon_marika_hammer", template = "minecraft:diamond")
    weapon("weapon_desert_wind", template = "minecraft:sand", largeModel = LargeModel.DOUBLE)
    weapon("weapon_phantom_needle", template = "minecraft:basalt", largeModel = LargeModel.DOUBLE)
    weapon("weapon_estoc", template = "minecraft:iron_nugget", largeModel = LargeModel.DOUBLE)
    weapon("weapon_fog_guard_dagger", template = "minecraft:soul_torch", largeModel = null)
    weapon("weapon_great_katana", template = "minecraft:bamboo")
    weapon("weapon_soldier_greatspear", template = "minecraft:stone_bricks")
    weapon("weapon_misericorde", template = "minecraft:feather", largeModel = null)
    weapon("weapon_knight_greatsword", template = "minecraft:iron_bars")
    weapon("weapon_banished_knight_greatsword", template = "minecraft:nether_bricks")
    weapon("weapon_dark_moon_greatsword", template = "minecraft:blue_ice")
    weapon("weapon_zweihander", template = "minecraft:anvil")
    weapon("weapon_watchdog_greatsword", template = "minecraft:bell")
    weapon("weapon_royal_greatsword", template = "minecraft:emerald_block")
    weapon("weapon_crucible_greatsword", template = "minecraft:blaze_rod")
    weapon("weapon_generic_rapier", template = "minecraft:paper", largeModel = LargeModel.DOUBLE)
    weapon("weapon_knight_halberd", template = "minecraft:iron_block")
    weapon("weapon_dancing_blade_of_ranah", template = "minecraft:sea_pickle")
}
