package me.fzzyhmstrs.amethyst_imbuement.entity

import me.fzzyhmstrs.amethyst_core.entity_util.ModifiableEffectEntity
import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentConsumer
import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentEffect
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEntity
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.projectile.AbstractFireballEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.Vec3d
import net.minecraft.world.GameRules
import net.minecraft.world.World

class PlayerFireballEntity: AbstractFireballEntity, ModifiableEffectEntity {
    constructor(entityType: EntityType<out PlayerFireballEntity?>, world: World): super(entityType, world)
    constructor(world: World, owner: LivingEntity, velocityX: Double, velocityY: Double, velocityZ: Double):
            super(RegisterEntity.PLAYER_FIREBALL,owner,velocityX,velocityY,velocityZ, world)

    override var entityEffects: AugmentEffect = AugmentEffect().withDamage(6.0F).withAmplifier(1)

    override fun passEffects(ae: AugmentEffect, level: Int) {
        super.passEffects(ae, level)
        entityEffects.setDamage(ae.damage(level))
        entityEffects.addAmplifier(ae.amplifier(level))
    }

    override fun onCollision(hitResult: HitResult) {
        super.onCollision(hitResult)
        if (!world.isClient) {
            val bl = world.gameRules.getBoolean(GameRules.DO_MOB_GRIEFING)
            world.createExplosion(
                null,
                this.x,
                this.y,
                this.z,
                entityEffects.amplifier(0).toFloat(),
                bl,
                if (bl) World.ExplosionSourceType.TNT else World.ExplosionSourceType.NONE
            )
            discard()
        }
    }

    override fun onEntityHit(entityHitResult: EntityHitResult) {
        super.onEntityHit(entityHitResult)
        if (world.isClient) {
            return
        }
        val entity = entityHitResult.entity
        val entity2 = owner
        entity.damage(DamageSource.fireball(this, entity2), entityEffects.damage(0))
        if (entity2 is LivingEntity) {
            if (entity is LivingEntity) {
                entityEffects.accept(entity, AugmentConsumer.Type.HARMFUL)
            }
            entityEffects.accept(entity2, AugmentConsumer.Type.BENEFICIAL)
            applyDamageEffects(entity2 as LivingEntity?, entity)
        }
    }

    override fun writeCustomDataToNbt(nbt: NbtCompound) {
        super.writeCustomDataToNbt(nbt)
        nbt.putByte("ExplosionPower", entityEffects.amplifier(0).toByte())
    }

    override fun readCustomDataFromNbt(nbt: NbtCompound) {
        super.readCustomDataFromNbt(nbt)
        if (nbt.contains("ExplosionPower", 99)) {
            entityEffects.setAmplifier(nbt.getByte("ExplosionPower").toInt())
        }
    }

    companion object{
        fun createFireball(world: World, user: LivingEntity, vel: Vec3d, pos: Vec3d, effects: AugmentEffect, level: Int): PlayerFireballEntity{
            val fbe = PlayerFireballEntity(world, user, vel.x, vel.y, vel.z)
            fbe.passEffects(effects, level)
            fbe.setPos(pos.x,pos.y,pos.z)
            return fbe
        }
    }
}