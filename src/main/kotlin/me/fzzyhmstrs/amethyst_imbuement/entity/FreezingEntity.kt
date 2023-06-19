package me.fzzyhmstrs.amethyst_imbuement.entity

import me.fzzyhmstrs.amethyst_core.entity_util.MissileEntity
import me.fzzyhmstrs.amethyst_core.interfaces.SpellCastingEntity
import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentConsumer
import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter_util.CustomDamageSources
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEntity
import me.fzzyhmstrs.fzzy_core.raycaster_util.RaycasterUtil
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.mob.Monster
import net.minecraft.particle.ParticleEffect
import net.minecraft.particle.ParticleTypes
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.world.World

class FreezingEntity(entityType: EntityType<FreezingEntity>, world: World): MissileEntity(entityType,world) {

    constructor(world: World,owner: LivingEntity,_level: Int) : this(RegisterEntity.FREEZING_ENTITY,world){
        this.level = _level
        this.owner = owner
        this.setPosition(
            owner.x,
            owner.eyeY - 0.4,
            owner.z
        )
        this.setRotation(owner.yaw, owner.pitch)
    }

    private var level: Int = 1
    override var entityEffects: AugmentEffect = AugmentEffect().withDamage(3.0F).withRange(4.0).withDuration(180)
    private var augment: ScepterAugment = RegisterEnchantment.FREEZING
    
    fun setAugment(aug: ScepterAugment){
        this.augment = aug
    }

    override fun passEffects(ae: AugmentEffect, level: Int) {
        super.passEffects(ae, level)
        entityEffects.setRange(ae.range(level))
        entityEffects.setDuration(ae.duration(level))
    }

    override fun onEntityHit(entityHitResult: EntityHitResult) {
        val entity = owner
        if (entity is LivingEntity) {
            val entity2 = entityHitResult.entity
            if (!(entity2 is SpellCastingEntity && AiConfig.entities.isEntityPvpTeammate(entity, entity2, augment))) {
                val bl = if (!entity2.isFireImmune) {
                    if (entity2 is LivingEntity) entity2.frozenTicks = entityEffects.duration(0)
                    entity2.damage(CustomDamageSources.freeze(world,this, entity), entityEffects.damage(0))
                } else {
                    if (entity2 is LivingEntity) entity2.frozenTicks = (entityEffects.duration() * 1.6).toInt()
                    entity2.damage(CustomDamageSources.freeze(world,this, entity), entityEffects.damage(0) * 1.6F)
                }
                if (bl) {
                    if (entity2 is LivingEntity) entityEffects.accept(entity2, AugmentConsumer.Type.HARMFUL)
                    applyDamageEffects(entity, entity2)
                    entityEffects.accept(entity, AugmentConsumer.Type.BENEFICIAL)
                }
                val entityList =
                    RaycasterUtil.raycastEntityArea(distance = entityEffects.range(0), entityHitResult.entity)
                if (entityList.isNotEmpty()) {
                    for (entity3 in entityList) {
                        if (entity3 is Monster) {
                            RegisterEnchantment.FREEZING.entityTask(
                                entity.world,
                                entity3,
                                entity,
                                level.toDouble(),
                                null,
                                entityEffects
                            )
                        }
                    }
                }
            }

        }
        discard()
    }

    override fun getParticleType(): ParticleEffect? {
        return ParticleTypes.SNOWFLAKE
    }

}
