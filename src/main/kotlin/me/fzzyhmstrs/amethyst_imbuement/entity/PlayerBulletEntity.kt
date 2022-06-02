package me.fzzyhmstrs.amethyst_imbuement.entity

import com.google.common.base.MoreObjects
import me.fzzyhmstrs.amethyst_imbuement.scepter.base_augments.AugmentConsumer
import me.fzzyhmstrs.amethyst_imbuement.scepter.base_augments.AugmentEffect
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.projectile.ShulkerBulletEntity
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.math.Direction
import net.minecraft.world.World

class PlayerBulletEntity: ShulkerBulletEntity, ModifiableDamageEntity {
    constructor(entityType: EntityType<out PlayerBulletEntity?>, world: World): super(entityType, world)
    constructor(world: World, owner: LivingEntity, target: Entity, axis: Direction.Axis): super(world, owner, target, axis)

    override var entityEffects: AugmentEffect = AugmentEffect().withDamage(4.0f).withDuration(140)

    override fun passEffects(ae: AugmentEffect, level: Int) {
        super.passEffects(ae, level)
        entityEffects.setDamage(ae.damage(level))
        entityEffects.addDuration(ae.duration(level))
    }

    override fun onEntityHit(entityHitResult: EntityHitResult) {
        super.onEntityHit(entityHitResult)
        val entity = entityHitResult.entity
        val entity2 = owner
        val livingEntity = if (entity2 is LivingEntity) entity2 else null
        val bl = entity.damage(DamageSource.mobProjectile(this, livingEntity).setProjectile(), entityEffects.damage(0))
        if (bl) {
            applyDamageEffects(livingEntity, entity)
            if (entity is LivingEntity) {
                entity.addStatusEffect(
                    StatusEffectInstance(StatusEffects.LEVITATION, entityEffects.duration(0)),
                    MoreObjects.firstNonNull(entity2, this)
                )
                entityEffects.accept(entity, AugmentConsumer.Type.HARMFUL)
            }
        }
    }


}