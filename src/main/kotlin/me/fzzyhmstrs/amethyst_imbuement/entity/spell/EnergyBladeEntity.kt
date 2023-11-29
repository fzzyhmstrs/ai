package me.fzzyhmstrs.amethyst_imbuement.entity.spell

import me.fzzyhmstrs.amethyst_core.entity_util.ModifiableEffectEntity
import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentConsumer
import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter_util.SpellDamageSource
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.mixins.ProjectileEntityAccessor
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEntity
import me.fzzyhmstrs.fzzy_core.raycaster_util.RaycasterUtil
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.projectile.ExplosiveProjectileEntity
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
import net.minecraft.util.math.Direction
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import net.minecraft.world.RaycastContext
import net.minecraft.world.RaycastContext.FluidHandling
import net.minecraft.world.World
import net.minecraft.world.event.GameEvent

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
        this.updatePositionAndAngles(owner.x, owner.eyeY - (user.height * 0.25), owner.z, owner.yaw, owner.pitch)
    }

    override var entityEffects: AugmentEffect = AugmentEffect().withDamage(8.0F).withRange(5.0)
    open var maxAge = 100
    protected var scepterAugment: ScepterAugment = RegisterEnchantment.ENERGY_BLADE
    private var blockHits = 0

    fun setAugment(aug: ScepterAugment){
        this.scepterAugment = aug
    }
    override fun passEffects(ae: AugmentEffect, level: Int) {
        super.passEffects(ae, level)
        entityEffects.setDamage(ae.damage(level))
        maxAge = ae.amplifier(level)
        entityEffects.setRange(ae.range(level))
    }

    override fun initDataTracker() {}

    override fun tick() {
        //println("Top of Tick:  ${this.yaw}")
        //println("Top of Tick prev:  ${this.prevYaw}")
        val entity = owner
        if (!world.isClient && (entity != null && entity.isRemoved || !world.isChunkLoaded(blockPos))) {
            discard()
            return
        }
        if (isRemoved)
            return
        if (age > maxAge){
            discard()
        }

        //println("After refresh:  ${this.yaw}")
        //println("After refresh prev:  ${this.prevYaw}")
        if (!(this as ProjectileEntityAccessor).isShot) {
            this.emitGameEvent(GameEvent.PROJECTILE_SHOOT, owner)
            (this as ProjectileEntityAccessor).isShot = true
        }
        if (!(this as ProjectileEntityAccessor).isLeftOwner) {
            (this as ProjectileEntityAccessor).isLeftOwner = shouldLeaveOwner()
        }
        baseTick()
        if (this.isBurning) {
            setOnFireFor(1)
        }

        val hitResult = getMissileBlockCollision()
        if (hitResult.type == HitResult.Type.BLOCK && hitResult is BlockHitResult){
            onBlockHit(hitResult)
        }
        val entityHits = getMissileEntityCollision()
        for (hit in entityHits){
            onEntityHit(hit)
        }
        val vec3d = velocity
        if (prevPitch == 0.0f && prevYaw == 0.0f) {
            yaw = -(MathHelper.atan2(vec3d.x, vec3d.z) * 57.2957763671875).toFloat()
            pitch = -(MathHelper.atan2(vec3d.y, vec3d.horizontalLength()) * 57.2957763671875).toFloat()
            prevYaw = yaw
            prevPitch = pitch
        }
        //onCollision(hitResult)
        val x2 = vec3d.x
        val y2 = vec3d.y
        val z2 = vec3d.z
        val d = this.x + x2
        val e = this.y + y2
        val f = this.z + z2

        addParticles(x2, y2, z2)
        val gg: Double = if (this.isTouchingWater) {
            0.95
        } else {
            drag.toDouble()
        }
        velocity = vec3d.multiply(gg)
        if (velocity.lengthSquared() == 0.0) {
            return
        }
        this.yaw = -(MathHelper.atan2(velocity.x, velocity.z) * 57.2957763671875).toFloat()
        this.pitch = -(MathHelper.atan2(velocity.y,velocity.horizontalLength()) * 57.2957763671875).toFloat()


        //ProjectileUtil.setRotationFromVelocity(this,0.5f)
        //println("Bottom Tick:  ${this.yaw}")
        //println("Bottom Tick prev:  ${this.prevYaw}")
/*        if (!hasNoGravity()) {
            velocity = velocity.add(0.0, -0.0, 0.0)
        }*/
        //world.addParticle(this.particleType, d, e + 0.5, f, 0.0, 0.0, 0.0)
        this.setPosition(d, e, f)
    }

    override fun setVelocityClient(x: Double, y: Double, z: Double) {
        this.setVelocity(x, y, z)
        if (prevPitch == 0.0f && prevYaw == 0.0f) {
            val d = Math.sqrt(x * x + z * z)
            pitch = -(MathHelper.atan2(y, d) * 57.2957763671875).toFloat()
            yaw = -(MathHelper.atan2(x, z) * 57.2957763671875).toFloat()
            prevPitch = pitch
            prevYaw = yaw
            this.refreshPositionAndAngles(x, y, z, yaw, pitch)
        }
    }

    private fun shouldLeaveOwner(): Boolean {
        val entity2 = owner
        if (entity2 != null) {
            for (entity22 in world.getOtherEntities(
                this, boundingBox.stretch(velocity).expand(1.0)
            ) { entity: Entity -> !entity.isSpectator && entity.canHit() }) {
                if (entity22.rootVehicle !== entity2.rootVehicle) continue
                return false
            }
        }
        return true
    }

    private fun getMissileBlockCollision(): HitResult{
        val hit = world.raycast(
            RaycastContext(
                pos,
                pos.add(velocity),
                RaycastContext.ShapeType.COLLIDER,
                fluidHandling(),
                this
            )
        )
        if (hit.type == HitResult.Type.BLOCK)
            blockHits++
        if (blockHits >= entityEffects.range(0)) {
            discard()
        }
        /*if (hit is BlockHitResult && hit.type != HitResult.Type.MISS){
            if (age - lastBlockHit <= 1)
                discard()
            lastBlockHit = age
        }*/
        return hit
    }

    private fun getMissileEntityCollision(): List<EntityHitResult>{
        if (world.isClient) return listOf()
        val rotation = rotationVector
        val flatRotation = rotation.multiply(1.0,0.0,1.0)
        val flatPerpendicular = RaycasterUtil.perpendicularVector(flatRotation,RaycasterUtil.InPlane.XZ)
        return RaycasterUtil.raycastEntityRotatedArea(
            (world as ServerWorld).iterateEntities(),
            this,
            pos.add(velocity),
            rotation,
            flatPerpendicular,
            1.5,
            2.75,
            1.5).map{EntityHitResult(it)}
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
            if (AiConfig.entities.shouldItHitBase(entity, entity2, scepterAugment)){
                val bl = entity2.damage(SpellDamageSource(entity.damageSources.indirectMagic(this,entity),scepterAugment),entityEffects.damage(0))
                if (bl) {
                    world.playSound(null,blockPos,SoundEvents.ENTITY_PLAYER_ATTACK_CRIT,SoundCategory.PLAYERS,0.5f,1.0f)
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
        splashParticles(pos,world)
        playHitSound(world,blockPos)
        velocity = when(blockHitResult.side.axis){
            Direction.Axis.X -> Vec3d(velocity.x * -1.0, velocity.y, velocity.z)
            Direction.Axis.Y -> Vec3d(velocity.x, velocity.y * -1.0, velocity.z)
            Direction.Axis.Z -> Vec3d(velocity.x, velocity.y, velocity.z * -1.0)
            else -> velocity
        }
        super.onBlockHit(blockHitResult)
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
        //ProjectileUtil.setRotationFromVelocity(this,1f)
    }

    override fun getParticleType(): ParticleEffect? {
        return ParticleTypes.ENCHANTED_HIT
    }

    open fun hitParticleType(): ParticleEffect?{
        return particleType
    }

    open fun playHitSound(world: World,pos: BlockPos){
        world.playSound(null,pos,SoundEvents.ENTITY_PLAYER_ATTACK_CRIT,SoundCategory.PLAYERS,0.3f,1.0f)
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
