package me.fzzyhmstrs.amethyst_imbuement.entity

import me.fzzyhmstrs.amethyst_imbuement.item.ScepterItem
import me.fzzyhmstrs.amethyst_imbuement.util.RaycasterUtil
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEntity
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.mob.Monster
import net.minecraft.entity.projectile.ExplosiveProjectileEntity
import net.minecraft.entity.projectile.ProjectileUtil
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket
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


    override fun onEntityHit(entityHitResult: EntityHitResult) {
        super.onEntityHit(entityHitResult)
        val entity = owner
        if (entity is LivingEntity) {
            (entityHitResult.entity as LivingEntity).frozenTicks = 180 + 100 * level
            entityHitResult.entity.damage(DamageSource.GENERIC,4.0F)
        }
        val entityList = RaycasterUtil.raycastEntityArea(distance = 4.0 + level,pos = entityHitResult.entity.blockPos)
        println(this.blockPos)
        println(entityHitResult.entity.blockPos)
        println(entityList)
        if (entityList.isNotEmpty()){
            for (entity2 in entityList){
                if (entity2 is Monster){
                    if (entity is LivingEntity) {
                        ScepterItem.addEntityToQueue(
                            entity2.uuid,
                            ScepterItem.EntityTaskInstance(
                                RegisterEnchantment.FREEZING,
                                entity,
                                level.toDouble(),
                                null
                            )
                        )
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