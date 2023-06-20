package me.fzzyhmstrs.amethyst_imbuement.block

import me.fzzyhmstrs.amethyst_imbuement.entity.living.CrystallineGolemEntity
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterBlock
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEntity.CRYSTAL_GOLEM_ENTITY
import net.minecraft.advancement.criterion.Criteria
import net.minecraft.block.AmethystBlock
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.block.pattern.BlockPattern
import net.minecraft.block.pattern.BlockPatternBuilder
import net.minecraft.block.pattern.CachedBlockPosition
import net.minecraft.predicate.block.BlockStatePredicate
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.WorldEvents
import java.util.function.Predicate

class CrystallineCoreBlock(settings: Settings): AmethystBlock( settings) {

    companion object{
        private var crystallineGolemPattern: BlockPattern? = null
        private val IS_GOLEM_HEAD_PREDICATE =
            Predicate { state: BlockState? ->
                state != null && (state.isOf(Blocks.CARVED_PUMPKIN) || state.isOf(
                    Blocks.JACK_O_LANTERN
                ))
            }

        fun getCrystallineGolemPattern(): BlockPattern {
            val pattern : BlockPattern
            if (this.crystallineGolemPattern == null) {
                this.crystallineGolemPattern = BlockPatternBuilder.start().aisle("~^~", "#$#", "~#~")
                    .where('^', CachedBlockPosition.matchesBlockState(IS_GOLEM_HEAD_PREDICATE))
                    .where('#', CachedBlockPosition.matchesBlockState(BlockStatePredicate.forBlock(Blocks.AMETHYST_BLOCK)))
                    .where('$', CachedBlockPosition.matchesBlockState(BlockStatePredicate.forBlock(RegisterBlock.CRYSTALLINE_CORE_BLOCK)))
                    .where('~') { pos: CachedBlockPosition -> pos.blockState.isAir }.build()
                pattern = crystallineGolemPattern as BlockPattern
            } else {
                pattern = crystallineGolemPattern as BlockPattern
            }
            return pattern
        }

        fun spawnCrystallineGolem(result: BlockPattern.Result, world: World){
            var cachedBlockPosition: CachedBlockPosition
            for (i in 0 until getCrystallineGolemPattern().width) {
                for (j in 0 until getCrystallineGolemPattern().height) {
                    cachedBlockPosition = result.translate(i, j, 0)
                    world.setBlockState(cachedBlockPosition.blockPos, Blocks.AIR.defaultState, NOTIFY_LISTENERS)
                    world.syncWorldEvent(
                        WorldEvents.BLOCK_BROKEN,
                        cachedBlockPosition.blockPos,
                        getRawIdFromState(cachedBlockPosition.blockState)
                    )
                }
            }
            val cge = CrystallineGolemEntity(CRYSTAL_GOLEM_ENTITY, world)

            val blockPos: BlockPos = result.translate(1, 2, 0).getBlockPos()
            cge.refreshPositionAndAngles(
                blockPos.x.toDouble() + 0.5,
                blockPos.y.toDouble() + 0.05,
                blockPos.z.toDouble() + 0.5,
                0.0f,
                0.0f
            )
            val owner = world.getClosestPlayer(cge.x,cge.y,cge.z,6.0,false)
            cge.setConstructOwner(owner)
            world.spawnEntity(cge)
            for (serverPlayerEntity in world.getNonSpectatingEntities(
                ServerPlayerEntity::class.java, cge.boundingBox.expand(5.0)
            )) {
                Criteria.SUMMONED_ENTITY.trigger(serverPlayerEntity, cge)
            }
            for (i in 0 until getCrystallineGolemPattern().width) {
                for (j in 0 until getCrystallineGolemPattern().height) {
                    val cachedBlockPosition2: CachedBlockPosition = result.translate(i, j, 0)
                    world.updateNeighbors(cachedBlockPosition2.blockPos, Blocks.AIR)
                }
            }
        }
    }

}