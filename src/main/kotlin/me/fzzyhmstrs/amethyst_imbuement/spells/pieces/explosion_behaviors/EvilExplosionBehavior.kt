package me.fzzyhmstrs.amethyst_imbuement.spells.pieces.explosion_behaviors

import me.fzzyhmstrs.amethyst_core.augments.CustomExplosion
import net.minecraft.block.SoulFireBlock
import net.minecraft.entity.AreaEffectCloudEntity
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.Tameable
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.projectile.ProjectileEntity
import net.minecraft.particle.ParticleTypes
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class EvilExplosionBehavior: CustomExplosion.CustomExplosionBehavior() {
    override fun extraExplosionEffects(x: Double, y: Double, z: Double, world: World, source: Entity?) {
        if (world.isClient) return
        val areaEffectCloudEntity = AreaEffectCloudEntity(world, x, y, z)
        areaEffectCloudEntity.particleType = ParticleTypes.SOUL
        areaEffectCloudEntity.duration = 800
        areaEffectCloudEntity.radius = 3.0f
        if (source is ProjectileEntity){
            val owner = source.owner
            if (owner is LivingEntity)
                areaEffectCloudEntity.owner = owner
        } else if (source is Tameable){
            val owner = source.owner
            areaEffectCloudEntity.owner = owner
        }
        areaEffectCloudEntity.radiusGrowth = (7.0f - areaEffectCloudEntity.radius) / areaEffectCloudEntity.duration.toFloat()
        areaEffectCloudEntity.addEffect(StatusEffectInstance(StatusEffects.WITHER, 80, 1))
        areaEffectCloudEntity.setPosition(x,y,z)
        world.spawnEntity(areaEffectCloudEntity)
    }
    override fun setFireBlockState(world: World, pos: BlockPos){
        world.setBlockState(pos, SoulFireBlock.getState(world, pos))
    }
}