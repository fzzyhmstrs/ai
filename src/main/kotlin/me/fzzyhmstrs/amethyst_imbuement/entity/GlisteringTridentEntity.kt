package me.fzzyhmstrs.amethyst_imbuement.entity

import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment.CONTAMINATED
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment.DECAYED
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEntity.GLISTERING_TRIDENT_ENTITY
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem.GLISTERING_TRIDENT
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.data.DataTracker
import net.minecraft.entity.data.TrackedDataHandlerRegistry
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.projectile.PersistentProjectileEntity
import net.minecraft.entity.projectile.TridentEntity
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World

class GlisteringTridentEntity : PersistentProjectileEntity {
    private var tridentStack = ItemStack(GLISTERING_TRIDENT)
    private var dealtDamage = false
    var returnTimer = 0

    constructor(entityType: EntityType<out GlisteringTridentEntity?>?, world: World?) : super(entityType, world)
    constructor(world: World?, owner: LivingEntity?, stack: ItemStack) : super(
        GLISTERING_TRIDENT_ENTITY,
        owner,
        world
    ) {
        tridentStack = stack.copy()
        dataTracker.set(LOYALTY, EnchantmentHelper.getLoyalty(stack).toByte())
        dataTracker.set(ENCHANTED, stack.hasGlint())
    }

    override fun initDataTracker() {
        super.initDataTracker()
        dataTracker.startTracking(LOYALTY, 0.toByte())
        dataTracker.startTracking(ENCHANTED, false)
    }

    override fun tick() {
        if (inGroundTime > 4) {
            dealtDamage = true
        }
        val entity = owner
        val i = dataTracker.get(LOYALTY)
        if (i > 0 && (dealtDamage || this.isNoClip) && entity != null) {
            if (!isOwnerAlive) {
                if (!world.isClient && pickupType == PickupPermission.ALLOWED) {
                    this.dropStack(asItemStack(), 0.1f)
                }
                discard()
            } else {
                this.isNoClip = true
                val vec3d = entity.eyePos.subtract(pos)
                setPos(this.x, this.y + vec3d.y * 0.015 * i.toDouble(), this.z)
                if (world.isClient) {
                    lastRenderY = this.y
                }
                val d = 0.05 * i.toDouble()
                velocity = velocity.multiply(0.95).add(vec3d.normalize().multiply(d))
                if (returnTimer == 0) {
                    playSound(SoundEvents.ITEM_TRIDENT_RETURN, 10.0f, 1.0f)
                }
                ++returnTimer
            }
        }
        super.tick()
    }

    private val isOwnerAlive: Boolean
        get() {
            val entity = owner
            return if (entity == null || !entity.isAlive) {
                false
            } else entity !is ServerPlayerEntity || !entity.isSpectator()
        }

    override fun asItemStack(): ItemStack {
        return tridentStack.copy()
    }

    val isEnchanted: Boolean
        get() = dataTracker.get(ENCHANTED)

    override fun getEntityCollision(currentPosition: Vec3d, nextPosition: Vec3d): EntityHitResult? {
        return if (dealtDamage) {
            null
        } else super.getEntityCollision(currentPosition, nextPosition)
    }

    override fun onEntityHit(entityHitResult: EntityHitResult) {
        val blockPos: BlockPos?
        var livingEntity: Entity
        val entity = entityHitResult.entity
        var f = 11.0f //base thrown trident damage, setting to 11 vs. vanilla 8
        if (entity is LivingEntity) {
            livingEntity = entity
            f += EnchantmentHelper.getAttackDamage(tridentStack, livingEntity.group)
        }
        val damageSource = DamageSource.trident(this, if (owner.also {
                livingEntity =
                    it!!
            } == null) this else livingEntity)
        dealtDamage = true
        var soundEvent = SoundEvents.ITEM_TRIDENT_HIT
        if (entity.damage(damageSource, f)) {
            if (entity.type === EntityType.ENDERMAN) {
                return
            }
            if (entity is LivingEntity) {
                if (livingEntity is LivingEntity) {
                    EnchantmentHelper.onUserDamaged(entity, livingEntity)
                    EnchantmentHelper.onTargetDamaged(livingEntity as LivingEntity, entity)
                    if (EnchantmentHelper.getLevel(DECAYED, asItemStack()) > 0) {
                        entity.addStatusEffect(StatusEffectInstance(StatusEffects.WITHER, 100, 1))
                    } else if (EnchantmentHelper.getLevel(CONTAMINATED, asItemStack()) > 0) {
                        entity.addStatusEffect(StatusEffectInstance(StatusEffects.POISON, 100, 1))
                    }
                }
                onHit(entity)
            }
        }
        velocity = velocity.multiply(-0.01, -0.1, -0.01)
        var livingEntity2 = 1.0f
        if (world is ServerWorld && world.isThundering && hasChanneling() && world.isSkyVisible(entity.blockPos)) {
            blockPos = entity.blockPos
            val lightningEntity = EntityType.LIGHTNING_BOLT.create(world)
            lightningEntity!!.refreshPositionAfterTeleport(Vec3d.ofBottomCenter(blockPos))
            lightningEntity.channeler =
                if (livingEntity is ServerPlayerEntity) livingEntity as ServerPlayerEntity else null
            world.spawnEntity(lightningEntity)
            soundEvent = SoundEvents.ITEM_TRIDENT_THUNDER
            livingEntity2 = 5.0f
        }
        playSound(soundEvent, livingEntity2, 1.0f)
    }

    fun hasChanneling(): Boolean {
        return EnchantmentHelper.hasChanneling(tridentStack)
    }

    override fun tryPickup(player: PlayerEntity): Boolean {
        return super.tryPickup(player) || this.isNoClip && isOwner(player) && player.inventory.insertStack(
            asItemStack()
        )
    }

    override fun getHitSound(): SoundEvent {
        return SoundEvents.ITEM_TRIDENT_HIT_GROUND
    }

    override fun onPlayerCollision(player: PlayerEntity) {
        if (isOwner(player) || owner == null) {
            super.onPlayerCollision(player)
        }
    }

    override fun readCustomDataFromNbt(nbt: NbtCompound) {
        super.readCustomDataFromNbt(nbt)
        if (nbt.contains("Trident", 10)) {
            tridentStack = ItemStack.fromNbt(nbt.getCompound("Trident"))
        }
        dealtDamage = nbt.getBoolean("DealtDamage")
        dataTracker.set(LOYALTY, EnchantmentHelper.getLoyalty(tridentStack).toByte())
    }

    override fun writeCustomDataToNbt(nbt: NbtCompound) {
        super.writeCustomDataToNbt(nbt)
        nbt.put("Trident", tridentStack.writeNbt(NbtCompound()))
        nbt.putBoolean("DealtDamage", dealtDamage)
    }

    public override fun age() {
        val i = dataTracker.get(LOYALTY)
        if (pickupType != PickupPermission.ALLOWED || i <= 0) {
            super.age()
        }
    }

    override fun getDragInWater(): Float {
        return 0.99f
    }

    override fun shouldRender(cameraX: Double, cameraY: Double, cameraZ: Double): Boolean {
        return true
    }

    companion object {
        private val LOYALTY = DataTracker.registerData(TridentEntity::class.java, TrackedDataHandlerRegistry.BYTE)
        private val ENCHANTED =
            DataTracker.registerData(TridentEntity::class.java, TrackedDataHandlerRegistry.BOOLEAN)
    }
}