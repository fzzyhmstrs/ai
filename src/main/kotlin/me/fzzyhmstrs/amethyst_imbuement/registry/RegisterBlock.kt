package me.fzzyhmstrs.amethyst_imbuement.registry

import me.fzzyhmstrs.amethyst_core.registry.RegisterAttribute
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.block.*
import me.fzzyhmstrs.amethyst_imbuement.item.GlisteningIceBlockItem
import me.fzzyhmstrs.amethyst_imbuement.item.SpellcastersReagentBlockItem
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.minecraft.block.*
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.item.BlockItem
import net.minecraft.item.ItemGroup
import net.minecraft.sound.BlockSoundGroup
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.registry.Registry
import net.minecraft.world.BlockView
import java.util.*

@Suppress("MemberVisibilityCanBePrivate")
object RegisterBlock {

    internal val regBlock: MutableMap<String, Block> = mutableMapOf()

    val IMBUING_TABLE = ImbuingTableBlock(FabricBlockSettings.of(Material.STONE, MapColor.YELLOW).requiresTool().strength(5.0f, 1200.0f).luminance(10)).also { regBlock["imbuing_table"] = it }
    val DISENCHANTING_TABLE = DisenchantingTableBlock(FabricBlockSettings.of(Material.STONE, MapColor.YELLOW).requiresTool().strength(5.0f, 1200.0f).luminance(10)).also { regBlock["disenchanting_table"] = it }
    val ALTAR_OF_EXPERIENCE = AltarOfExperienceBlock(FabricBlockSettings.of(Material.STONE, MapColor.YELLOW).requiresTool().strength(5.0f, 1200.0f).luminance(10)).also { regBlock["altar_of_experience"] = it }
    val CRYSTAL_ALTAR = CrystalAltarBlock(FabricBlockSettings.of(Material.WOOD, MapColor.DEEPSLATE_GRAY).strength(2.5f).sounds(BlockSoundGroup.WOOD)).also { regBlock["crystal_altar"] = it }
    val EXPERIENCE_BUSH = ExperienceBushBlock(FabricBlockSettings.of(Material.PLANT, MapColor.LICHEN_GREEN).ticksRandomly().noCollision().sounds(BlockSoundGroup.SWEET_BERRY_BUSH)).also { regBlock["experience_bush"] = it }
    val CRYSTALLINE_CORE_BLOCK = AmethystBlock(FabricBlockSettings.of(Material.AMETHYST, MapColor.PURPLE).strength(4.0f).requiresTool()).also { regBlock["crystalline_core"] = it }
    val WARDING_CANDLE = WardingCandleBlock(FabricBlockSettings.of(Material.DECORATION, MapColor.OFF_WHITE).nonOpaque().strength(0.1f).sounds(BlockSoundGroup.CANDLE).luminance(WardingCandleBlock.STATE_TO_LUMINANCE)).also { regBlock["warding_candle"] = it }
    val STEEL_BLOCK = Block(FabricBlockSettings.of(Material.METAL, MapColor.IRON_GRAY).requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL)).also { regBlock["steel_block"] = it }
    val STEEL_BLOCK_ITEM = SpellcastersReagentBlockItem(
        EntityAttributes.GENERIC_ARMOR,
        EntityAttributeModifier(UUID.fromString("75c099f6-ce58-11ed-afa1-0242ac120002"),"steel_modifier",1.0,EntityAttributeModifier.Operation.ADDITION),
        STEEL_BLOCK,
        FabricItemSettings().group(RegisterItem.AI_GROUP)
    )
    val BERYL_COPPER_BLOCK = Block(FabricBlockSettings.of(Material.METAL, MapColor.ORANGE).requiresTool().strength(3.0f, 6.0f).sounds(BlockSoundGroup.COPPER)).also { regBlock["beryl_copper_block"] = it }
    val BERYL_COPPER_BLOCK_ITEM = SpellcastersReagentBlockItem(
        EntityAttributes.GENERIC_MOVEMENT_SPEED,
        EntityAttributeModifier(UUID.fromString("75c099f6-ce58-11ed-afa1-0242ac120002"),"beryl_modifier",0.03,EntityAttributeModifier.Operation.MULTIPLY_TOTAL),
        BERYL_COPPER_BLOCK,
        FabricItemSettings().group(RegisterItem.AI_GROUP)
    )
    val CUT_BERYL_COPPER_BLOCK = Block(FabricBlockSettings.of(Material.METAL, MapColor.ORANGE).requiresTool().strength(3.0f, 6.0f).sounds(BlockSoundGroup.COPPER)).also { regBlock["cut_beryl_copper_block"] = it }
    val CUT_BERYL_COPPER_SLAB = SlabBlock(FabricBlockSettings.of(Material.METAL, MapColor.ORANGE).requiresTool().strength(3.0f, 6.0f).sounds(BlockSoundGroup.COPPER)).also { regBlock["cut_beryl_copper_slab"] = it }
    val CUT_BERYL_COPPER_STAIRS = StairsBlock(CUT_BERYL_COPPER_BLOCK.defaultState,FabricBlockSettings.of(Material.METAL, MapColor.ORANGE).requiresTool().strength(3.0f, 6.0f).sounds(BlockSoundGroup.COPPER)).also { regBlock["cut_beryl_copper_stairs"] = it }
    val POLISHED_AMETHYST_BLOCK = AmethystBlock(FabricBlockSettings.of(Material.AMETHYST, MapColor.PURPLE).strength(4.0f).requiresTool()).also { regBlock["polished_amethyst_block"] = it }
    val POLISHED_AMETHYST_SLAB = PolishedAmethystSlabBlock(FabricBlockSettings.of(Material.AMETHYST, MapColor.PURPLE).strength(4.0f).requiresTool()).also { regBlock["polished_amethyst_slab"] = it }
    val POLISHED_AMETHYST_STAIRS = PolishedAmethystStairsBlock(POLISHED_AMETHYST_BLOCK.defaultState,FabricBlockSettings.of(Material.AMETHYST, MapColor.PURPLE).strength(4.0f).requiresTool()).also { regBlock["polished_amethyst_stairs"] = it }
    val POLISHED_AMETHYST_PILLAR = PolishedAmethystPillarBlock(FabricBlockSettings.of(Material.AMETHYST, MapColor.PURPLE).strength(4.0f).requiresTool()).also { regBlock["polished_amethyst_pillar"] = it }
    val POLISHED_AMETHYST_BRICKS = AmethystBlock(FabricBlockSettings.of(Material.AMETHYST, MapColor.PURPLE).strength(4.0f).requiresTool()).also { regBlock["polished_amethyst_bricks"] = it }
    val CHISELED_POLISHED_AMETHYST_BLOCK = AmethystBlock(FabricBlockSettings.of(Material.AMETHYST, MapColor.PURPLE).strength(4.0f).requiresTool()).also { regBlock["chiseled_polished_amethyst_block"] = it }
    val HARD_LIGHT_BLOCK = Block(FabricBlockSettings.of(Material.GLASS, MapColor.PINK).strength(0.3f).sounds(BlockSoundGroup.GLASS).nonOpaque().breakInstantly().luminance(10)).also { regBlock["hard_light_block"] = it }
    val FORCEFIELD_BLOCK = ForcefieldBlock(FabricBlockSettings.of(Material.GLASS, MapColor.LIGHT_BLUE_GRAY).nonOpaque().strength(5.0f, 1200.0f).sounds(BlockSoundGroup.WOOL).blockVision { _: BlockState, _: BlockView, _: BlockPos -> never() }.suffocates { _: BlockState, _: BlockView, _: BlockPos -> never() }).also { regBlock["forcefield_block"] = it }
    val GLISTENING_ICE = TransparentBlock(FabricBlockSettings.of(Material.DENSE_ICE).strength(3.0f).slipperiness(0.992f).sounds(BlockSoundGroup.GLASS)).also { regBlock["glistening_ice"] = it }
    val GLISTENING_ICE_ITEM = SpellcastersReagentBlockItem(
        RegisterAttribute.SPELL_COOLDOWN,
        EntityAttributeModifier(
            UUID.fromString("102d4ad8-c5e5-11ed-afa1-0242ac120002"),"glistening_modifier",-0.03,
            EntityAttributeModifier.Operation.MULTIPLY_TOTAL),
        GLISTENING_ICE,
        FabricItemSettings().group(RegisterItem.AI_GROUP)
    )

    fun registerAll() {
        for (k in regBlock.keys) {
            if (k == "experience_bush" || k =="steel_block" || k =="beryl_copper_block" || k =="glistening_ice") continue
            registerBlock(k,regBlock[k],RegisterItem.AI_GROUP)
        }

        Registry.register(Registry.BLOCK, Identifier(AI.MOD_ID, "experience_bush"), EXPERIENCE_BUSH)
        Registry.register(Registry.BLOCK, Identifier(AI.MOD_ID, "steel_block"), STEEL_BLOCK)
        Registry.register(Registry.ITEM, Identifier(AI.MOD_ID,"steel_block"), STEEL_BLOCK_ITEM)
        Registry.register(Registry.BLOCK, Identifier(AI.MOD_ID, "beryl_copper_block"), BERYL_COPPER_BLOCK)
        Registry.register(Registry.ITEM, Identifier(AI.MOD_ID,"beryl_copper_block"), BERYL_COPPER_BLOCK_ITEM)
        Registry.register(Registry.BLOCK, Identifier(AI.MOD_ID, "glistening_ice"), GLISTENING_ICE)
        Registry.register(Registry.ITEM, Identifier(AI.MOD_ID,"glistening_ice"), GLISTENING_ICE_ITEM)
    }

    private fun registerBlock(path: String, block:Block?, itemGroup: ItemGroup){
        Registry.register(Registry.BLOCK, Identifier(AI.MOD_ID, path), block)
        Registry.register(Registry.ITEM, Identifier(AI.MOD_ID,path), BlockItem(block,FabricItemSettings().group(itemGroup)))
    }

    @Suppress("unused")
    private fun always(): Boolean {
        return true
    }
    private fun never(): Boolean {
        return false
    }

}