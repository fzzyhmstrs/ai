package me.fzzyhmstrs.amethyst_imbuement.registry

import me.fzzyhmstrs.amethyst_core.registry.RegisterAttribute
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.block.*
import me.fzzyhmstrs.amethyst_imbuement.item.AiItemSettings
import me.fzzyhmstrs.amethyst_imbuement.item.SpellcastersReagentBlockItem
import me.fzzyhmstrs.amethyst_imbuement.particle.ColoredEndParticleEffect
import me.fzzyhmstrs.fzzy_core.coding_util.FzzyBlockSettings
import me.fzzyhmstrs.fzzy_core.coding_util.FzzyPort
import me.fzzyhmstrs.fzzy_core.coding_util.compat.XpDroppingBlock
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.minecraft.block.*
import net.minecraft.block.enums.Instrument
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.sound.BlockSoundGroup
import net.minecraft.sound.SoundEvents
import net.minecraft.util.DyeColor
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.intprovider.UniformIntProvider
import net.minecraft.world.BlockView
import java.util.*

@Suppress("MemberVisibilityCanBePrivate")
object RegisterBlock {

    internal val regBlockItem: ArrayList<Item> = ArrayList(65)

    val steelSoundGroup = BlockSoundGroup(1.0f, 1.5f, SoundEvents.BLOCK_METAL_BREAK, RegisterSound.STEEL_STEPS, SoundEvents.BLOCK_METAL_PLACE, SoundEvents.BLOCK_METAL_HIT, RegisterSound.STEEL_FALLS)

    val IMBUING_TABLE = registerBoth(ImbuingTableBlock(FzzyBlockSettings.basic().mapColor(MapColor.YELLOW).requiresTool().strength(5.0f, 1200.0f).luminance(10)), "imbuing_table")
    val DISENCHANTING_TABLE = registerBoth(DisenchantingTableBlock(FzzyBlockSettings.basic().mapColor(MapColor.YELLOW).requiresTool().strength(5.0f, 1200.0f).luminance(10)), "disenchanting_table")
    val ALTAR_OF_EXPERIENCE = registerBoth(AltarOfExperienceBlock(FzzyBlockSettings.basic().mapColor(MapColor.YELLOW).requiresTool().strength(5.0f, 1200.0f).luminance(10)), "altar_of_experience")
    val CRYSTAL_ALTAR = registerBoth(CrystalAltarBlock(FzzyBlockSettings.burn().mapColor(MapColor.OFF_WHITE).strength(2.5f).sounds(BlockSoundGroup.WOOD)), "crystal_altar")
    val WITCHES_BOOKSHELF = registerBoth(WitchesBookshelfBlock(FzzyBlockSettings.burn().mapColor(MapColor.BROWN).strength(1.5f).sounds(BlockSoundGroup.WOOD)), "witches_bookshelf")
    val EXPERIENCE_BUSH = registerBlock(ExperienceBushBlock(FzzyBlockSettings.nonSolidLightDestroyMove().mapColor(MapColor.LICHEN_GREEN).ticksRandomly().noCollision().sounds(BlockSoundGroup.SWEET_BERRY_BUSH)), "experience_bush")
    val CRYSTALLINE_CORE_BLOCK = registerBoth(AmethystBlock(FzzyBlockSettings.basic().mapColor(MapColor.PURPLE).strength(4.0f).requiresTool()), "crystalline_core")
    val WARDING_CANDLE = registerBlock(WardingCandleBlock(FzzyBlockSettings.nonSolidLightDestroyMove().mapColor(MapColor.OFF_WHITE).strength(0.1f).sounds(BlockSoundGroup.CANDLE).luminance(WardingCandleBlock.STATE_TO_LUMINANCE)), "warding_candle")
    val WARDING_CANDLE_ITEM = registerItem(SpellcastersReagentBlockItem(
        EntityAttributes.GENERIC_ARMOR_TOUGHNESS,
        EntityAttributeModifier(UUID.fromString("279ce6aa-d1c6-11ed-afa1-0242ac120002"),"warding_modifier",0.5,EntityAttributeModifier.Operation.ADDITION),
        WARDING_CANDLE,
        FabricItemSettings()
    ), "warding_candle")
    val STEEL_BLOCK = registerBlock(Block(FzzyBlockSettings.basic().mapColor(MapColor.IRON_GRAY).requiresTool().strength(5.0f, 6.0f).sounds(steelSoundGroup)), "steel_block")
    val STEEL_BLOCK_ITEM = registerItem(SpellcastersReagentBlockItem(
        EntityAttributes.GENERIC_ARMOR,
        EntityAttributeModifier(UUID.fromString("75c099f6-ce58-11ed-afa1-0242ac120002"),"steel_modifier",0.75,EntityAttributeModifier.Operation.ADDITION),
        STEEL_BLOCK,
        FabricItemSettings()
    ), "steel_block")
    val BERYL_COPPER_BLOCK = registerBlock(Block(FzzyBlockSettings.basic().mapColor(MapColor.ORANGE).requiresTool().strength(3.0f, 6.0f).sounds(BlockSoundGroup.COPPER)), "beryl_copper_block")
    val BERYL_COPPER_BLOCK_ITEM = registerItem(SpellcastersReagentBlockItem(
        EntityAttributes.GENERIC_ATTACK_SPEED,
        EntityAttributeModifier(UUID.fromString("75c099f6-ce58-11ed-afa1-0242ac120002"),"beryl_modifier",0.05,EntityAttributeModifier.Operation.MULTIPLY_TOTAL),
        BERYL_COPPER_BLOCK,
        FabricItemSettings()
    ), "beryl_copper_block")
    val CUT_BERYL_COPPER_BLOCK = registerBoth(Block(FzzyBlockSettings.basic().mapColor(MapColor.ORANGE).requiresTool().strength(3.0f, 6.0f).sounds(BlockSoundGroup.COPPER)), "cut_beryl_copper_block")
    val CUT_BERYL_COPPER_SLAB = registerBoth(SlabBlock(FzzyBlockSettings.basic().mapColor(MapColor.ORANGE).requiresTool().strength(3.0f, 6.0f).sounds(BlockSoundGroup.COPPER)), "cut_beryl_copper_slab")
    val CUT_BERYL_COPPER_STAIRS = registerBoth(StairsBlock(CUT_BERYL_COPPER_BLOCK.defaultState,FzzyBlockSettings.basic().mapColor(MapColor.ORANGE).requiresTool().strength(3.0f, 6.0f).sounds(BlockSoundGroup.COPPER)), "cut_beryl_copper_stairs")
    val POLISHED_AMETHYST_BLOCK = registerBoth(AmethystBlock(FzzyBlockSettings.basic().mapColor(MapColor.PURPLE).strength(4.0f).requiresTool()), "polished_amethyst_block")
    val POLISHED_AMETHYST_SLAB = registerBoth(PolishedAmethystSlabBlock(FzzyBlockSettings.basic().mapColor(MapColor.PURPLE).strength(4.0f).requiresTool()), "polished_amethyst_slab")
    val POLISHED_AMETHYST_STAIRS = registerBoth(PolishedAmethystStairsBlock(POLISHED_AMETHYST_BLOCK.defaultState,FzzyBlockSettings.basic().mapColor(MapColor.PURPLE).strength(4.0f).requiresTool()), "polished_amethyst_stairs")
    val POLISHED_AMETHYST_PILLAR = registerBoth(PolishedAmethystPillarBlock(FzzyBlockSettings.basic().mapColor(MapColor.PURPLE).strength(4.0f).requiresTool()), "polished_amethyst_pillar")
    val POLISHED_AMETHYST_BRICKS = registerBoth(AmethystBlock(FzzyBlockSettings.basic().mapColor(MapColor.PURPLE).strength(4.0f).requiresTool()), "polished_amethyst_bricks")
    val CHISELED_POLISHED_AMETHYST_BLOCK = registerBoth(AmethystBlock(FzzyBlockSettings.basic().mapColor(MapColor.PURPLE).strength(4.0f).requiresTool()), "chiseled_polished_amethyst_block")
    val FORCEFIELD_BLOCK = registerBoth(ForcefieldBlock(FzzyBlockSettings.light().mapColor(MapColor.LIGHT_BLUE_GRAY).nonOpaque().strength(5.0f, 1200.0f).sounds(BlockSoundGroup.WOOL).blockVision { _: BlockState, _: BlockView, _: BlockPos -> never() }.suffocates { _: BlockState, _: BlockView, _: BlockPos -> never() }), "forcefield_block")
    val GLISTENING_ICE = registerBlock(TransparentBlock(FzzyBlockSettings.basic().mapColor(MapColor.PALE_PURPLE).strength(3.0f).slipperiness(0.992f).sounds(BlockSoundGroup.GLASS)), "glistening_ice")
    val GLISTENING_ICE_ITEM = registerItem(SpellcastersReagentBlockItem(
        RegisterAttribute.SPELL_COOLDOWN,
        EntityAttributeModifier(
            UUID.fromString("102d4ad8-c5e5-11ed-afa1-0242ac120002"),"glistening_modifier",0.035,
            EntityAttributeModifier.Operation.MULTIPLY_TOTAL),
        GLISTENING_ICE,
        FabricItemSettings()
    ), "glistening_ice")
    val GILDED_LOCKBOX = registerBoth(GildedLockboxBlock(FzzyBlockSettings.burn().mapColor(MapColor.OFF_WHITE).instrument(Instrument.BASS).strength(2.5f).sounds(BlockSoundGroup.WOOD)), "gilded_lockbox")
    val PLANAR_DOOR = registerBlock(PlanarDoorBlock(FzzyBlockSettings.nonSolidLightDestroyMoveReplace().mapColor(MapColor.PURPLE).sounds(BlockSoundGroup.GLASS)), "planar_door")
    val SARDONYX_CRYSTAL = registerBoth(SardonyxCrystalBlock(FzzyBlockSettings.basic().mapColor(MapColor.ORANGE).requiresTool().strength(3.0f, 6.0f).luminance(10).sounds(BlockSoundGroup.AMETHYST_CLUSTER)), "sardonyx_crystal")
    
    val TIGERS_EYE_BLACKSTONE_ORE = registerBoth(XpDroppingBlock(FzzyBlockSettings.basic().mapColor(MapColor.BLACK).requiresTool().strength(1.5f, 6.0f),UniformIntProvider.create(3,7)), "tigers_eye_blackstone_ore")
    val TIGERS_EYE_BASALT_ORE = registerBoth(PillarExperienceDroppingBlock(FzzyBlockSettings.basic().mapColor(MapColor.BLACK).requiresTool().strength(1.25f, 4.2f).sounds(BlockSoundGroup.BASALT),UniformIntProvider.create(3,7)), "tigers_eye_basalt_ore")

    val HARD_LIGHT_BLOCK = registerBoth(HardLightBlock(FzzyBlockSettings.light().mapColor(MapColor.PINK).strength(0.3f).sounds(BlockSoundGroup.GLASS).nonOpaque().breakInstantly().luminance(10)), "hard_light_block")
    val CRYSTALLIZED_LIGHT_WHITE = registerBoth(HardLightBlock(FzzyBlockSettings.light().mapColor(MapColor.WHITE).strength(0.3f).sounds(BlockSoundGroup.GLASS).nonOpaque().breakInstantly().luminance(10)), "crystallized_light_white")
    val CRYSTALLIZED_LIGHT_LIGHT_GRAY = registerBoth(HardLightBlock(FzzyBlockSettings.light().mapColor(MapColor.LIGHT_GRAY).strength(0.3f).sounds(BlockSoundGroup.GLASS).nonOpaque().breakInstantly().luminance(10)), "crystallized_light_light_gray")
    val CRYSTALLIZED_LIGHT_GRAY = registerBoth(HardLightBlock(FzzyBlockSettings.light().mapColor(MapColor.GRAY).strength(0.3f).sounds(BlockSoundGroup.GLASS).nonOpaque().breakInstantly().luminance(10)), "crystallized_light_gray")
    val CRYSTALLIZED_LIGHT_BLACK = registerBoth(HardLightBlock(FzzyBlockSettings.light().mapColor(MapColor.BLACK).strength(0.3f).sounds(BlockSoundGroup.GLASS).nonOpaque().breakInstantly().luminance(10)), "crystallized_light_black")
    val CRYSTALLIZED_LIGHT_BROWN = registerBoth(HardLightBlock(FzzyBlockSettings.light().mapColor(MapColor.BROWN).strength(0.3f).sounds(BlockSoundGroup.GLASS).nonOpaque().breakInstantly().luminance(10)), "crystallized_light_brown")
    val CRYSTALLIZED_LIGHT_RED = registerBoth(HardLightBlock(FzzyBlockSettings.light().mapColor(MapColor.RED).strength(0.3f).sounds(BlockSoundGroup.GLASS).nonOpaque().breakInstantly().luminance(10)), "crystallized_light_red")
    val CRYSTALLIZED_LIGHT_ORANGE = registerBoth(HardLightBlock(FzzyBlockSettings.light().mapColor(MapColor.ORANGE).strength(0.3f).sounds(BlockSoundGroup.GLASS).nonOpaque().breakInstantly().luminance(10)), "crystallized_light_orange")
    val CRYSTALLIZED_LIGHT_YELLOW = registerBoth(HardLightBlock(FzzyBlockSettings.light().mapColor(MapColor.YELLOW).strength(0.3f).sounds(BlockSoundGroup.GLASS).nonOpaque().breakInstantly().luminance(10)), "crystallized_light_yellow")
    val CRYSTALLIZED_LIGHT_LIME = registerBoth(HardLightBlock(FzzyBlockSettings.light().mapColor(MapColor.LIME).strength(0.3f).sounds(BlockSoundGroup.GLASS).nonOpaque().breakInstantly().luminance(10)), "crystallized_light_lime")
    val CRYSTALLIZED_LIGHT_GREEN = registerBoth(HardLightBlock(FzzyBlockSettings.light().mapColor(MapColor.GREEN).strength(0.3f).sounds(BlockSoundGroup.GLASS).nonOpaque().breakInstantly().luminance(10)), "crystallized_light_green")
    val CRYSTALLIZED_LIGHT_CYAN = registerBoth(HardLightBlock(FzzyBlockSettings.light().mapColor(MapColor.CYAN).strength(0.3f).sounds(BlockSoundGroup.GLASS).nonOpaque().breakInstantly().luminance(10)), "crystallized_light_cyan")
    val CRYSTALLIZED_LIGHT_LIGHT_BLUE = registerBoth(HardLightBlock(FzzyBlockSettings.light().mapColor(MapColor.LIGHT_BLUE).strength(0.3f).sounds(BlockSoundGroup.GLASS).nonOpaque().breakInstantly().luminance(10)), "crystallized_light_light_blue")
    val CRYSTALLIZED_LIGHT_BLUE = registerBoth(HardLightBlock(FzzyBlockSettings.light().mapColor(MapColor.BLUE).strength(0.3f).sounds(BlockSoundGroup.GLASS).nonOpaque().breakInstantly().luminance(10)), "crystallized_light_blue")
    val CRYSTALLIZED_LIGHT_PURPLE = registerBoth(HardLightBlock(FzzyBlockSettings.light().mapColor(MapColor.PURPLE).strength(0.3f).sounds(BlockSoundGroup.GLASS).nonOpaque().breakInstantly().luminance(10)), "crystallized_light_purple")
    val CRYSTALLIZED_LIGHT_MAGENTA = registerBoth(HardLightBlock(FzzyBlockSettings.light().mapColor(MapColor.MAGENTA).strength(0.3f).sounds(BlockSoundGroup.GLASS).nonOpaque().breakInstantly().luminance(10)), "crystallized_light_magenta")
    val CRYSTALLIZED_LIGHT_PINK = registerBoth(HardLightBlock(FzzyBlockSettings.light().mapColor(MapColor.PINK).strength(0.3f).sounds(BlockSoundGroup.GLASS).nonOpaque().breakInstantly().luminance(10)), "crystallized_light_pink")

    val SHINE_LIGHT = registerBoth(ShineLightBlock(FzzyBlockSettings.nonSolidLightDestroyMove().mapColor(MapColor.WHITE).nonOpaque().strength(0.01f).luminance(15).sounds(BlockSoundGroup.CANDLE).blockVision { _,_,_ -> never() }.suffocates { _,_,_ -> never() }), "shine_light")
    val SHINE_LIGHT_LIGHT_GRAY = registerBoth(ShineLightBlock(FzzyBlockSettings.nonSolidLightDestroyMove().mapColor(MapColor.LIGHT_GRAY).nonOpaque().strength(0.01f).luminance(15).sounds(BlockSoundGroup.CANDLE).blockVision { _,_,_ -> never() }.suffocates { _,_,_ -> never() }, ColoredEndParticleEffect(DyeColor.LIGHT_GRAY)), "shine_light_light_gray")
    val SHINE_LIGHT_GRAY = registerBoth(ShineLightBlock(FzzyBlockSettings.nonSolidLightDestroyMove().mapColor(MapColor.GRAY).nonOpaque().strength(0.01f).luminance(15).sounds(BlockSoundGroup.CANDLE).blockVision { _,_,_ -> never() }.suffocates { _,_,_ -> never() }, ColoredEndParticleEffect(DyeColor.GRAY)), "shine_light_gray")
    val SHINE_LIGHT_BLACK = registerBoth(ShineLightBlock(FzzyBlockSettings.nonSolidLightDestroyMove().mapColor(MapColor.BLACK).nonOpaque().strength(0.01f).luminance(15).sounds(BlockSoundGroup.CANDLE).blockVision { _,_,_ -> never() }.suffocates { _,_,_ -> never() }, ColoredEndParticleEffect(DyeColor.BLACK)), "shine_light_black")
    val SHINE_LIGHT_BROWN = registerBoth(ShineLightBlock(FzzyBlockSettings.nonSolidLightDestroyMove().mapColor(MapColor.BROWN).nonOpaque().strength(0.01f).luminance(15).sounds(BlockSoundGroup.CANDLE).blockVision { _,_,_ -> never() }.suffocates { _,_,_ -> never() }, ColoredEndParticleEffect(DyeColor.BROWN)), "shine_light_brown")
    val SHINE_LIGHT_RED = registerBoth(ShineLightBlock(FzzyBlockSettings.nonSolidLightDestroyMove().mapColor(MapColor.RED).nonOpaque().strength(0.01f).luminance(15).sounds(BlockSoundGroup.CANDLE).blockVision { _,_,_ -> never() }.suffocates { _,_,_ -> never() }, ColoredEndParticleEffect(DyeColor.RED)), "shine_light_red")
    val SHINE_LIGHT_ORANGE = registerBoth(ShineLightBlock(FzzyBlockSettings.nonSolidLightDestroyMove().mapColor(MapColor.ORANGE).nonOpaque().strength(0.01f).luminance(15).sounds(BlockSoundGroup.CANDLE).blockVision { _,_,_ -> never() }.suffocates { _,_,_ -> never() }, ColoredEndParticleEffect(DyeColor.ORANGE)), "shine_light_orange")
    val SHINE_LIGHT_YELLOW = registerBoth(ShineLightBlock(FzzyBlockSettings.nonSolidLightDestroyMove().mapColor(MapColor.YELLOW).nonOpaque().strength(0.01f).luminance(15).sounds(BlockSoundGroup.CANDLE).blockVision { _,_,_ -> never() }.suffocates { _,_,_ -> never() }, ColoredEndParticleEffect(DyeColor.YELLOW)), "shine_light_yellow")
    val SHINE_LIGHT_LIME = registerBoth(ShineLightBlock(FzzyBlockSettings.nonSolidLightDestroyMove().mapColor(MapColor.LIME).nonOpaque().strength(0.01f).luminance(15).sounds(BlockSoundGroup.CANDLE).blockVision { _,_,_ -> never() }.suffocates { _,_,_ -> never() }, ColoredEndParticleEffect(DyeColor.LIME)), "shine_light_lime")
    val SHINE_LIGHT_GREEN = registerBoth(ShineLightBlock(FzzyBlockSettings.nonSolidLightDestroyMove().mapColor(MapColor.GREEN).nonOpaque().strength(0.01f).luminance(15).sounds(BlockSoundGroup.CANDLE).blockVision { _,_,_ -> never() }.suffocates { _,_,_ -> never() }, ColoredEndParticleEffect(DyeColor.GREEN)), "shine_light_green")
    val SHINE_LIGHT_CYAN = registerBoth(ShineLightBlock(FzzyBlockSettings.nonSolidLightDestroyMove().mapColor(MapColor.CYAN).nonOpaque().strength(0.01f).luminance(15).sounds(BlockSoundGroup.CANDLE).blockVision { _,_,_ -> never() }.suffocates { _,_,_ -> never() }, ColoredEndParticleEffect(DyeColor.CYAN)), "shine_light_cyan")
    val SHINE_LIGHT_LIGHT_BLUE = registerBoth(ShineLightBlock(FzzyBlockSettings.nonSolidLightDestroyMove().mapColor(MapColor.LIGHT_BLUE).nonOpaque().strength(0.01f).luminance(15).sounds(BlockSoundGroup.CANDLE).blockVision { _,_,_ -> never() }.suffocates { _,_,_ -> never() }, ColoredEndParticleEffect(DyeColor.LIGHT_BLUE)), "shine_light_light_blue")
    val SHINE_LIGHT_BLUE = registerBoth(ShineLightBlock(FzzyBlockSettings.nonSolidLightDestroyMove().mapColor(MapColor.BLUE).nonOpaque().strength(0.01f).luminance(15).sounds(BlockSoundGroup.CANDLE).blockVision { _,_,_ -> never() }.suffocates { _,_,_ -> never() }, ColoredEndParticleEffect(DyeColor.BLUE)), "shine_light_blue")
    val SHINE_LIGHT_PURPLE = registerBoth(ShineLightBlock(FzzyBlockSettings.nonSolidLightDestroyMove().mapColor(MapColor.PURPLE).nonOpaque().strength(0.01f).luminance(15).sounds(BlockSoundGroup.CANDLE).blockVision { _,_,_ -> never() }.suffocates { _,_,_ -> never() }, ColoredEndParticleEffect(DyeColor.PURPLE)), "shine_light_purple")
    val SHINE_LIGHT_MAGENTA = registerBoth(ShineLightBlock(FzzyBlockSettings.nonSolidLightDestroyMove().mapColor(MapColor.MAGENTA).nonOpaque().strength(0.01f).luminance(15).sounds(BlockSoundGroup.CANDLE).blockVision { _,_,_ -> never() }.suffocates { _,_,_ -> never() }, ColoredEndParticleEffect(DyeColor.MAGENTA)), "shine_light_magenta")
    val SHINE_LIGHT_PINK = registerBoth(ShineLightBlock(FzzyBlockSettings.nonSolidLightDestroyMove().mapColor(MapColor.PINK).nonOpaque().strength(0.01f).luminance(15).sounds(BlockSoundGroup.CANDLE).blockVision { _,_,_ -> never() }.suffocates { _,_,_ -> never() }, ColoredEndParticleEffect(DyeColor.PINK)), "shine_light_pink")
    val SHINE_LIGHT_RAINBOW = registerBoth(ShineLightRainbowBlock(FzzyBlockSettings.nonSolidLightDestroyMove().mapColor(MapColor.WHITE).nonOpaque().strength(0.01f).luminance(15).sounds(BlockSoundGroup.CANDLE).blockVision { _, _, _ -> never() }.suffocates { _, _, _ -> never() }), "shine_light_rainbow")

    fun registerAll() {
    }

    private fun<T: Block> registerBoth(block:T, path: String): T{
        val item = BlockItem(block,AiItemSettings())
        regBlockItem.add(item)
        FzzyPort.ITEM.register(AI.identity(path),item)
        return FzzyPort.BLOCK.register(AI.identity(path),block)
    }

    private fun<T: Block> registerBlock(block:T, path: String): T{
        return FzzyPort.BLOCK.register(AI.identity(path),block)
    }

    private fun<T: Item> registerItem(item:T, path: String): T{
        regBlockItem.add(item)
        return FzzyPort.ITEM.register(AI.identity(path),item)
    }


    @Suppress("unused")
    private fun always(): Boolean {
        return true
    }
    private fun never(): Boolean {
        return false
    }

}
