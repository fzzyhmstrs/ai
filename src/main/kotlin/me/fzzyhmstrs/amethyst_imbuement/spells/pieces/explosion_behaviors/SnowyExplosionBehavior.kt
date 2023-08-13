package me.fzzyhmstrs.amethyst_imbuement.spells.pieces.explosion_behaviors

import me.fzzyhmstrs.amethyst_core.augments.CustomExplosion
import net.minecraft.block.Blocks
import net.minecraft.entity.Entity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class SnowyExplosionBehavior: CustomExplosion.CustomExplosionBehavior() {
    override fun setFireBlockState(world: World, pos: BlockPos) {
        world.setBlockState(pos,Blocks.SNOW.defaultState)
    }

    override fun affectEntity(entity: Entity) {
        entity.frozenTicks = 180
    }
}