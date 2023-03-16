package me.fzzyhmstrs.amethyst_imbuement.config

object AiConfigDefaults {

    internal val enabledEnchantments: Map<String,Boolean> = mapOf(
        "heroic" to true,
        "wasting" to true,
        "deadly_shot" to true,
        "puncturing" to true,
        "insight" to true,
        "lifesteal" to true,
        "decayed" to true,
        "contaminated" to true,
        "cleaving" to true,
        "bulwark" to true,
        "multi_jump" to true,
        "night_vision" to true,
        "steadfast" to true,
        "rain_of_thorns" to true,
        "vein_miner" to true
    )
    
    internal val enchantmentCosts: Map<String,Int> = mapOf(
        "heroic" to 1,
        "wasting" to 1,
        "deadly_shot" to 1,
        "puncturing" to 1,
        "insight" to 1,
        "lifesteal" to 1,
        "decayed" to 1,
        "contaminated" to 1,
        "cleaving" to 1,
        "bulwark" to 1,
        "multi_jump" to 1,
        "night_vision" to 1,
        "steadfast" to 1,
        "rain_of_thorns" to 1,
        "vein_miner" to 1
    )

    internal val enabledAugments: Map<String,Boolean> = mapOf(
        "angelic" to true,
        "crystalline" to true,
        "draconic_vision" to true,
        "escape" to true,
        "feline" to true,
        "friendly" to true,
        "guardian" to true,
        "hasting" to true,
        "headhunter" to true,
        "healthy" to true,
        "illuminating" to true,
        "immunity" to true,
        "invisibility" to true,
        "leaping" to true,
        "lightfooted" to true,
        "lucky" to true,
        "moonlit" to true,
        "resilience" to true,
        "shielding" to true,
        "slimy" to true,
        "soulbinding" to true,
        "soul_of_the_conduit" to true,
        "spectral_vision" to true,
        "spiked" to true,
        "strength" to true,
        "striding" to true,
        "suntouched" to true,
        "swiftness" to true,
        "undying" to true
    )
}
