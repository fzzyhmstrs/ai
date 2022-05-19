package me.fzzyhmstrs.amethyst_imbuement.entity

import me.fzzyhmstrs.amethyst_imbuement.item.ScepterItem
import me.fzzyhmstrs.amethyst_imbuement.util.RaycasterUtil
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEntity
import me.fzzyhmstrs.amethyst_imbuement.scepter.ScepterObject
import me.fzzyhmstrs.amethyst_imbuement.scepter.base_augments.AugmentEffect
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
    override var damage: Float = 3.0F
    override var range: Double = 4.0
    override var duration: Int = 180


    override fun onEntityHit(entityHitResult: EntityHitResult) {
        super.onEntityHit(entityHitResult)
        val entity = owner
        if (entity is LivingEntity) {
            if (!entityHitResult.entity.isFireImmune) {
                (entityHitResult.entity as LivingEntity).frozenTicks = duration
                entityHitResult.entity.damage(DamageSource.GENERIC, damage)
            } else {
                (entityHitResult.entity as LivingEntity).frozenTicks = (duration * 1.6).toInt()
                entityHitResult.entity.damage(DamageSource.GENERIC, damage * 1.6F)
            }
            val entityList = RaycasterUtil.raycastEntityArea(distance = range, entityHitResult.entity)
            if (entityList.isNotEmpty()) {
                for (entity2 in entityList) {
                    if (entity2 is Monster) {
                        val effects = AugmentEffect().withDuration(duration).withDamage(damage)
                        RegisterEnchantment.FREEZING.entityTask(entity.world,entity2,entity,level.toDouble(),null,effects)
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