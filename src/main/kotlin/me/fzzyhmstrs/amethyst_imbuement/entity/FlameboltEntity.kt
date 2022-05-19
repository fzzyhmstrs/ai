package me.fzzyhmstrs.amethyst_imbuement.entity

import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEntity
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.projectile.SmallFireballEntity
import net.minecraft.particle.ParticleEffect
import net.minecraft.particle.ParticleTypes
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.world.World

class FlameboltEntity(entityType: EntityType<FlameboltEntity>, world: World): MissileEntity(entityType,world) {

    override var damage: Float = 6.0F
    override var amplifier: Int = 5

    constructor(world: World,owner: LivingEntity, speed: Float, divergence: Float, x: Double, y: Double, z: Double) : this(RegisterEntity.FLAMEBOLT_ENTITY,world){
        this.owner = owner
        this.setVelocity(owner,
            owner.pitch,
            owner.yaw,
            0.0f,
            speed,
            divergence)
        this.setPosition(x,y,z)
        this.setRotation(owner.yaw, owner.pitch)
    }

    override fun onEntityHit(entityHitResult: EntityHitResult) {
        super.onEntityHit(entityHitResult)
        val entity2 = owner
        if (entity2 is LivingEntity) {
            val entity = entityHitResult.entity
            val fbe = SmallFireballEntity(EntityType.SMALL_FIREBALL,world)
            if (!entity.isFireImmune) {
                val i = entity.fireTicks
                entity.setOnFireFor(amplifier)
                val bl = entity.damage(
                    DamageSource.fireball(fbe,owner),
                    damage
                )
                if (!bl) {
                    entity.fireTicks = i
                }
                applyDamageEffects(entity2 as LivingEntity?, entity)
            }
        }
        discard()
    }

    override fun isBurning(): Boolean {
        return true
    }

    override fun getParticleType(): ParticleEffect {
        return ParticleTypes.FLAME
    }

}