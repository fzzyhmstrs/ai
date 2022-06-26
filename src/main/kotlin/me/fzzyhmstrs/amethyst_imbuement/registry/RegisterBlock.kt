package me.fzzyhmstrs.amethyst_imbuement.registry

import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.block.*
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.minecraft.block.*
import net.minecraft.item.BlockItem
import net.minecraft.item.ItemGroup
import net.minecraft.sound.BlockSoundGroup
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.registry.Registry
import net.minecraft.world.BlockView

@Suppress("MemberVisibilityCanBePrivate")
object RegisterBlock {
    val POLISHED_AMETHYST_BLOCK = AmethystBlock(FabricBlockSettings.of(Material.AMETHYST, MapColor.PURPLE).strength(4.0f).requiresTool())
    val POLISHED_AMETHYST_SLAB = PolishedAmethystSlabBlock(FabricBlockSettings.of(Material.AMETHYST, MapColor.PURPLE).strength(4.0f).requiresTool())
    val POLISHED_AMETHYST_STAIRS = PolishedAmethystStairsBlock(POLISHED_AMETHYST_BLOCK.defaultState,FabricBlockSettings.of(Material.AMETHYST, MapColor.PURPLE).strength(4.0f).requiresTool())
    val POLISHED_AMETHYST_PILLAR = PolishedAmethystPillarBlock(FabricBlockSettings.of(Material.AMETHYST, MapColor.PURPLE).strength(4.0f).requiresTool())
    val POLISHED_AMETHYST_BRICKS = PolishedAmethystBlockBlock(FabricBlockSettings.of(Material.AMETHYST, MapColor.PURPLE).strength(4.0f).requiresTool())
    val CHISELED_POLISHED_AMETHYST_BLOCK = PolishedAmethystBlockBlock(FabricBlockSettings.of(Material.AMETHYST, MapColor.PURPLE).strength(4.0f).requiresTool())
    val CRYSTALLINE_CORE_BLOCK = AmethystBlock(FabricBlockSettings.of(Material.AMETHYST, MapColor.PURPLE).strength(4.0f).requiresTool())
    val WARDING_CANDLE = WardingCandleBlock(FabricBlockSettings.of(Material.DECORATION, MapColor.OFF_WHITE).nonOpaque().strength(0.1f).sounds(BlockSoundGroup.CANDLE).luminance(WardingCandleBlock.STATE_TO_LUMINANCE))
    val IMBUING_TABLE = ImbuingTableBlock(FabricBlockSettings.of(Material.STONE, MapColor.YELLOW).requiresTool().strength(5.0f, 1200.0f).luminance(10))
    val DISENCHANTING_TABLE = DisenchantingTableBlock(FabricBlockSettings.of(Material.STONE, MapColor.YELLOW).requiresTool().strength(5.0f, 1200.0f).luminance(10))
    val ALTAR_OF_EXPERIENCE = AltarOfExperienceBlock(FabricBlockSettings.of(Material.STONE, MapColor.YELLOW).requiresTool().strength(5.0f, 1200.0f).luminance(10))
    val STEEL_BLOCK = Block(FabricBlockSettings.of(Material.METAL, MapColor.IRON_GRAY).requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL))
    val BERYL_COPPER_BLOCK = Block(FabricBlockSettings.of(Material.METAL, MapColor.ORANGE).requiresTool().strength(3.0f, 6.0f).sounds(BlockSoundGroup.COPPER))
    val CUT_BERYL_COPPER_BLOCK = Block(FabricBlockSettings.of(Material.METAL, MapColor.ORANGE).requiresTool().strength(3.0f, 6.0f).sounds(BlockSoundGroup.COPPER))
    val CUT_BERYL_COPPER_SLAB = SlabBlock(FabricBlockSettings.of(Material.METAL, MapColor.ORANGE).requiresTool().strength(3.0f, 6.0f).sounds(BlockSoundGroup.COPPER))
    val CUT_BERYL_COPPER_STAIRS = CutBerylCopperStairsBlock(CUT_BERYL_COPPER_BLOCK.defaultState,FabricBlockSettings.of(Material.METAL, MapColor.ORANGE).requiresTool().strength(3.0f, 6.0f).sounds(BlockSoundGroup.COPPER))
    val HARD_LIGHT_BLOCK = Block(FabricBlockSettings.of(Material.GLASS, MapColor.PINK).strength(0.3f).sounds(BlockSoundGroup.GLASS).nonOpaque().breakInstantly().luminance(10))
    val EXPERIENCE_BUSH = ExperienceBushBlock(FabricBlockSettings.of(Material.PLANT, MapColor.LICHEN_GREEN).ticksRandomly().noCollision().sounds(BlockSoundGroup.SWEET_BERRY_BUSH))
    val CRYSTAL_ALTAR = CrystalAltarBlock(FabricBlockSettings.of(Material.WOOD, MapColor.DEEPSLATE_GRAY).strength(2.5f).sounds(BlockSoundGroup.WOOD))
    val FORCEFIELD_BLOCK = ForcefieldBlock(FabricBlockSettings.of(Material.GLASS, MapColor.LIGHT_BLUE_GRAY).nonOpaque().strength(5.0f, 1200.0f).sounds(BlockSoundGroup.WOOL).blockVision { _: BlockState, _: BlockView, _: BlockPos -> never() }.suffocates { _: BlockState, _: BlockView, _: BlockPos -> never() })

    fun registerAll() {
        registerBlock("polished_amethyst_block",POLISHED_AMETHYST_BLOCK,ItemGroup.DECORATIONS)
        registerBlock("polished_amethyst_slab",POLISHED_AMETHYST_SLAB,ItemGroup.DECORATIONS)
        registerBlock("polished_amethyst_stairs",POLISHED_AMETHYST_STAIRS,ItemGroup.DECORATIONS)
        registerBlock("polished_amethyst_pillar",POLISHED_AMETHYST_PILLAR,ItemGroup.DECORATIONS)
        registerBlock("polished_amethyst_bricks", POLISHED_AMETHYST_BRICKS,ItemGroup.DECORATIONS)
        registerBlock("chiseled_polished_amethyst_block",CHISELED_POLISHED_AMETHYST_BLOCK,ItemGroup.DECORATIONS)
        registerBlock("crystalline_core", CRYSTALLINE_CORE_BLOCK,ItemGroup.DECORATIONS)
        registerBlock("warding_candle",WARDING_CANDLE,ItemGroup.DECORATIONS)
        registerBlock("imbuing_table", IMBUING_TABLE,ItemGroup.DECORATIONS)
        registerBlock("disenchanting_table", DISENCHANTING_TABLE,ItemGroup.DECORATIONS)
        registerBlock("altar_of_experience", ALTAR_OF_EXPERIENCE,ItemGroup.DECORATIONS)
        registerBlock("steel_block", STEEL_BLOCK,ItemGroup.BUILDING_BLOCKS)
        registerBlock("beryl_copper_block", BERYL_COPPER_BLOCK,ItemGroup.BUILDING_BLOCKS)
        registerBlock("cut_beryl_copper_block", CUT_BERYL_COPPER_BLOCK,ItemGroup.BUILDING_BLOCKS)
        registerBlock("cut_beryl_copper_stairs", CUT_BERYL_COPPER_STAIRS,ItemGroup.BUILDING_BLOCKS)
        registerBlock("cut_beryl_copper_slab", CUT_BERYL_COPPER_SLAB,ItemGroup.BUILDING_BLOCKS)
        registerBlock("hard_light_block", HARD_LIGHT_BLOCK,ItemGroup.DECORATIONS)
        registerBlock("forcefield_block", FORCEFIELD_BLOCK,ItemGroup.DECORATIONS)
        Registry.register(Registry.BLOCK, Identifier(AI.MOD_ID, "experience_bush"), EXPERIENCE_BUSH)
        registerBlock("crystal_altar", CRYSTAL_ALTAR,ItemGroup.DECORATIONS)
    }

    private fun registerBlock(path: String, block:Block, itemGroup: ItemGroup){
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