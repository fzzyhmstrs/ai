package me.fzzyhmstrs.amethyst_imbuement.config

object AiConfigDefaults {

    internal val enabledEnchantments: Map<String,Boolean> = mapOf(
        "amethyst_imbuement:cleaving" to true,
        "amethyst_imbuement:heroic" to true,
        "amethyst_imbuement:wasting" to true,
        "amethyst_imbuement:deadly_shot" to true,
        "amethyst_imbuement:puncturing" to true,
        "amethyst_imbuement:insight" to true,
        "amethyst_imbuement:lifesteal" to true,
        "amethyst_imbuement:decayed" to true,
        "amethyst_imbuement:contaminated" to true,
        "amethyst_imbuement:multi_jump" to true,
        "amethyst_imbuement:night_vision" to true,
        "amethyst_imbuement:steadfast" to true,
        "amethyst_imbuement:rain_of_thorns" to true,
        "amethyst_imbuement:vein_miner" to true
    )
    
    internal val aiEnchantmentMaxLevels: Map<String,Int> = mapOf(
        "amethyst_imbuement:cleaving" to 3,
        "amethyst_imbuement:contaminated" to 1,
        "amethyst_imbuement:deadly_shot" to 3,
        "amethyst_imbuement:decayed" to 1,
        "amethyst_imbuement:heroic" to 7,
        "amethyst_imbuement:insight" to 3,
        "amethyst_imbuement:lifesteal" to 3,
        "amethyst_imbuement:multi_jump" to 1,
        "amethyst_imbuement:night_vision" to 1,
        "amethyst_imbuement:puncturing" to 6,
        "amethyst_imbuement:rain_of_thorns" to 3,
        "amethyst_imbuement:steadfast" to 3,
        "amethyst_imbuement:vein_miner" to 3,
        "amethyst_imbuement:wasting" to 4
    )

    internal val vanillaEnchantmentMaxLevels: Map<String,Int> = mapOf(
        "minecraft:bane_of_arthropods" to 7,
        "minecraft:blast_protection" to 5,
        "minecraft:efficiency" to 6,
        "minecraft:feather_falling" to 5,
        "minecraft:fire_aspect" to 5,
        "minecraft:fire_protection" to 5,
        "minecraft:fortune" to 5,
        "minecraft:knockback" to 5,
        "minecraft:looting" to 5,
        "minecraft:loyalty" to 5,
        "minecraft:lure" to 4,
        "minecraft:piercing" to 6,
        "minecraft:power" to 5,
        "minecraft:protection" to 5,
        "minecraft:quick_charge" to 4,
        "minecraft:respiration" to 5,
        "minecraft:sharpness" to 7,
        "minecraft:smite" to 7,
        "minecraft:sweeping" to 5,
        "minecraft:thorns" to 5,
        "minecraft:unbreaking" to 5
    )

    internal val enabledAugments: Map<String,Boolean> = mapOf(
        "amethyst_imbuement:angelic" to true,
        "amethyst_imbuement:bulwark" to true,
        "amethyst_imbuement:crystalline" to true,
        "amethyst_imbuement:draconic_vision" to true,
        "amethyst_imbuement:escape" to true,
        "amethyst_imbuement:feline" to true,
        "amethyst_imbuement:friendly" to true,
        "amethyst_imbuement:guardian" to true,
        "amethyst_imbuement:hasting" to true,
        "amethyst_imbuement:headhunter" to true,
        "amethyst_imbuement:healthy" to true,
        "amethyst_imbuement:illuminating" to true,
        "amethyst_imbuement:immunity" to true,
        "amethyst_imbuement:invisibility" to true,
        "amethyst_imbuement:leaping" to true,
        "amethyst_imbuement:lightfooted" to true,
        "amethyst_imbuement:lucky" to true,
        "amethyst_imbuement:moonlit" to true,
        "amethyst_imbuement:resilience" to true,
        "amethyst_imbuement:shielding" to true,
        "amethyst_imbuement:slimy" to true,
        "amethyst_imbuement:soulbinding" to true,
        "amethyst_imbuement:soul_of_the_conduit" to true,
        "amethyst_imbuement:spectral_vision" to true,
        "amethyst_imbuement:spiked" to true,
        "amethyst_imbuement:strength" to true,
        "amethyst_imbuement:striding" to true,
        "amethyst_imbuement:suntouched" to true,
        "amethyst_imbuement:swiftness" to true,
        "amethyst_imbuement:undying" to true
    )
}
