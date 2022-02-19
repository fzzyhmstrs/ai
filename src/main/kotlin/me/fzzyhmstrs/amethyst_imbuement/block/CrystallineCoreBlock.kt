package me.fzzyhmstrs.amethyst_imbuement.block

import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterBlock
import net.minecraft.block.*
import net.minecraft.block.pattern.BlockPattern
import net.minecraft.block.pattern.BlockPatternBuilder
import net.minecraft.block.pattern.CachedBlockPosition
import net.minecraft.predicate.block.BlockStatePredicate
import net.minecraft.util.function.MaterialPredicate
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
                    .where('~', CachedBlockPosition.matchesBlockState(MaterialPredicate.create(Material.AIR))).build()
                pattern = crystallineGolemPattern as BlockPattern
            } else {
                pattern = crystallineGolemPattern as BlockPattern
            }
            return pattern
        }
    }

}