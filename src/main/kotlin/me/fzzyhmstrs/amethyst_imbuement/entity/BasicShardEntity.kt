package me.fzzyhmstrs.amethyst_imbuement.entity

import me.fzzyhmstrs.amethyst_core.augments.paired.PairedAugments
import me.fzzyhmstrs.amethyst_core.augments.paired.ProcessContext
import me.fzzyhmstrs.amethyst_core.entity.ModifiableEffectContainer
import me.fzzyhmstrs.amethyst_core.entity.ModifiableEffectEntity
import me.fzzyhmstrs.amethyst_core.interfaces.SpellCastingEntity
import me.fzzyhmstrs.amethyst_core.modifier.AugmentEffect
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.projectile.PersistentProjectileEntity
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue

open class BasicShardEntity(entityType: EntityType<out BasicShardEntity?>, world: World
): PersistentProjectileEntity(entityType, world), ModifiableEffectEntity {

    constructor(entityType: EntityType<out BasicShardEntity?>,world: World, owner: LivingEntity, speed: Float, divergence: Float, pos: Vec3d, rot: Vec3d): this(entityType,world){
        this.owner = owner
        this.setVelocity(rot.x,rot.y,rot.z,speed,divergence)
        this.setPosition(pos)
    }

    override var entityEffects: AugmentEffect = AugmentEffect().withDamage(6F).withAmplifier(0)
    override var level: Int = 1
    override var spells: PairedAugments = PairedAugments()
    override var modifiableEffects = ModifiableEffectContainer()
    override var processContext = ProcessContext.EMPTY_CONTEXT
    private val struckEntities: MutableList<UUID> = mutableListOf()
    private val particle
        get() = spells.getCastParticleType()

    override fun onCollision(hitResult: HitResult) {
        super.onCollision(hitResult)
        if (!world.isClient) {
            discard()
        }
    }
    override fun onEntityHit(entityHitResult: EntityHitResult) {
        if (world.isClient) {
            return
        }
        val entity = owner
        if (entity is LivingEntity) {
            val entity2 = entityHitResult.entity
            val aug = spells.primary()?:return
            if (!(entity2 is SpellCastingEntity && AiConfig.entities.isEntityPvpTeammate(entity, entity2, aug))){
                if (!struckEntities.contains(entity2.uuid)){
                    struckEntities.add(entity2.uuid)
                    if (struckEntities.size > entityEffects.amplifier(0)){
                        processContext.beforeRemoval()
                    }
                    onShardEntityHit(entityHitResult)
                }
            }
        }
        if (struckEntities.size > entityEffects.amplifier(0)){
            discard()
        }
    }

    open fun onShardEntityHit(entityHitResult: EntityHitResult){
        val entity = owner
        if (entity is LivingEntity && entity is SpellCastingEntity) {
            spells.processSingleEntityHit(entityHitResult,world,this,entity, Hand.MAIN_HAND,level,entityEffects)
            if (!entityHitResult.entity.isAlive){
                spells.processOnKill(entityHitResult,world,this,entity, Hand.MAIN_HAND,level,entityEffects)
            }
        }
    }

    override fun onBlockHit(blockHitResult: BlockHitResult) {
        super.onBlockHit(blockHitResult)
        onShardBlockHit(blockHitResult)
        discard()
    }

    open fun onShardBlockHit(blockHitResult: BlockHitResult){
        val entity = owner
        if (entity is LivingEntity && entity is SpellCastingEntity) {
            spells.processSingleBlockHit(blockHitResult,world,this,entity, Hand.MAIN_HAND,level,entityEffects)
        }
    }

    override fun remove(reason: RemovalReason?) {
        processContext.beforeRemoval()
        runEffect(ModifiableEffectEntity.ON_REMOVED,this,owner,processContext)
        super.remove(reason)
    }

    override fun asItemStack(): ItemStack {
        return ItemStack.EMPTY
    }

    override fun tryPickup(player: PlayerEntity): Boolean {
        return false
    }

    override fun tick() {
        super.tick()
        if (this.age > 1200){
            discard()
        }
        tickTickEffects(this,owner,processContext)
        if (!inGround)
            addParticles(velocity.x, velocity.y, velocity.z)
    }

    override fun readCustomDataFromNbt(nbt: NbtCompound) {
        readModifiableNbt(nbt)
        super.readCustomDataFromNbt(nbt)
    }

    override fun writeCustomDataToNbt(nbt: NbtCompound) {
        writeModifiableNbt(nbt)
        super.writeCustomDataToNbt(nbt)
    }

    open fun addParticles(x2: Double, y2: Double, z2: Double){
        val particleWorld = world
        if (particleWorld !is ServerWorld) return
        if (this.isTouchingWater) {
            particleWorld.spawnParticles(ParticleTypes.BUBBLE,this.x,this.y,this.z,3,1.0,1.0,1.0,0.0)
        } else {
            particleWorld.spawnParticles(particle,this.x,this.y,this.z,3,1.0,1.0,1.0,0.0)
        }
    }

}
