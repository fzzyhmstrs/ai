package me.fzzyhmstrs.amethyst_imbuement.entity.spell

import com.google.common.base.MoreObjects
import me.fzzyhmstrs.amethyst_core.entity_util.ModifiableEffectEntity
import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentConsumer
import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.projectile.ShulkerBulletEntity
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.math.Direction
import net.minecraft.world.World

class PlayerBulletEntity: ShulkerBulletEntity, ModifiableEffectEntity {
    constructor(entityType: EntityType<out PlayerBulletEntity?>, world: World): super(entityType, world)
    constructor(world: World, owner: LivingEntity, target: Entity, axis: Direction.Axis): super(world, owner, target, axis)

    override var entityEffects: AugmentEffect = AugmentEffect().withDamage(4.0f).withDuration(140)
    private var augment: ScepterAugment = RegisterEnchantment.LEVITATING_BULLET
    
    fun setAugment(aug: ScepterAugment){
        this.augment = aug
    }

    override fun passEffects(ae: AugmentEffect, level: Int) {
        super.passEffects(ae, level)
        entityEffects.setDamage(ae.damage(level))
        entityEffects.addDuration(ae.duration(level))
    }

    override fun onEntityHit(entityHitResult: EntityHitResult) {
        super.onEntityHit(entityHitResult)
        val entity = entityHitResult.entity
        val entity2 = owner
        if (entity2 is LivingEntity && AiConfig.entities.shouldItHitBase(entity2, entity, augment)) {
            val bl = entity.damage(this.damageSources.mobProjectile(this, entity2), entityEffects.damage(0))
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
