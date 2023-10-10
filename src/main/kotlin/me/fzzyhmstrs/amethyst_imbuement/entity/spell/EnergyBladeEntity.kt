package me.fzzyhmstrs.amethyst_imbuement.entity.spell

import me.fzzyhmstrs.amethyst_core.entity_util.ModifiableEffectEntity
import me.fzzyhmstrs.amethyst_core.interfaces.SpellCastingEntity
import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentConsumer
import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEntity
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.projectile.ExplosiveProjectileEntity
import net.minecraft.entity.projectile.ProjectileUtil
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket
import net.minecraft.particle.ParticleEffect
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.RaycastContext
import net.minecraft.world.RaycastContext.FluidHandling
import net.minecraft.world.World
import java.util.*
import kotlin.math.max

/**
 * basic missile projectile for use with spells or any other projectile-lobbing object.
 *
 * Extend and modify as you might any other projectile, like arrows, with the added functionality of the Modifiability.
 *
 * See [ModifiableEffectEntity] for more info. In general, modifiable entities can pass modifications from the object that spawned it to the projectile damage/effect itself. See below for a basic implementation of the modifiable effect.
 *
 * The [entityEffects] bucket holds attributes like damage and range that can be used in the [onEntityHit] methods and others. This allows for dynamic damage rather than defining one static value. As seen below, 3.0 damage is set as a default value, but if passEffects is called and it's parameter has a different damage, the entity will deal that modified amount on hit.
 *
 * Similarly, if the effect is provided with a consumer, on hit the missile will apply any of those consumers marked as harmful. An example consumer would be one that applies 10 seconds of blindness to any affected entity. Basically a bucket for applying secondary effects on hit. See [AugmentEffect] for more info.
 */

open class EnergyBladeEntity(entityType: EntityType<out EnergyBladeEntity?>, world: World): ExplosiveProjectileEntity(entityType,world),
    ModifiableEffectEntity {

    constructor(world: World,owner: LivingEntity) : this(RegisterEntity.ENERGY_BLADE,world){
        this.owner = owner
        this.setPosition(
            owner.x,
            owner.eyeY - 0.4,
            owner.z
        )
        this.setRotation(owner.yaw, owner.pitch)
    }

    private var pierce: Boolean = false
    override var entityEffects: AugmentEffect = AugmentEffect().withDamage(8.0F)
    open val maxAge = 100
    private val struckEntities: MutableList<UUID> = mutableListOf()
    protected var scepterAugment: ScepterAugment = RegisterEnchantment.ICE_SHARD

    fun setAugment(aug: ScepterAugment){
        this.scepterAugment = aug
    }
    override fun passEffects(ae: AugmentEffect, level: Int) {
        super.passEffects(ae, level)
        entityEffects.setDamage(ae.damage(level))
    }

    override fun initDataTracker() {}

    override fun tick() {
        super.tick()
        if (isRemoved)
            return
        if (age > maxAge){
            discard()
        }
        val vec3d = velocity
        val hitResult = getMissileCollision()
        onCollision(hitResult)
        val x2 = vec3d.x
        val y2 = vec3d.y
        val z2 = vec3d.z
        val d = this.x + x2
        val e = this.y + y2
        val f = this.z + z2
        this.updateRotation()
        val g = drag.toDouble()
        addParticles(x2, y2, z2)
        val gg: Double = if (this.isTouchingWater) {
            0.95
        } else {
            g
        }
        velocity = vec3d.multiply(gg)
        if (!hasNoGravity()) {
            velocity = velocity.add(0.0, -0.0, 0.0)
        }
        this.setPosition(d, e, f)
    }

    private fun getMissileCollision(): HitResult{
        var vec3d = pos.add(velocity)
        var hitResult: HitResult = world.raycast(
            RaycastContext(
                pos,
                vec3d,
                RaycastContext.ShapeType.COLLIDER,
                fluidHandling(),
                this
            )
        )
        if (hitResult.type != HitResult.Type.MISS) {
            vec3d = hitResult.pos
        }
        val hitResult2 = ProjectileUtil.getEntityCollision(
            world,
            this,
            pos,
            vec3d,
            this.boundingBox.stretch(velocity).expand(1.0)
        ) { target -> canHit(target) }
        if (hitResult2 != null) {
            hitResult = hitResult2
        }
        return hitResult

    }

    open fun fluidHandling(): FluidHandling{
        return FluidHandling.NONE
    }

    override fun onEntityHit(entityHitResult: EntityHitResult) {
        if (world.isClient) {
            return
        }
        val entity = owner
        if (entity is LivingEntity) {
            val entity2 = entityHitResult.entity
            if (!(entity2 is SpellCastingEntity && AiConfig.entities.isEntityPvpTeammate(entity, entity2, scepterAugment))){
                val bl = entity2.damage(entity.damageSources.mobProjectile(this,entity),
                    max(1f,entityEffects.damage(0) - struckEntities.size)
                )
                if (!struckEntities.contains(entity2.uuid)){
                    struckEntities.add(entity2.uuid)
                }
                if (bl) {
                    entityEffects.accept(entity, AugmentConsumer.Type.BENEFICIAL)
                    applyDamageEffects(entity as LivingEntity?, entity2)
                    if (entity2 is LivingEntity) {
                        entityEffects.accept(entity2, AugmentConsumer.Type.HARMFUL)
                    }
                }
            }
        }
    }

    override fun onBlockHit(blockHitResult: BlockHitResult) {
        super.onBlockHit(blockHitResult)
        onMissileBlockHit(blockHitResult)
        discard()
    }

    open fun onMissileBlockHit(blockHitResult: BlockHitResult){
        splashParticles(pos,world)
        playHitSound(world,blockPos)
    }

    override fun writeCustomDataToNbt(nbt: NbtCompound) {
        super.writeCustomDataToNbt(nbt)
    }

    override fun readCustomDataFromNbt(nbt: NbtCompound) {
        super.readCustomDataFromNbt(nbt)
    }

    override fun damage(source: DamageSource, amount: Float): Boolean {
        return false
    }

    override fun isBurning(): Boolean {
        return false
    }

    override fun getDrag(): Float {
        return 0.999999f
    }

    override fun onSpawnPacket(packet: EntitySpawnS2CPacket) {
        super.onSpawnPacket(packet)
        val d = packet.velocityX
        val e = packet.velocityY
        val f = packet.velocityZ
        this.setVelocity(d, e, f)
    }

    override fun getParticleType(): ParticleEffect? {
        return ParticleTypes.CRIT
    }

    open fun hitParticleType(): ParticleEffect?{
        return particleType
    }

    open fun playHitSound(world: World,pos: BlockPos){
        world.playSound(null,pos,SoundEvents.ENTITY_DRAGON_FIREBALL_EXPLODE,SoundCategory.PLAYERS,0.3f,1.0f)
    }

    open fun splashParticles(pos: Vec3d, world: World){
        if (world is ServerWorld){
            world.spawnParticles(hitParticleType(),pos.x,pos.y,pos.z,20,.25,.25,.25,0.2)
        }
    }

    open fun addParticles(x2: Double, y2: Double, z2: Double){
        val particleWorld = world
        if (particleWorld !is ServerWorld) return
        if (this.isTouchingWater) {
            particleWorld.spawnParticles(ParticleTypes.BUBBLE,this.x,this.y,this.z,3,1.0,1.0,1.0,0.0)
        } else {
            particleWorld.spawnParticles(particleType,this.x,this.y,this.z,3,1.0,1.0,1.0,0.0)
        }
    }

}