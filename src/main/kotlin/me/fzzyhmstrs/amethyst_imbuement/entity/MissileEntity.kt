package me.fzzyhmstrs.amethyst_imbuement.entity

import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEntity
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.projectile.ExplosiveProjectileEntity
import net.minecraft.entity.projectile.ProjectileUtil
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket
import net.minecraft.particle.ParticleEffect
import net.minecraft.particle.ParticleTypes
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.world.World

class MissileEntity(entityType: EntityType<MissileEntity>, world: World): ExplosiveProjectileEntity(entityType,world) {

    constructor(world: World,owner: LivingEntity,_pierce: Boolean) : this(RegisterEntity.MISSILE_ENTITY,world){
        this.pierce = _pierce
        this.owner = owner
        this.setPosition(
            owner.x,
            owner.eyeY - 0.4,
            owner.z
        )
        this.setRotation(owner.yaw, owner.pitch)
    }

    private var pierce: Boolean = false

    override fun initDataTracker() {}

    override fun tick() {
        super.tick()
        if (age > 200){
            discard()
        }
        val vec3d = velocity
        val hitResult = ProjectileUtil.getCollision(
            this
        ) { entity: Entity ->
            canHit(
                entity
            )
        }
        onCollision(hitResult)
        val x2 = vec3d.x
        val y2 = vec3d.y
        val z2 = vec3d.z
        val d = this.x + x2
        val e = this.y + y2
        val f = this.z + z2
        this.updateRotation()
        val g = 0.999999
        val gg: Double
        val h = 0.0
        if (this.isTouchingWater) {
            for (i in -1..1) {
                world.addParticle(
                    ParticleTypes.BUBBLE,
                    this.x + x2 * (world.random.nextFloat()-0.5f),
                    this.y + x2 * (world.random.nextFloat()-0.5f),
                    this.z + x2 * (world.random.nextFloat()-0.5f),
                    -x2,
                    -y2,
                    -z2
                )
            }
            gg = 0.95
        } else {
            for (i in 0..3) {
                world.addParticle(
                    ParticleTypes.CRIT,
                    this.x + x2 * (world.random.nextFloat()-0.5f),
                    this.y + x2 * (world.random.nextFloat()-0.5f),
                    this.z + x2 * (world.random.nextFloat()-0.5f),
                    -x2,
                    -y2,
                    -z2
                )
            }
            gg = g
        }
        velocity = vec3d.multiply(gg)
        if (!hasNoGravity()) {
            velocity = velocity.add(0.0, -h, 0.0)
        }
        this.setPosition(d, e, f)
    }

    override fun onEntityHit(entityHitResult: EntityHitResult) {
        super.onEntityHit(entityHitResult)
        val entity = owner
        if (entity is LivingEntity) {
            if(pierce){
                entityHitResult.entity.damage(
                    DamageSource.magic(this, entity).setProjectile(),
                    4.0f
                )
            } else {
                entityHitResult.entity.damage(
                    DamageSource.thrownProjectile(this, entity).setProjectile(),
                    3.0f
                )
            }
        }
        discard()
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

    override fun canHit(entity: Entity): Boolean {
        return super.canHit(entity) && !entity.noClip
    }

    override fun isBurning(): Boolean {
        return false
    }

    override fun getDrag(): Float {
        return 0.999999f
    }

    override fun getParticleType(): ParticleEffect? {
        return ParticleTypes.CRIT
    }

    override fun onSpawnPacket(packet: EntitySpawnS2CPacket) {
        super.onSpawnPacket(packet)
        val d = packet.velocityX
        val e = packet.velocityY
        val f = packet.velocityZ
        /*for (i in 0..6) {
            val g = 0.95-0.02*i
            world.addParticle(ParticleTypes.CRIT, this.x, this.y, this.z, d * g, e, f * g)
            println("adding particles!")
        }*/
        this.setVelocity(d, e, f)
    }

}