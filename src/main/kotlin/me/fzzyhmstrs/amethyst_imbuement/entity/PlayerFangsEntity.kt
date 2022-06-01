package me.fzzyhmstrs.amethyst_imbuement.entity

import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEntity
import me.fzzyhmstrs.amethyst_imbuement.scepter.base_augments.AugmentEffect
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.damage.DamageSource
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.Packet
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundEvents
import net.minecraft.world.World
import java.util.*
import java.util.function.Consumer

class PlayerFangsEntity(entityType: EntityType<PlayerFangsEntity>, world: World): Entity(entityType,world), ModifiableDamageEntity {

    private var warmup = 0
    private var startedAttack = false
    private var ticksLeft = 22
    private var playingAnimation = false
    private var owner: LivingEntity? = null
    private var ownerUuid: UUID? = null
    override var entityEffects: AugmentEffect = AugmentEffect().withDamage(6.0F)

    constructor(world: World,x: Double, y: Double, z: Double, yaw: Float, warmup: Int, owner: LivingEntity): this(RegisterEntity.PLAYER_FANGS,world){
        this.warmup = warmup
        setOwner(owner)
        this.yaw = yaw * 57.295776f
        this.setPosition(x, y, z)
    }

    private fun setOwner(owner: LivingEntity?) {
        this.owner = owner
        ownerUuid = owner?.uuid
    }
    private fun getOwner(): LivingEntity? {
        return if (owner == null){
            if (ownerUuid != null){
                val entity: Entity?
                val world = this.world
                if (world is ServerWorld){
                    entity = world.getEntity(ownerUuid)
                    entity as LivingEntity
                } else {
                    null
                }
            } else {
                null
            }
        } else {
            owner
        }
    }

    override fun tick() {
        super.tick()
        if (world.isClient) {
            if (playingAnimation) {
                --ticksLeft
                if (ticksLeft == 14) {
                    for (i in 0..11) {
                        val d = this.x + (random.nextDouble() * 2.0 - 1.0) * this.width.toDouble() * 0.5
                        val e = this.y + 0.05 + random.nextDouble()
                        val f = this.z + (random.nextDouble() * 2.0 - 1.0) * this.width.toDouble() * 0.5
                        val g = (random.nextDouble() * 2.0 - 1.0) * 0.3
                        val h = 0.3 + random.nextDouble() * 0.3
                        val j = (random.nextDouble() * 2.0 - 1.0) * 0.3
                        world.addParticle(ParticleTypes.CRIT, d, e + 1.0, f, g, h, j)
                    }
                }
            }
        } else if (--warmup < 0) {
            if (warmup == -8) {
                val list = world.getNonSpectatingEntities(
                    LivingEntity::class.java, boundingBox.expand(0.2, 0.0, 0.2)
                )
                for (livingEntity in list) {
                    this.damage(livingEntity)
                }
            }
            if (!startedAttack) {
                world.sendEntityStatus(this, 4.toByte())
                startedAttack = true
            }
            if (--ticksLeft < 0) {
                discard()
            }
        }
    }

    private fun damage(target: LivingEntity) {
        val livingEntity = getOwner()
        if (!target.isAlive || target.isInvulnerable || target === livingEntity) {
            return
        }
        if (livingEntity == null) {
            target.damage(DamageSource.MAGIC, entityEffects.damage(0))
        } else {
            if (livingEntity.isTeammate(target)) {
                return
            }
            target.damage(DamageSource.magic(this, livingEntity), entityEffects.damage(0))
        }
    }

    override fun handleStatus(status: Byte) {
        super.handleStatus(status)
        if (status.toInt() == 4) {
            playingAnimation = true
            if (!this.isSilent) {
                world.playSound(
                    this.x,
                    this.y,
                    this.z,
                    SoundEvents.ENTITY_EVOKER_FANGS_ATTACK,
                    this.soundCategory,
                    1.0f,
                    random.nextFloat() * 0.2f + 0.85f,
                    false
                )
            }
        }
    }

    fun getAnimationProgress(tickDelta: Float): Float {
        if (!playingAnimation) {
            return 0.0f
        }
        val i = ticksLeft - 2
        return if (i <= 0) {
            1.0f
        } else 1.0f - (i.toFloat() - tickDelta) / 20.0f
    }

    override fun initDataTracker() {
    }

    override fun readCustomDataFromNbt(nbt: NbtCompound) {
        warmup = nbt.getInt("Warmup")
        if (nbt.containsUuid("Owner")) {
            ownerUuid = nbt.getUuid("Owner")
        }
    }

    override fun writeCustomDataToNbt(nbt: NbtCompound) {
        nbt.putInt("Warmup", warmup)
        if (ownerUuid != null) {
            nbt.putUuid("Owner", ownerUuid)
        }
    }

    override fun createSpawnPacket(): Packet<*> {
        return EntitySpawnS2CPacket(this)
    }


}