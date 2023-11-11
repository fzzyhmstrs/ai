package me.fzzyhmstrs.amethyst_imbuement.util

import com.mojang.serialization.codecs.RecordCodecBuilder
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterWorldgen.WATERLOGGED_UNDO_PROCESSOR
import net.minecraft.block.Block
import net.minecraft.registry.RegistryCodecs
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.entry.RegistryEntryList
import net.minecraft.state.property.Properties
import net.minecraft.structure.StructurePlacementData
import net.minecraft.structure.StructureTemplate
import net.minecraft.structure.processor.StructureProcessor
import net.minecraft.structure.processor.StructureProcessorType
import net.minecraft.util.math.BlockPos
import net.minecraft.world.WorldView
import java.util.*

class WaterloggedUndoStructureProcessor(private val blocksToSkip: Optional<RegistryEntryList<Block>>): StructureProcessor() {

    private val actualBlocksToSkip = if(blocksToSkip.isPresent) blocksToSkip.get().map { it.value() } else listOf()

    companion object {
        val CODEC =
            RecordCodecBuilder.create { instance: RecordCodecBuilder.Instance<WaterloggedUndoStructureProcessor> ->
                instance.group(
                    RegistryCodecs.entryList(RegistryKeys.BLOCK).optionalFieldOf("blocksToSkip")
                        .forGetter { processor -> processor.blocksToSkip }
                ).apply(instance) { bTS -> WaterloggedUndoStructureProcessor(bTS) }
            }
    }

    override fun getType(): StructureProcessorType<*> {
        return WATERLOGGED_UNDO_PROCESSOR
    }

    override fun process(
        world: WorldView,
        pos: BlockPos,
        pivot: BlockPos,
        originalBlockInfo: StructureTemplate.StructureBlockInfo,
        currentBlockInfo: StructureTemplate.StructureBlockInfo,
        data: StructurePlacementData
    ): StructureTemplate.StructureBlockInfo? {
        val blockState = currentBlockInfo.state()
        if (actualBlocksToSkip.contains(blockState.block))
            return super.process(world, pos, pivot, originalBlockInfo, currentBlockInfo, data)
        if (!blockState.contains(Properties.WATERLOGGED))
            return super.process(world, pos, pivot, originalBlockInfo, currentBlockInfo, data)
        if (blockState.contains(Properties.WATERLOGGED) && !blockState.get(Properties.WATERLOGGED)){
            return super.process(world, pos, pivot, originalBlockInfo, currentBlockInfo, data)
        }

        val blockState2 = blockState.with(Properties.WATERLOGGED, false)

        return StructureTemplate.StructureBlockInfo(currentBlockInfo.pos(), blockState2, currentBlockInfo.nbt());
    }

}