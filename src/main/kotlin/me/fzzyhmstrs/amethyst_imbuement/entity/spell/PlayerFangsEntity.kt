package me.fzzyhmstrs.amethyst_imbuement.entity.spell

import me.fzzyhmstrs.amethyst_core.entity_util.ModifiableEffectEntity
import me.fzzyhmstrs.amethyst_core.interfaces.SpellCastingEntity
import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentConsumer
import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEntity
import net.minecraft.block.BlockState
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.listener.ClientPlayPacketListener
import net.minecraft.network.packet.Packet
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundEvents
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.MathHelper
import net.minecraft.util.shape.VoxelShape
import net.minecraft.world.World
import java.util.*

open class PlayerFangsEntity(entityType: EntityType<PlayerFangsEntity>, world: World): Entity(entityType,world), ModifiableEffectEntity {

    private var warmup = 0
    private var startedAttack = false
    private var ticksLeft = 22
    private var playingAnimation = false
    private var owner: LivingEntity? = null
    private var ownerUuid: UUID? = null
    override var entityEffects: AugmentEffect = AugmentEffect().withDamage(6.0F)
    private var augment: ScepterAugment = RegisterEnchantment.FANGS
    
    fun setAugment(aug: ScepterAugment){
        this.augment = aug
    }

    override fun passEffects(ae: AugmentEffect, level: Int) {
        super.passEffects(ae, level)
        entityEffects.setDamage(ae.damage(level))
    }

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
                    if (entity != null){
                        entity as LivingEntity
                    } else {
                        null
                    }
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
                entityEffects.accept(list,AugmentConsumer.Type.HARMFUL)
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
        if (!AiConfig.entities.shouldItHitBase(livingEntity, target, augment)) return
        if (livingEntity == null) {
            target.damage(SpellDamageSource(this.damageSources.magic(),augment), entityEffects.damage(0))
            entityEffects.accept(target, AugmentConsumer.Type.HARMFUL)
        } else {
            if (livingEntity.isTeammate(target)) {
                return
            }
            target.damage(this.damageSources.indirectMagic(this, livingEntity), entityEffects.damage(0))
            entityEffects.accept(target, AugmentConsumer.Type.HARMFUL)
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

    override fun createSpawnPacket(): Packet<ClientPlayPacketListener>? {
        return EntitySpawnS2CPacket(this)
    }

    companion object{
        fun conjureFangs(world: World,user: LivingEntity,
                         x: Double, z: Double, maxY: Double, y: Double, yaw: Float,
                         warmup: Int, effect: AugmentEffect, level: Int, augment: ScepterAugment): Double{
            var blockPos = BlockPos(x.toInt(), y.toInt(), z.toInt())
            var bl = false
            var d = 0.0
            do {

                val blockPos2: BlockPos = blockPos.down()
                val blockState: BlockState = world.getBlockState(blockPos2)
                val blockState2: BlockState = world.getBlockState(blockPos)
                val voxelShape: VoxelShape = blockState2.getCollisionShape(world,blockPos)
                if (!blockState.isSideSolidFullSquare(world,blockPos2, Direction.UP)) {
                    blockPos = blockPos.down()
                    continue
                }
                if (!world.isAir(blockPos) && !voxelShape.isEmpty) {
                    d = voxelShape.getMax(Direction.Axis.Y)
                }
                bl = true
                break
            } while (blockPos.y >= MathHelper.floor(maxY) - 5)
            if (bl) {
                //consider a custom fangs entity that can have damage effects
                val pfe = PlayerFangsEntity(
                    world,
                    x,
                    blockPos.y.toDouble() + d,
                    z,
                    yaw,
                    warmup,
                    user
                )
                pfe.passEffects(effect, level)
                pfe.setAugment(augment)
                world.spawnEntity(pfe)
                return (blockPos.y.toDouble() + d)
            }
            return Double.NEGATIVE_INFINITY
        }
    }


}
