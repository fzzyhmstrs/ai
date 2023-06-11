package me.fzzyhmstrs.amethyst_imbuement.entity

import me.fzzyhmstrs.amethyst_core.entity_util.ModifiableEffectEntity
import me.fzzyhmstrs.amethyst_core.interfaces.SpellCastingEntity
import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentConsumer
import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentEffect
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEntity
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.particle.ParticleEffect
import net.minecraft.particle.ParticleTypes
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import java.util.*
import kotlin.math.max

class BoneShardEntity(entityType: EntityType<out BoneShardEntity?>, world: World): BaseShardEntity(entityType, world), ModifiableEffectEntity {

    constructor(world: World, owner: LivingEntity, speed: Float, divergence: Float, pos: Vec3d, rot: Vec3d): this(
        RegisterEntity.BONE_SHARD_ENTITY,world){
        this.owner = owner
        this.setVelocity(rot.x,rot.y,rot.z,speed,divergence)
        this.setPosition(pos)
    }

    override var entityEffects: AugmentEffect = super.entityEffects.withDuration(180)


    override fun passEffects(ae: AugmentEffect, level: Int) {
        super<BaseShardEntity>.passEffects(ae, level)
        entityEffects.setDuration(ae.duration(level))
    }

    override fun onEntityHitSideEffects(entity: LivingEntity) {
        if (entity.world.random.nextFloat() < 0.1){
            entity.frozenTicks = entityEffects.duration(0)
        }
    }

    override fun particle(): ParticleEffect {
        return ParticleTypes.CRIT
    }



}
