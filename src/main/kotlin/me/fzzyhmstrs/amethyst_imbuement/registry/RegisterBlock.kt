package me.fzzyhmstrs.amethyst_imbuement.registry

import me.fzzyhmstrs.amethyst_core.registry.RegisterAttribute
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.block.*
import me.fzzyhmstrs.amethyst_imbuement.item.SpellcastersReagentBlockItem
import me.fzzyhmstrs.amethyst_imbuement.particle.ColoredEndParticleEffect
import me.fzzyhmstrs.fzzy_core.coding_util.FzzyBlockSettings
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.minecraft.block.*
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.sound.BlockSoundGroup
import net.minecraft.util.DyeColor
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.intprovider.UniformIntProvider
import net.minecraft.world.BlockView
import java.util.*

@Suppress("MemberVisibilityCanBePrivate")
object RegisterBlock {

    internal val regBlock: MutableMap<String, Block> = mutableMapOf()

    val IMBUING_TABLE = ImbuingTableBlock(FzzyBlockSettings.basic().mapColor(MapColor.YELLOW).requiresTool().strength(5.0f, 1200.0f).luminance(10)).also { regBlock["imbuing_table"] = it }
    val DISENCHANTING_TABLE = DisenchantingTableBlock(FzzyBlockSettings.basic().mapColor(MapColor.YELLOW).requiresTool().strength(5.0f, 1200.0f).luminance(10)).also { regBlock["disenchanting_table"] = it }
    val ALTAR_OF_EXPERIENCE = AltarOfExperienceBlock(FzzyBlockSettings.basic().mapColor(MapColor.YELLOW).requiresTool().strength(5.0f, 1200.0f).luminance(10)).also { regBlock["altar_of_experience"] = it }
    val CRYSTAL_ALTAR = CrystalAltarBlock(FzzyBlockSettings.burn().mapColor(MapColor.OFF_WHITE).strength(2.5f).sounds(BlockSoundGroup.WOOD)).also { regBlock["crystal_altar"] = it }
    val EXPERIENCE_BUSH = ExperienceBushBlock(FzzyBlockSettings.nonSolidLightDestroyMove().mapColor(MapColor.LICHEN_GREEN).ticksRandomly().noCollision().sounds(BlockSoundGroup.SWEET_BERRY_BUSH)).also { regBlock["experience_bush"] = it }
    val CRYSTALLINE_CORE_BLOCK = AmethystBlock(FzzyBlockSettings.basic().mapColor(MapColor.PURPLE).strength(4.0f).requiresTool()).also { regBlock["crystalline_core"] = it }
    val WARDING_CANDLE = WardingCandleBlock(FzzyBlockSettings.nonSolidLightDestroyMove().mapColor(MapColor.OFF_WHITE).strength(0.1f).sounds(BlockSoundGroup.CANDLE).luminance(WardingCandleBlock.STATE_TO_LUMINANCE)).also { regBlock["warding_candle"] = it }
    val WARDING_CANDLE_ITEM = SpellcastersReagentBlockItem(
        EntityAttributes.GENERIC_ARMOR_TOUGHNESS,
        EntityAttributeModifier(UUID.fromString("279ce6aa-d1c6-11ed-afa1-0242ac120002"),"warding_modifier",0.5,EntityAttributeModifier.Operation.ADDITION),
        WARDING_CANDLE,
        FabricItemSettings()
    )
    val STEEL_BLOCK = Block(FzzyBlockSettings.basic().mapColor(MapColor.IRON_GRAY).requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL)).also { regBlock["steel_block"] = it }
    val STEEL_BLOCK_ITEM = SpellcastersReagentBlockItem(
        EntityAttributes.GENERIC_ARMOR,
        EntityAttributeModifier(UUID.fromString("75c099f6-ce58-11ed-afa1-0242ac120002"),"steel_modifier",0.75,EntityAttributeModifier.Operation.ADDITION),
        STEEL_BLOCK,
        FabricItemSettings()
    )
    val BERYL_COPPER_BLOCK = Block(FzzyBlockSettings.basic().mapColor(MapColor.ORANGE).requiresTool().strength(3.0f, 6.0f).sounds(BlockSoundGroup.COPPER)).also { regBlock["beryl_copper_block"] = it }
    val BERYL_COPPER_BLOCK_ITEM = SpellcastersReagentBlockItem(
        EntityAttributes.GENERIC_ATTACK_SPEED,
        EntityAttributeModifier(UUID.fromString("75c099f6-ce58-11ed-afa1-0242ac120002"),"beryl_modifier",0.05,EntityAttributeModifier.Operation.MULTIPLY_TOTAL),
        BERYL_COPPER_BLOCK,
        FabricItemSettings()
    )
    val CUT_BERYL_COPPER_BLOCK = Block(FzzyBlockSettings.basic().mapColor(MapColor.ORANGE).requiresTool().strength(3.0f, 6.0f).sounds(BlockSoundGroup.COPPER)).also { regBlock["cut_beryl_copper_block"] = it }
    val CUT_BERYL_COPPER_SLAB = SlabBlock(FzzyBlockSettings.basic().mapColor(MapColor.ORANGE).requiresTool().strength(3.0f, 6.0f).sounds(BlockSoundGroup.COPPER)).also { regBlock["cut_beryl_copper_slab"] = it }
    val CUT_BERYL_COPPER_STAIRS = StairsBlock(CUT_BERYL_COPPER_BLOCK.defaultState,FzzyBlockSettings.basic().mapColor(MapColor.ORANGE).requiresTool().strength(3.0f, 6.0f).sounds(BlockSoundGroup.COPPER)).also { regBlock["cut_beryl_copper_stairs"] = it }
    val POLISHED_AMETHYST_BLOCK = AmethystBlock(FzzyBlockSettings.basic().mapColor(MapColor.PURPLE).strength(4.0f).requiresTool()).also { regBlock["polished_amethyst_block"] = it }
    val POLISHED_AMETHYST_SLAB = PolishedAmethystSlabBlock(FzzyBlockSettings.basic().mapColor(MapColor.PURPLE).strength(4.0f).requiresTool()).also { regBlock["polished_amethyst_slab"] = it }
    val POLISHED_AMETHYST_STAIRS = PolishedAmethystStairsBlock(POLISHED_AMETHYST_BLOCK.defaultState,FzzyBlockSettings.basic().mapColor(MapColor.PURPLE).strength(4.0f).requiresTool()).also { regBlock["polished_amethyst_stairs"] = it }
    val POLISHED_AMETHYST_PILLAR = PolishedAmethystPillarBlock(FzzyBlockSettings.basic().mapColor(MapColor.PURPLE).strength(4.0f).requiresTool()).also { regBlock["polished_amethyst_pillar"] = it }
    val POLISHED_AMETHYST_BRICKS = AmethystBlock(FzzyBlockSettings.basic().mapColor(MapColor.PURPLE).strength(4.0f).requiresTool()).also { regBlock["polished_amethyst_bricks"] = it }
    val CHISELED_POLISHED_AMETHYST_BLOCK = AmethystBlock(FzzyBlockSettings.basic().mapColor(MapColor.PURPLE).strength(4.0f).requiresTool()).also { regBlock["chiseled_polished_amethyst_block"] = it }
    val FORCEFIELD_BLOCK = ForcefieldBlock(FzzyBlockSettings.light().mapColor(MapColor.LIGHT_BLUE_GRAY).nonOpaque().strength(5.0f, 1200.0f).sounds(BlockSoundGroup.WOOL).blockVision { _: BlockState, _: BlockView, _: BlockPos -> never() }.suffocates { _: BlockState, _: BlockView, _: BlockPos -> never() }).also { regBlock["forcefield_block"] = it }
    val GLISTENING_ICE = TransparentBlock(FzzyBlockSettings.basic().mapColor(MapColor.PALE_PURPLE).strength(3.0f).slipperiness(0.992f).sounds(BlockSoundGroup.GLASS)).also { regBlock["glistening_ice"] = it }
    val GLISTENING_ICE_ITEM = SpellcastersReagentBlockItem(
        RegisterAttribute.SPELL_COOLDOWN,
        EntityAttributeModifier(
            UUID.fromString("102d4ad8-c5e5-11ed-afa1-0242ac120002"),"glistening_modifier",0.035,
            EntityAttributeModifier.Operation.MULTIPLY_TOTAL),
        GLISTENING_ICE,
        FabricItemSettings()
    )

    val TIGERS_EYE_BLACKSTONE_ORE = ExperienceDroppingBlock(FzzyBlockSettings.basic().mapColor(MapColor.BLACK).requiresTool().strength(1.5f, 6.0f),UniformIntProvider.create(3,7)).also { regBlock["tigers_eye_blackstone_ore"] = it }
    val TIGERS_EYE_BASALT_ORE = PillarExperienceDroppingBlock(FzzyBlockSettings.basic().mapColor(MapColor.BLACK).requiresTool().strength(1.25f, 4.2f).sounds(BlockSoundGroup.BASALT),UniformIntProvider.create(3,7)).also { regBlock["tigers_eye_basalt_ore"] = it }

    val HARD_LIGHT_BLOCK = HardLightBlock(FzzyBlockSettings.light().mapColor(MapColor.PINK).strength(0.3f).sounds(BlockSoundGroup.GLASS).nonOpaque().breakInstantly().luminance(10)).also { regBlock["hard_light_block"] = it }
    val CRYSTALLIZED_LIGHT_WHITE = HardLightBlock(FzzyBlockSettings.light().mapColor(MapColor.WHITE).strength(0.3f).sounds(BlockSoundGroup.GLASS).nonOpaque().breakInstantly().luminance(10)).also { regBlock["crystallized_light_white"] = it }
    val CRYSTALLIZED_LIGHT_LIGHT_GRAY = HardLightBlock(FzzyBlockSettings.light().mapColor(MapColor.LIGHT_GRAY).strength(0.3f).sounds(BlockSoundGroup.GLASS).nonOpaque().breakInstantly().luminance(10)).also { regBlock["crystallized_light_light_gray"] = it }
    val CRYSTALLIZED_LIGHT_GRAY = HardLightBlock(FzzyBlockSettings.light().mapColor(MapColor.GRAY).strength(0.3f).sounds(BlockSoundGroup.GLASS).nonOpaque().breakInstantly().luminance(10)).also { regBlock["crystallized_light_gray"] = it }
    val CRYSTALLIZED_LIGHT_BLACK = HardLightBlock(FzzyBlockSettings.light().mapColor(MapColor.BLACK).strength(0.3f).sounds(BlockSoundGroup.GLASS).nonOpaque().breakInstantly().luminance(10)).also { regBlock["crystallized_light_black"] = it }
    val CRYSTALLIZED_LIGHT_BROWN = HardLightBlock(FzzyBlockSettings.light().mapColor(MapColor.BROWN).strength(0.3f).sounds(BlockSoundGroup.GLASS).nonOpaque().breakInstantly().luminance(10)).also { regBlock["crystallized_light_brown"] = it }
    val CRYSTALLIZED_LIGHT_RED = HardLightBlock(FzzyBlockSettings.light().mapColor(MapColor.RED).strength(0.3f).sounds(BlockSoundGroup.GLASS).nonOpaque().breakInstantly().luminance(10)).also { regBlock["crystallized_light_red"] = it }
    val CRYSTALLIZED_LIGHT_ORANGE = HardLightBlock(FzzyBlockSettings.light().mapColor(MapColor.ORANGE).strength(0.3f).sounds(BlockSoundGroup.GLASS).nonOpaque().breakInstantly().luminance(10)).also { regBlock["crystallized_light_orange"] = it }
    val CRYSTALLIZED_LIGHT_YELLOW = HardLightBlock(FzzyBlockSettings.light().mapColor(MapColor.YELLOW).strength(0.3f).sounds(BlockSoundGroup.GLASS).nonOpaque().breakInstantly().luminance(10)).also { regBlock["crystallized_light_yellow"] = it }
    val CRYSTALLIZED_LIGHT_LIME = HardLightBlock(FzzyBlockSettings.light().mapColor(MapColor.LIME).strength(0.3f).sounds(BlockSoundGroup.GLASS).nonOpaque().breakInstantly().luminance(10)).also { regBlock["crystallized_light_lime"] = it }
    val CRYSTALLIZED_LIGHT_GREEN = HardLightBlock(FzzyBlockSettings.light().mapColor(MapColor.GREEN).strength(0.3f).sounds(BlockSoundGroup.GLASS).nonOpaque().breakInstantly().luminance(10)).also { regBlock["crystallized_light_green"] = it }
    val CRYSTALLIZED_LIGHT_CYAN = HardLightBlock(FzzyBlockSettings.light().mapColor(MapColor.CYAN).strength(0.3f).sounds(BlockSoundGroup.GLASS).nonOpaque().breakInstantly().luminance(10)).also { regBlock["crystallized_light_cyan"] = it }
    val CRYSTALLIZED_LIGHT_LIGHT_BLUE = HardLightBlock(FzzyBlockSettings.light().mapColor(MapColor.LIGHT_BLUE).strength(0.3f).sounds(BlockSoundGroup.GLASS).nonOpaque().breakInstantly().luminance(10)).also { regBlock["crystallized_light_light_blue"] = it }
    val CRYSTALLIZED_LIGHT_BLUE = HardLightBlock(FzzyBlockSettings.light().mapColor(MapColor.BLUE).strength(0.3f).sounds(BlockSoundGroup.GLASS).nonOpaque().breakInstantly().luminance(10)).also { regBlock["crystallized_light_blue"] = it }
    val CRYSTALLIZED_LIGHT_PURPLE = HardLightBlock(FzzyBlockSettings.light().mapColor(MapColor.PURPLE).strength(0.3f).sounds(BlockSoundGroup.GLASS).nonOpaque().breakInstantly().luminance(10)).also { regBlock["crystallized_light_purple"] = it }
    val CRYSTALLIZED_LIGHT_MAGENTA = HardLightBlock(FzzyBlockSettings.light().mapColor(MapColor.MAGENTA).strength(0.3f).sounds(BlockSoundGroup.GLASS).nonOpaque().breakInstantly().luminance(10)).also { regBlock["crystallized_light_magenta"] = it }
    val CRYSTALLIZED_LIGHT_PINK = HardLightBlock(FzzyBlockSettings.light().mapColor(MapColor.PINK).strength(0.3f).sounds(BlockSoundGroup.GLASS).nonOpaque().breakInstantly().luminance(10)).also { regBlock["crystallized_light_pink"] = it }

    val SHINE_LIGHT = ShineLightBlock(FzzyBlockSettings.nonSolidLightDestroyMove().mapColor(MapColor.WHITE).nonOpaque().strength(0.01f).luminance(15).sounds(BlockSoundGroup.CANDLE).blockVision { _,_,_ -> never() }.suffocates { _,_,_ -> never() }).also { regBlock["shine_light"] = it }
    val SHINE_LIGHT_LIGHT_GRAY = ShineLightBlock(FzzyBlockSettings.nonSolidLightDestroyMove().mapColor(MapColor.LIGHT_GRAY).nonOpaque().strength(0.01f).luminance(15).sounds(BlockSoundGroup.CANDLE).blockVision { _,_,_ -> never() }.suffocates { _,_,_ -> never() }, ColoredEndParticleEffect(DyeColor.LIGHT_GRAY)).also { regBlock["shine_light_light_gray"] = it }
    val SHINE_LIGHT_GRAY = ShineLightBlock(FzzyBlockSettings.nonSolidLightDestroyMove().mapColor(MapColor.GRAY).nonOpaque().strength(0.01f).luminance(15).sounds(BlockSoundGroup.CANDLE).blockVision { _,_,_ -> never() }.suffocates { _,_,_ -> never() }, ColoredEndParticleEffect(DyeColor.GRAY)).also { regBlock["shine_light_gray"] = it }
    val SHINE_LIGHT_BLACK = ShineLightBlock(FzzyBlockSettings.nonSolidLightDestroyMove().mapColor(MapColor.BLACK).nonOpaque().strength(0.01f).luminance(15).sounds(BlockSoundGroup.CANDLE).blockVision { _,_,_ -> never() }.suffocates { _,_,_ -> never() }, ColoredEndParticleEffect(DyeColor.BLACK)).also { regBlock["shine_light_black"] = it }
    val SHINE_LIGHT_BROWN = ShineLightBlock(FzzyBlockSettings.nonSolidLightDestroyMove().mapColor(MapColor.BROWN).nonOpaque().strength(0.01f).luminance(15).sounds(BlockSoundGroup.CANDLE).blockVision { _,_,_ -> never() }.suffocates { _,_,_ -> never() }, ColoredEndParticleEffect(DyeColor.BROWN)).also { regBlock["shine_light_brown"] = it }
    val SHINE_LIGHT_RED = ShineLightBlock(FzzyBlockSettings.nonSolidLightDestroyMove().mapColor(MapColor.RED).nonOpaque().strength(0.01f).luminance(15).sounds(BlockSoundGroup.CANDLE).blockVision { _,_,_ -> never() }.suffocates { _,_,_ -> never() }, ColoredEndParticleEffect(DyeColor.RED)).also { regBlock["shine_light_red"] = it }
    val SHINE_LIGHT_ORANGE = ShineLightBlock(FzzyBlockSettings.nonSolidLightDestroyMove().mapColor(MapColor.ORANGE).nonOpaque().strength(0.01f).luminance(15).sounds(BlockSoundGroup.CANDLE).blockVision { _,_,_ -> never() }.suffocates { _,_,_ -> never() }, ColoredEndParticleEffect(DyeColor.ORANGE)).also { regBlock["shine_light_orange"] = it }
    val SHINE_LIGHT_YELLOW = ShineLightBlock(FzzyBlockSettings.nonSolidLightDestroyMove().mapColor(MapColor.YELLOW).nonOpaque().strength(0.01f).luminance(15).sounds(BlockSoundGroup.CANDLE).blockVision { _,_,_ -> never() }.suffocates { _,_,_ -> never() }, ColoredEndParticleEffect(DyeColor.YELLOW)).also { regBlock["shine_light_yellow"] = it }
    val SHINE_LIGHT_LIME = ShineLightBlock(FzzyBlockSettings.nonSolidLightDestroyMove().mapColor(MapColor.LIME).nonOpaque().strength(0.01f).luminance(15).sounds(BlockSoundGroup.CANDLE).blockVision { _,_,_ -> never() }.suffocates { _,_,_ -> never() }, ColoredEndParticleEffect(DyeColor.LIME)).also { regBlock["shine_light_lime"] = it }
    val SHINE_LIGHT_GREEN = ShineLightBlock(FzzyBlockSettings.nonSolidLightDestroyMove().mapColor(MapColor.GREEN).nonOpaque().strength(0.01f).luminance(15).sounds(BlockSoundGroup.CANDLE).blockVision { _,_,_ -> never() }.suffocates { _,_,_ -> never() }, ColoredEndParticleEffect(DyeColor.GREEN)).also { regBlock["shine_light_green"] = it }
    val SHINE_LIGHT_CYAN = ShineLightBlock(FzzyBlockSettings.nonSolidLightDestroyMove().mapColor(MapColor.CYAN).nonOpaque().strength(0.01f).luminance(15).sounds(BlockSoundGroup.CANDLE).blockVision { _,_,_ -> never() }.suffocates { _,_,_ -> never() }, ColoredEndParticleEffect(DyeColor.CYAN)).also { regBlock["shine_light_cyan"] = it }
    val SHINE_LIGHT_LIGHT_BLUE = ShineLightBlock(FzzyBlockSettings.nonSolidLightDestroyMove().mapColor(MapColor.LIGHT_BLUE).nonOpaque().strength(0.01f).luminance(15).sounds(BlockSoundGroup.CANDLE).blockVision { _,_,_ -> never() }.suffocates { _,_,_ -> never() }, ColoredEndParticleEffect(DyeColor.LIGHT_BLUE)).also { regBlock["shine_light_light_blue"] = it }
    val SHINE_LIGHT_BLUE = ShineLightBlock(FzzyBlockSettings.nonSolidLightDestroyMove().mapColor(MapColor.BLUE).nonOpaque().strength(0.01f).luminance(15).sounds(BlockSoundGroup.CANDLE).blockVision { _,_,_ -> never() }.suffocates { _,_,_ -> never() }, ColoredEndParticleEffect(DyeColor.BLUE)).also { regBlock["shine_light_blue"] = it }
    val SHINE_LIGHT_PURPLE = ShineLightBlock(FzzyBlockSettings.nonSolidLightDestroyMove().mapColor(MapColor.PURPLE).nonOpaque().strength(0.01f).luminance(15).sounds(BlockSoundGroup.CANDLE).blockVision { _,_,_ -> never() }.suffocates { _,_,_ -> never() }, ColoredEndParticleEffect(DyeColor.PURPLE)).also { regBlock["shine_light_purple"] = it }
    val SHINE_LIGHT_MAGENTA = ShineLightBlock(FzzyBlockSettings.nonSolidLightDestroyMove().mapColor(MapColor.MAGENTA).nonOpaque().strength(0.01f).luminance(15).sounds(BlockSoundGroup.CANDLE).blockVision { _,_,_ -> never() }.suffocates { _,_,_ -> never() }, ColoredEndParticleEffect(DyeColor.MAGENTA)).also { regBlock["shine_light_magenta"] = it }
    val SHINE_LIGHT_PINK = ShineLightBlock(FzzyBlockSettings.nonSolidLightDestroyMove().mapColor(MapColor.PINK).nonOpaque().strength(0.01f).luminance(15).sounds(BlockSoundGroup.CANDLE).blockVision { _,_,_ -> never() }.suffocates { _,_,_ -> never() }, ColoredEndParticleEffect(DyeColor.PINK)).also { regBlock["shine_light_pink"] = it }
    val SHINE_LIGHT_RAINBOW = ShineLightRainbowBlock(FzzyBlockSettings.nonSolidLightDestroyMove().mapColor(MapColor.WHITE).nonOpaque().strength(0.01f).luminance(15).sounds(BlockSoundGroup.CANDLE).blockVision { _, _, _ -> never() }.suffocates { _, _, _ -> never() }).also { regBlock["shine_light_rainbow"] = it }

    fun registerAll() {
        for (k in regBlock.keys) {
            if (k == "experience_bush" || k =="warding_candle" || k =="steel_block" || k =="beryl_copper_block" || k =="glistening_ice") continue
            registerBlock(k,regBlock[k]?:continue)
        }

        Registry.register(Registries.BLOCK, Identifier(AI.MOD_ID, "experience_bush"), EXPERIENCE_BUSH)
        Registry.register(Registries.BLOCK, Identifier(AI.MOD_ID, "warding_candle"), WARDING_CANDLE)
        Registry.register(Registries.ITEM, Identifier(AI.MOD_ID,"warding_candle"), WARDING_CANDLE_ITEM)
        Registry.register(Registries.BLOCK, Identifier(AI.MOD_ID, "steel_block"), STEEL_BLOCK)
        Registry.register(Registries.ITEM, Identifier(AI.MOD_ID,"steel_block"), STEEL_BLOCK_ITEM)
        Registry.register(Registries.BLOCK, Identifier(AI.MOD_ID, "beryl_copper_block"), BERYL_COPPER_BLOCK)
        Registry.register(Registries.ITEM, Identifier(AI.MOD_ID,"beryl_copper_block"), BERYL_COPPER_BLOCK_ITEM)
        Registry.register(Registries.BLOCK, Identifier(AI.MOD_ID, "glistening_ice"), GLISTENING_ICE)
        Registry.register(Registries.ITEM, Identifier(AI.MOD_ID,"glistening_ice"), GLISTENING_ICE_ITEM)
    }

    private fun registerBlock(path: String, block:Block): Block{
        val item = BlockItem(block,FabricItemSettings())
        Registry.register(Registries.ITEM, Identifier(AI.MOD_ID,path), item)
        return Registry.register(Registries.BLOCK, Identifier(AI.MOD_ID, path), block)
    }

    private fun registerBlockOnly(path: String, block:Block): Block{
        return Registry.register(Registries.BLOCK, Identifier(AI.MOD_ID, path), block)
    }

    private fun registerItemOnly(path: String, item: Item): Item{
        return Registry.register(Registries.ITEM, Identifier(AI.MOD_ID,path), item)
    }

    @Suppress("unused")
    private fun always(): Boolean {
        return true
    }
    private fun never(): Boolean {
        return false
    }

}
