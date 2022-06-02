package me.fzzyhmstrs.amethyst_imbuement.entity

import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEntity
import me.fzzyhmstrs.amethyst_imbuement.scepter.base_augments.AugmentConsumer
import me.fzzyhmstrs.amethyst_imbuement.scepter.base_augments.AugmentEffect
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.projectile.SmallFireballEntity
import net.minecraft.particle.ParticleEffect
import net.minecraft.particle.ParticleTypes
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.world.World

class FlameboltEntity(entityType: EntityType<FlameboltEntity>, world: World): MissileEntity(entityType,world) {

    override var entityEffects: AugmentEffect = AugmentEffect().withDamage(6.0F).withAmplifier(5)

    override fun passEffects(ae: AugmentEffect, level: Int) {
        super.passEffects(ae, level)
        ae.addAmplifier(ae.amplifier(level))
    }

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
        val entity = owner
        if (entity is LivingEntity) {
            val entity2 = entityHitResult.entity
            val fbe = SmallFireballEntity(EntityType.SMALL_FIREBALL,world)
            if (!entity2.isFireImmune) {
                val i = entity2.fireTicks
                entity2.setOnFireFor(entityEffects.amplifier(0))
                val bl = entity2.damage(
                    DamageSource.fireball(fbe,owner),
                    entityEffects.damage(0)
                )
                if (!bl) {
                    entity2.fireTicks = i
                }
                applyDamageEffects(entity as LivingEntity?, entity2)
            }
            if (entity2 is LivingEntity) {
                entityEffects.accept(entity2, AugmentConsumer.Type.HARMFUL)
            }
        }

        discard()
    }

    override fun isBurning(): Boolean {
        return this.age > 1
    }

    override fun getParticleType(): ParticleEffect {
        return ParticleTypes.FLAME
    }

}