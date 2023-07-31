package me.fzzyhmstrs.amethyst_imbuement.entity.horse

import me.fzzyhmstrs.amethyst_core.entity.ModifiableEffectEntity
import me.fzzyhmstrs.amethyst_core.entity.Scalable
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.entity.living.PlayerCreatedHorseEntity
import me.fzzyhmstrs.fzzy_config.config_util.ConfigSection
import me.fzzyhmstrs.fzzy_config.validated_field.ValidatedDouble
import me.fzzyhmstrs.fzzy_core.entity_util.PlayerCreatable
import net.minecraft.block.Blocks
import net.minecraft.entity.*
import net.minecraft.entity.attribute.DefaultAttributeContainer
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.data.DataTracker
import net.minecraft.entity.data.TrackedData
import net.minecraft.entity.data.TrackedDataHandlerRegistry
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.passive.AbstractHorseEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtList
import net.minecraft.sound.SoundEvents
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import net.minecraft.world.LocalDifficulty
import net.minecraft.world.ServerWorldAccess
import net.minecraft.world.World


class SeahorseEntity(entityType: EntityType<out AbstractHorseEntity>?, world: World?) : PlayerCreatedHorseEntity(entityType, world),
    ModifiableEffectEntity, PlayerCreatable, Scalable {

    class Seahorse: ConfigSection(Header.Builder().space().add("readme.entities.seahorse").build()){
        var baseJumpStrength = ValidatedDouble(0.93,2.0,0.0)
        var baseHealth = ValidatedDouble(30.0,150.0,10.0)
        var baseMoveSpeed = ValidatedDouble(0.275,1.0,0.01)
    }

    companion object{

        private val CHEST: TrackedData<Boolean> = DataTracker.registerData(SeahorseEntity::class.java, TrackedDataHandlerRegistry.BOOLEAN)
        fun createSeahorseBaseAttributes(): DefaultAttributeContainer.Builder{
            return createMobAttributes()
                .add(EntityAttributes.HORSE_JUMP_STRENGTH, AiConfig.entities.seahorse.baseJumpStrength.get())
                .add(EntityAttributes.GENERIC_MAX_HEALTH, AiConfig.entities.seahorse.baseHealth.get())
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, AiConfig.entities.seahorse.baseMoveSpeed.get())
        }
    }

    override fun canBreatheInWater(): Boolean {
        return true
    }

    override fun hurtByWater(): Boolean {
        return false
    }

    override fun getNextAirUnderwater(air: Int): Int {
        return air
    }

    override fun initDataTracker() {
        super.initDataTracker()
        dataTracker.startTracking(CHEST, false)
    }

    fun hasChest(): Boolean {
        return dataTracker.get(CHEST)
    }

    fun setHasChest(hasChest: Boolean) {
        dataTracker.set(CHEST, hasChest)
    }

    override fun getInventorySize(): Int {
        return if (hasChest()) {
            7
        } else super.getInventorySize()
    }

    override fun dropInventory() {
        super.dropInventory()
        if (hasChest()) {
            if (!world.isClient) {
                this.dropItem(Blocks.CHEST)
            }
            setHasChest(false)
        }
    }

    override fun writeCustomDataToNbt(nbt: NbtCompound) {
        super.writeCustomDataToNbt(nbt)
        nbt.putBoolean("ChestedHorse", hasChest())
        if (this.hasChest()) {
            val nbtList = NbtList()
            for (i in 2 until items.size()) {
                val itemStack = items.getStack(i)
                if (itemStack.isEmpty) continue
                val nbtCompound = NbtCompound()
                nbtCompound.putByte("Slot", i.toByte())
                itemStack.writeNbt(nbtCompound)
                nbtList.add(nbtCompound)
            }
            nbt.put("Items", nbtList)
        }
    }

    override fun readCustomDataFromNbt(nbt: NbtCompound) {
        super.readCustomDataFromNbt(nbt)
        setHasChest(nbt.getBoolean("ChestedHorse"))
        onChestedStatusChanged()
        if (this.hasChest()) {
            val nbtList = nbt.getList("Items", NbtElement.COMPOUND_TYPE.toInt())
            for (i in nbtList.indices) {
                val nbtCompound = nbtList.getCompound(i)
                val j = nbtCompound.getByte("Slot").toInt() and 0xFF
                if (j < 2 || j >= items.size()) continue
                items.setStack(j, ItemStack.fromNbt(nbtCompound))
            }
        }
    }

    override fun initialize(
        world: ServerWorldAccess?,
        difficulty: LocalDifficulty?,
        spawnReason: SpawnReason?,
        entityData: EntityData?,
        entityNbt: NbtCompound?
    ): EntityData? {
        val result = super.initialize(world, difficulty, spawnReason, entityData, entityNbt)
        onChestedStatusChanged()
        return result
    }

    override fun travel(movementInput: Vec3d?) {
        val isBeingRidden = hasPassengers() && this.isTame && this.isSaddled
        if (isBeingRidden) {
            val passengerEntity = this.firstPassenger as LivingEntity?
            yaw = passengerEntity!!.yaw
            prevYaw = yaw
            pitch = passengerEntity.pitch * 0.5f
            setRotation(yaw, pitch)
            bodyYaw = yaw
            headYaw = bodyYaw
            val passengerSidewaysSpeed = passengerEntity.sidewaysSpeed * 0.5f
            var passengerForwardSpeed = passengerEntity.forwardSpeed
            if (passengerForwardSpeed <= 0.0f) {
                passengerForwardSpeed *= 0.25f
                soundTicks = 0
            }
            val maxFlightSpeed = movementSpeed.toDouble() * getChildMovementSpeedBonus { random.nextDouble() } * 200.0
            var upwardsVelocity: Double
            val velocity: Vec3d
            if (jumpStrength > 0.0f && this.isOnGround) {
                velocity = this.velocity
                upwardsVelocity = getJumpStrength() * jumpStrength.toDouble() * this.jumpVelocityMultiplier.toDouble()
                if (hasStatusEffect(StatusEffects.JUMP_BOOST)) {
                    upwardsVelocity += ((getStatusEffect(StatusEffects.JUMP_BOOST)!!.amplifier + 1).toFloat() * 0.1f).toDouble()
                }
                this.setVelocity(velocity.x, upwardsVelocity, velocity.z)
                velocityDirty = true
                if (passengerForwardSpeed > 0.0f) {
                    val i = MathHelper.sin(yaw * 0.017453292f)
                    val j = MathHelper.cos(yaw * 0.017453292f)
                    this.velocity = this.velocity.add(
                        (-0.4f * i * jumpStrength).toDouble(),
                        0.0,
                        (0.4f * j * jumpStrength).toDouble()
                    )
                }
                jumpStrength = 0.0f
            } else if (jumpStrength > 0.0f && this.isTouchingWater) {
                velocity = this.velocity
                upwardsVelocity = getJumpStrength() * jumpStrength.toDouble() * this.jumpVelocityMultiplier.toDouble()
                this.velocity = velocity.normalize().multiply(2.0 * upwardsVelocity)
                velocityDirty = true
                jumpStrength = 0.0f
            }
            if (this.isLogicalSideForUpdatingMovement) {
                if (this.isInLava) {
                    updateVelocity(0.02f, movementInput)
                    move(MovementType.SELF, this.velocity)
                    this.velocity = this.velocity.multiply(0.5)
                }
                movementSpeed = this.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED).toFloat()
                if (this.isTouchingWater && (MathHelper.abs(passengerForwardSpeed) > 0.0f || MathHelper.abs(passengerSidewaysSpeed) > 0.0f)) {
                    updateVelocity(0.05f, movementInput)
                    move(MovementType.SELF, this.velocity)
                }
                this.velocity = this.velocity
                var upDown = 0.0
                if (this.isTouchingWater) {
                    if (passengerEntity.pitch < -10.0f) {
                        upDown =
                            (passengerEntity.pitch / 90.0f).toDouble() * -maxFlightSpeed * passengerForwardSpeed.toDouble()
                    } else if (passengerEntity.pitch > 20.0f) {
                        upDown = 0.05 * -maxFlightSpeed * passengerForwardSpeed.toDouble()
                    }
                }
                val flightVector = Vec3d(passengerSidewaysSpeed.toDouble(), upDown, passengerForwardSpeed.toDouble())
                super.travel(flightVector)
            } else if (passengerEntity is PlayerEntity) {
                this.velocity = Vec3d.ZERO
            }
        }
        if (!world.isClient) {
            if (!isBeingRidden && !this.isTouchingWater) {
                super.travel(movementInput)
            } else if (!isBeingRidden && this.isTouchingWater) {
                this.turtleTravel(movementInput)
            }
        }
        this.updateLimbs(false)
    }

    private fun turtleTravel(movementInput: Vec3d?) {
        if (canMoveVoluntarily() && this.isTouchingWater) {
            updateVelocity(0.1f, movementInput)
            move(MovementType.SELF, velocity)
            velocity = velocity.multiply(0.9)
            if (target == null) {
                velocity = velocity.add(0.0, -0.005, 0.0)
            }
        } else {
            super.travel(movementInput)
        }
    }

    override fun interactMob(player: PlayerEntity, hand: Hand?): ActionResult? {
        val itemStack = player.getStackInHand(hand)
        if (!itemStack.isEmpty) {
            if (!hasChest() && itemStack.isOf(Items.CHEST)) {
                addChest(player, itemStack)
                return ActionResult.success(world.isClient)
            }
        }
        return super.interactMob(player, hand)
    }

    private fun addChest(player: PlayerEntity, chest: ItemStack) {
        setHasChest(true)
        playAddChestSound()
        if (!player.abilities.creativeMode) {
            chest.decrement(1)
        }
        onChestedStatusChanged()
    }

    protected fun playAddChestSound() {
        playSound(SoundEvents.ENTITY_DONKEY_CHEST, 1.0f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f)
    }

}