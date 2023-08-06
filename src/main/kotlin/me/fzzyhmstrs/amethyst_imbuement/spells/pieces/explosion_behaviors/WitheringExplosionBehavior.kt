package me.fzzyhmstrs.amethyst_imbuement.spells.pieces.explosion_behaviors

import me.fzzyhmstrs.amethyst_core.augments.CustomExplosion
import net.minecraft.block.SoulFireBlock
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class WitheringExplosionBehavior: CustomExplosion.CustomExplosionBehavior() {
    override fun affectEntity(entity: Entity) {
        if (entity is LivingEntity){
            entity.addStatusEffect(StatusEffectInstance(StatusEffects.WITHER,100,1))
        }
    }
    override fun setFireBlockState(world: World, pos: BlockPos){
        world.setBlockState(pos, SoulFireBlock.getState(world, pos))
    }
}