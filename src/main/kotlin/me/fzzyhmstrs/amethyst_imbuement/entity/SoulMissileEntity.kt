package me.fzzyhmstrs.amethyst_imbuement.entity

import me.fzzyhmstrs.amethyst_core.entity_util.MissileEntity
import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentConsumer
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEntity
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.damage.DamageSource
import net.minecraft.particle.ParticleEffect
import net.minecraft.particle.ParticleTypes
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.world.World

class SoulMissileEntity: MissileEntity {

    constructor(entityType: EntityType<SoulMissileEntity>, world: World): super(entityType, world)

    constructor(world: World, owner: LivingEntity) : super(RegisterEntity.SOUL_MISSILE_ENTITY, world){
        this.owner = owner
        this.setPosition(
            owner.x,
            owner.eyeY - 0.4,
            owner.z
        )
        this.setRotation(owner.yaw, owner.pitch)
    }

    override fun onEntityHit(entityHitResult: EntityHitResult) {
        if (world.isClient) {
            return
        }
        val entity = owner
        if (entity is LivingEntity) {
            val entity2 = entityHitResult.entity
            val bl: Boolean = entity2.damage(
                    DamageSource.magic(this, entity).setProjectile(),
                    entityEffects.damage(0)
                )

            if (bl) {
                applyDamageEffects(entity, entity2)
                if (entity2 is LivingEntity) {
                    entityEffects.accept(entity2, AugmentConsumer.Type.HARMFUL)
                }
            }
        }
        discard()
    }

    override fun getParticleType(): ParticleEffect {
        return ParticleTypes.SOUL
    }
}