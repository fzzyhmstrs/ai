package me.fzzyhmstrs.amethyst_imbuement.entity

import me.fzzyhmstrs.amethyst_core.entity_util.MissileEntity
import me.fzzyhmstrs.amethyst_core.interfaces.SpellCastingEntity
import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentConsumer
import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter_util.CustomDamageSources
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEntity
import me.fzzyhmstrs.fzzy_core.raycaster_util.RaycasterUtil
import me.fzzyhmstrs.fzzy_core.registry.EventRegistry
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.projectile.SmallFireballEntity
import net.minecraft.particle.ParticleEffect
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.math.MathHelper
import net.minecraft.world.World

class BallLightningEntity(entityType: EntityType<BallLightningEntity>, world: World): MissileEntity(entityType, world) {

    constructor(world: World,owner: LivingEntity, speed: Float, divergence: Float, x: Double, y: Double, z: Double) : this(RegisterEntity.BALL_LIGHTNING_ENTITY,world){
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

    override var entityEffects: AugmentEffect = AugmentEffect()
        .withDamage(5.4F,0.2F,0.0F)
        .withDuration(19,-1)
        .withRange(3.0,.25)
    override val maxAge: Int
        get() = 600
    var ticker = EventRegistry.ticker_20

    override fun passEffects(ae: AugmentEffect, level: Int) {
        super.passEffects(ae, level)
        entityEffects.setDuration(ae.duration(level))
        entityEffects.setRange(ae.range(level))
        ticker = EventRegistry.Ticker(ae.duration(level))
        EventRegistry.registerTickUppable(ticker)
    }

    override fun tick() {
        super.tick()
        if (world !is ServerWorld) return
        if (!ticker.isReady()) return
        if (owner == null || owner !is LivingEntity) return
        val entities = RaycasterUtil.raycastEntityArea(entityEffects.range(0),this,this.pos)
        for (entity in entities){
            if (entity is SpellCastingEntity && AiConfig.entities.isEntityPvpTeammate(owner as LivingEntity, entity,RegisterEnchantment.BALL_LIGHTNING)) continue
            if (entity !is LivingEntity) continue
            entity.damage(CustomDamageSources.LightningDamageSource(this),entityEffects.damage(0))
            beam(world as ServerWorld,entity)
            world.playSound(null,this.blockPos, SoundEvents.ITEM_TRIDENT_THUNDER, SoundCategory.NEUTRAL,0.3f,2.0f + world.random.nextFloat() * 0.4f - 0.2f)
        }
    }

    private fun beam(serverWorld: ServerWorld, entity: LivingEntity){
        val startPos = this.pos.add(0.0,0.25,0.0)
        val endPos = entity.pos.add(0.0,entity.height/2.0,0.0)
        val vec = endPos.subtract(startPos).multiply(0.2)
        var pos = startPos
        for (i in 1..5){
            serverWorld.spawnParticles(ParticleTypes.ELECTRIC_SPARK,pos.x,pos.y,pos.z,2,vec.x,vec.y,vec.z,0.0)
            pos = pos.add(vec)
        }

    }

    override fun onEntityHit(entityHitResult: EntityHitResult) {
    }

    override fun onRemoved() {
        EventRegistry.removeTickUppable(ticker)
    }

    override fun getParticleType(): ParticleEffect {
        return ParticleTypes.ELECTRIC_SPARK
    }

    companion object{
        fun createBallLightning(world: World, user: LivingEntity, speed: Float, div: Float, effects: AugmentEffect, level: Int): BallLightningEntity{
            val fbe = BallLightningEntity(
                world, user, speed, div,
                user.x - (user.width + 0.5f) * 0.5 * MathHelper.sin(user.bodyYaw * (Math.PI.toFloat() / 180)) * MathHelper.cos(
                    user.pitch * (Math.PI.toFloat() / 180)
                ),
                user.eyeY - 0.6 - 0.8 * MathHelper.sin(user.pitch * (Math.PI.toFloat() / 180)),
                user.z + (user.width + 0.5f) * 0.5 * MathHelper.cos(user.bodyYaw * (Math.PI.toFloat() / 180)) * MathHelper.cos(
                    user.pitch * (Math.PI.toFloat() / 180)
                ),
            )
            fbe.passEffects(effects, level)
            return fbe
        }
    }

}