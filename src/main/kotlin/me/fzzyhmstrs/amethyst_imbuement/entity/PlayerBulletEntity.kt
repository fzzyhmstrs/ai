package me.fzzyhmstrs.amethyst_imbuement.entity

import com.google.common.base.MoreObjects
import me.fzzyhmstrs.amethyst_core.entity_util.ModifiableDamageEntity
import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentEffect
import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentConsumer
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
        if (entity2 is LivingEntity) {
            val bl =
                entity.damage(DamageSource.mobProjectile(this, entity2).setProjectile(), entityEffects.damage(0))
            if (bl) {
                applyDamageEffects(entity2, entity)
                if (entity is LivingEntity) {
                    entity.addStatusEffect(
                        StatusEffectInstance(StatusEffects.LEVITATION, entityEffects.duration(0)),
                        MoreObjects.firstNonNull(entity2, this)
                    )
                    entityEffects.accept(entity, AugmentConsumer.Type.HARMFUL)
                }
                entityEffects.accept(entity2, AugmentConsumer.Type.BENEFICIAL)
            }
        }
    }


}