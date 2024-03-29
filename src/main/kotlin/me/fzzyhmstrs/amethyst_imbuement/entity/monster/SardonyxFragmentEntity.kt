package me.fzzyhmstrs.amethyst_imbuement.entity.monster

import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.entity.living.PlayerCreatedConstructEntity
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterSound
import net.minecraft.block.BlockState
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.ai.goal.*
import net.minecraft.entity.attribute.DefaultAttributeContainer
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.data.DataTracker
import net.minecraft.entity.data.TrackedData
import net.minecraft.entity.data.TrackedDataHandlerRegistry
import net.minecraft.entity.mob.HostileEntity
import net.minecraft.entity.mob.MobEntity
import net.minecraft.entity.passive.IronGolemEntity
import net.minecraft.entity.passive.MerchantEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.world.World
import java.util.*

class SardonyxFragmentEntity(entityType: EntityType<out HostileEntity>?, world: World?) : HostileEntity(entityType, world) {

    companion object{

        protected val ENRAGED: TrackedData<Boolean> = DataTracker.registerData(SardonyxFragmentEntity::class.java, TrackedDataHandlerRegistry.BOOLEAN)
        protected val DAMAGE_UUID = UUID.fromString("71c5ccf4-2d8a-11ee-be56-0242ac120002")
        protected val SPEED_UUID = UUID.fromString("78ee5708-2d8a-11ee-be56-0242ac120002")
        protected val ARMOR_UUID = UUID.fromString("f6a24268-70dd-11ee-b962-0242ac120002")


        fun createFragmentAttributes(): DefaultAttributeContainer.Builder {
            return createHostileAttributes()
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 16.0)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, AiConfig.entities.sardonyxFragment.baseHealth.get())
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.75)
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 0.5)
                .add(EntityAttributes.GENERIC_ARMOR, AiConfig.entities.sardonyxFragment.baseArmor.get())
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, AiConfig.entities.sardonyxFragment.baseDamage.get())
        }
    }

    private val damageModifier = EntityAttributeModifier(
        DAMAGE_UUID,
        "Sardonyx Damage Bonus",
        AiConfig.entities.sardonyxFragment.enragedDamage.get(),
        EntityAttributeModifier.Operation.ADDITION)
    private val speedModifier = EntityAttributeModifier(
        SPEED_UUID,
        "Sardonyx Speed Bonus",
        0.25,
        EntityAttributeModifier.Operation.MULTIPLY_TOTAL)
    private val armorModifier = EntityAttributeModifier(
        ARMOR_UUID,
        "Sardonyx Armor Bonus",
        6.0,
        EntityAttributeModifier.Operation.ADDITION)

    protected var attackTicksLeft = 0

    override fun initGoals() {
        goalSelector.add(8, LookAtEntityGoal(this, PlayerEntity::class.java, 8.0f))
        goalSelector.add(8, LookAroundGoal(this))
        initCustomGoals()
    }

    protected fun initCustomGoals() {
        goalSelector.add(2, MeleeAttackGoal(this, 1.0, false))
        goalSelector.add(7, WanderAroundFarGoal(this, 1.0))
        targetSelector.add(1, RevengeGoal(this, SardonyxElementalEntity::class.java))
        targetSelector.add(2, ActiveTargetGoal(this as MobEntity, PlayerEntity::class.java, true))
        targetSelector.add(3, ActiveTargetGoal(this as MobEntity, MerchantEntity::class.java, false))
        targetSelector.add(4, ActiveTargetGoal(this as MobEntity, PlayerCreatedConstructEntity::class.java, true))
        targetSelector.add(5, ActiveTargetGoal(this as MobEntity, IronGolemEntity::class.java, true))
    }

    override fun initDataTracker() {
        super.initDataTracker()
        dataTracker.startTracking(ENRAGED,false)
    }

    override fun getNextAirUnderwater(air: Int): Int {
        return air
    }
    override fun readCustomDataFromNbt(nbt: NbtCompound) {
        super.readCustomDataFromNbt(nbt)
        setEnraged(nbt.getBoolean("enraged"), true)
    }

    override fun writeCustomDataToNbt(nbt: NbtCompound) {
        super.writeCustomDataToNbt(nbt)
        nbt.putBoolean("enraged", getEnraged())
    }

    override fun getAmbientSound(): SoundEvent {
        return RegisterSound.FRAGMENT_CRUMBLES
    }

    override fun getHurtSound(source: DamageSource?): SoundEvent {
        return RegisterSound.ELEMENTAL_HURT
    }

    override fun getDeathSound(): SoundEvent {
        return RegisterSound.ELEMENTAL_DEATH
    }

    override fun playStepSound(pos: BlockPos?, state: BlockState?) {
        playSound(SoundEvents.ENTITY_IRON_GOLEM_STEP, 0.5f, 1.0f)
    }

    override fun tickMovement() {
        super.tickMovement()
        if (attackTicksLeft > 0) {
            --attackTicksLeft
        }
    }

    fun getAttackTicks(): Int {
        return attackTicksLeft
    }

    override fun handleStatus(status: Byte) {
        if (status.toInt() == 4) {
            attackTicksLeft = 10
        } else {
            super.handleStatus(status)
        }
    }

    fun getEnraged(): Boolean{
        return dataTracker.get(ENRAGED)
    }

    override fun tryAttack(target: Entity): Boolean {
        attackTicksLeft = 10
        world.sendEntityStatus(this, 4.toByte())
        val bl = super.tryAttack(target)
        if (bl) {
            val f = world.getLocalDifficulty(blockPos).localDifficulty
            if (this.mainHandStack.isEmpty && this.isOnFire && random.nextFloat() < f * 0.3f) {
                target.setOnFireFor(2 * f.toInt())
            }
        }
        return bl
    }

    fun setEnraged(bl: Boolean, loading: Boolean){
        if (!loading) {
            if (bl && !getEnraged()) {
                this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE)?.addPersistentModifier(damageModifier)
                this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)?.addPersistentModifier(speedModifier)
                this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR)?.addPersistentModifier(armorModifier)
                val entityWorld = world
                if (entityWorld is ServerWorld){
                    entityWorld.spawnParticles(ParticleTypes.ANGRY_VILLAGER,this.x,this.eyeY,this.z,15,.25,.25,.25,0.2)
                }
                world.playSound(null,blockPos,SoundEvents.ENTITY_DRAGON_FIREBALL_EXPLODE, SoundCategory.HOSTILE,0.4f,1.0f)
            } else if (!bl && getEnraged()) {
                this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE)?.removeModifier(damageModifier)
                this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)?.removeModifier(speedModifier)
                this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR)?.removeModifier(armorModifier)
            }
        }
        dataTracker.set(ENRAGED, bl)
    }

    override fun remove(reason: RemovalReason) {
        if (reason == RemovalReason.KILLED) {
            val box = Box(pos.x + 16.0, pos.y + 16.0, pos.z + 16.0, pos.x - 16.0, pos.y - 16.0, pos.z - 16.0)
            for (sardonyxFragmentEntity in world.getOtherEntities(this, box) { it is SardonyxFragmentEntity }.stream().map { it as SardonyxFragmentEntity }) {
                sardonyxFragmentEntity.setEnraged(true, false)
            }
        }
        super.remove(reason)
    }


}