package me.fzzyhmstrs.amethyst_imbuement.registry

import me.fzzyhmstrs.amethyst_imbuement.AI
import net.fabricmc.fabric.api.biome.v1.BiomeModifications
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.util.Identifier
import net.minecraft.world.gen.GenerationStep

object RegisterWorldgen {

    val TIGERS_EYE_BLACKSTONE_PLACED_KEY = RegistryKey.of(RegistryKeys.PLACED_FEATURE, AI.identity("ore_tigers_eye_blackstone"))
    val TIGERS_EYE_BASALT_PLACED_KEY = RegistryKey.of(RegistryKeys.PLACED_FEATURE, AI.identity("ore_tigers_eye_basalt"))

    fun registerAll(){
        BiomeModifications.addFeature(BiomeSelectors.foundInTheNether(),GenerationStep.Feature.UNDERGROUND_DECORATION, TIGERS_EYE_BLACKSTONE_PLACED_KEY)
        BiomeModifications.addFeature(BiomeSelectors.foundInTheNether(),GenerationStep.Feature.UNDERGROUND_ORES, TIGERS_EYE_BASALT_PLACED_KEY)
    }

}