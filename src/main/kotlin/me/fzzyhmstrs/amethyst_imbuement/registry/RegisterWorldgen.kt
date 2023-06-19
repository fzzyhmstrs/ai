package me.fzzyhmstrs.amethyst_imbuement.registry

import me.fzzyhmstrs.amethyst_imbuement.AI
import net.fabricmc.fabric.api.biome.v1.BiomeModifications
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors
import net.minecraft.block.Blocks
import net.minecraft.structure.rule.BlockMatchRuleTest
import net.minecraft.structure.rule.RuleTest
import net.minecraft.util.Identifier
import net.minecraft.util.registry.BuiltinRegistries
import net.minecraft.util.registry.Registry
import net.minecraft.util.registry.RegistryEntry
import net.minecraft.util.registry.RegistryKey
import net.minecraft.world.gen.GenerationStep
import net.minecraft.world.gen.YOffset
import net.minecraft.world.gen.feature.ConfiguredFeature
import net.minecraft.world.gen.feature.Feature
import net.minecraft.world.gen.feature.OreFeatureConfig
import net.minecraft.world.gen.feature.PlacedFeature
import net.minecraft.world.gen.placementmodifier.CountPlacementModifier
import net.minecraft.world.gen.placementmodifier.HeightRangePlacementModifier
import net.minecraft.world.gen.placementmodifier.SquarePlacementModifier


object RegisterWorldgen {

    private val BLACKSTONE_RULE: RuleTest = BlockMatchRuleTest(Blocks.BLACKSTONE)
    private val BASALT_RULE: RuleTest = BlockMatchRuleTest(Blocks.BASALT)
    private val BLACKSTONE_TARGETS: List<OreFeatureConfig.Target> = listOf(OreFeatureConfig.createTarget(BLACKSTONE_RULE,RegisterBlock.TIGERS_EYE_BLACKSTONE_ORE.defaultState))
    private val BASALT_TARGETS: List<OreFeatureConfig.Target> = listOf(OreFeatureConfig.createTarget(BASALT_RULE,RegisterBlock.TIGERS_EYE_BASALT_ORE.defaultState))


    private val ORE_TIGERS_EYE_BLACKSTONE_CONFIGURED = ConfiguredFeature(Feature.ORE, OreFeatureConfig(BLACKSTONE_TARGETS, 3))
    private val ORE_TIGERS_EYE_BLACKSTONE_PLACED = PlacedFeature(RegistryEntry.of(ORE_TIGERS_EYE_BLACKSTONE_CONFIGURED),
            listOf(
                CountPlacementModifier.of(3),
                SquarePlacementModifier.of(),
                HeightRangePlacementModifier.uniform(YOffset.aboveBottom(4), YOffset.belowTop(8))
            )
        )

    private val  ORE_TIGERS_EYE_BLACKSTONE_KEY = RegistryKey.of(Registry.PLACED_FEATURE_KEY,Identifier(AI.MOD_ID,"ore_tigers_eye_blackstone"))


    private val ORE_TIGERS_EYE_BASALT_CONFIGURED = ConfiguredFeature(Feature.ORE, OreFeatureConfig(BASALT_TARGETS, 4))
    private val ORE_TIGERS_EYE_BASALT_PLACED = PlacedFeature(RegistryEntry.of(ORE_TIGERS_EYE_BASALT_CONFIGURED),
        listOf(
            CountPlacementModifier.of(3),
            SquarePlacementModifier.of(),
            HeightRangePlacementModifier.uniform(YOffset.aboveBottom(4), YOffset.fixed(60))
        )
    )

    private val  ORE_TIGERS_EYE_BASALT_KEY = RegistryKey.of(Registry.PLACED_FEATURE_KEY,Identifier(AI.MOD_ID,"ore_tigers_eye_basalt"))

    fun registerAll(){
        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, Identifier(AI.MOD_ID,"ore_tigers_eye_blackstone"),
            ORE_TIGERS_EYE_BLACKSTONE_CONFIGURED)
        Registry.register(BuiltinRegistries.PLACED_FEATURE,Identifier(AI.MOD_ID,"ore_tigers_eye_blackstone"),
            ORE_TIGERS_EYE_BLACKSTONE_PLACED)
        BiomeModifications.addFeature(BiomeSelectors.foundInTheNether(),GenerationStep.Feature.UNDERGROUND_DECORATION, ORE_TIGERS_EYE_BLACKSTONE_KEY)

        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, Identifier(AI.MOD_ID,"ore_tigers_eye_basalt"),
            ORE_TIGERS_EYE_BASALT_CONFIGURED)
        Registry.register(BuiltinRegistries.PLACED_FEATURE,Identifier(AI.MOD_ID,"ore_tigers_eye_basalt"),
            ORE_TIGERS_EYE_BASALT_PLACED)
        BiomeModifications.addFeature(BiomeSelectors.foundInTheNether(),GenerationStep.Feature.UNDERGROUND_ORES, ORE_TIGERS_EYE_BASALT_KEY)
    }

}