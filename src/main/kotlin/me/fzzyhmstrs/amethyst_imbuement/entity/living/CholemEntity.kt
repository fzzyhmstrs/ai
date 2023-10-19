package me.fzzyhmstrs.amethyst_imbuement.entity.living

import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentEffect
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.entity.goal.ConstructLookGoal
import me.fzzyhmstrs.amethyst_imbuement.mixins.PlayerHitTimerAccessor
import net.minecraft.block.BlockState
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.attribute.DefaultAttributeContainer
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.data.DataTracker
import net.minecraft.entity.data.TrackedData
import net.minecraft.entity.data.TrackedDataHandlerRegistry
import net.minecraft.entity.mob.Monster
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import java.util.*

@Suppress("PrivatePropertyName")
open class CholemEntity: PlayerCreatedConstructEntity {

    constructor(entityType: EntityType<CholemEntity>, world: World): super(entityType, world)

    constructor(entityType: EntityType<CholemEntity>, world: World, ageLimit: Int, createdBy: LivingEntity?, augmentEffect: AugmentEffect? = null, level: Int = 1) : super(entityType, world, ageLimit, createdBy, augmentEffect, level)

    companion object {

        protected val ENRAGED: TrackedData<Boolean> = DataTracker.registerData(CholemEntity::class.java, TrackedDataHandlerRegistry.BOOLEAN)
        protected val DAMAGE_UUID = UUID.fromString("71c5ccf4-2d8a-11ee-be56-0242ac120002")
        protected val SPEED_UUID = UUID.fromString("78ee5708-2d8a-11ee-be56-0242ac120002")
        fun createCholemAttributes(): DefaultAttributeContainer.Builder {
            return createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, AiConfig.entities.cholem.baseHealth.get())
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.75)
                .add(EntityAttributes.GENERIC_ARMOR, AiConfig.entities.cholem.baseArmor.get())
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, AiConfig.entities.cholem.baseDamage.get().toDouble())
        }
    }

    private val damageModifier = EntityAttributeModifier(
        DAMAGE_UUID,
        "Cholem Damage Bonus",
        AiConfig.entities.cholem.enragedDamage.get().toDouble(),
        EntityAttributeModifier.Operation.ADDITION)
    private val speedModifier = EntityAttributeModifier(
        SPEED_UUID,
        "Cholem Speed Bonus",
        0.15,
        EntityAttributeModifier.Operation.MULTIPLY_TOTAL)

    override fun initGoals() {
        super.initGoals()
        goalSelector.add(6, ConstructLookGoal(this))
    }

    override fun initDataTracker() {
        super.initDataTracker()
        dataTracker.startTracking(ENRAGED,false)
    }

    override fun readCustomDataFromNbt(nbt: NbtCompound) {
        super.readCustomDataFromNbt(nbt)
        setEnraged(nbt.getBoolean("enraged"), true)
    }

    override fun writeCustomDataToNbt(nbt: NbtCompound) {
        super.writeCustomDataToNbt(nbt)
        nbt.putBoolean("enraged", getEnraged())
    }


    override fun pushAway(entity: Entity) {
        if (entity is Monster && getRandom().nextInt(20) == 0) {
            target = entity as LivingEntity
        }
        super.pushAway(entity)
    }

    private fun getAttackDamage(): Float {
        return getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE).toFloat()
    }

    override fun tryAttack(target: Entity): Boolean {
        attackTicksLeft = 10
        world.sendEntityStatus(this, 4.toByte())
        val f = getAttackDamage()
        val g = if (f.toInt() > 0) f / 2.0f + random.nextInt(f.toInt()).toFloat() else f
        val bl = target.damage(this.damageSources.mobAttack(this), g) /*when (val entity = owner) {
            null -> {
                target.damage(this.damageSources.mobAttack(this), g)
            }
            is PlayerEntity -> {
                target.damage(this.damageSources.playerAttack(entity), g)
            }

            else -> {
                target.damage(this.damageSources.mobAttack(entity), g)
            }
        }*/
        if (bl) {
            val summoner = getOwner()
            if (summoner != null && summoner is PlayerEntity && target is LivingEntity){
                (target as PlayerHitTimerAccessor).setPlayerHitTimer(100)
            }
            target.velocity = target.velocity.add(0.0, 0.3, 0.0)
            applyDamageEffects(this, target)
            onAttacking(target)
        }
        playSound(SoundEvents.ENTITY_IRON_GOLEM_ATTACK, 1.0f, 1.0f)
        return bl
    }

    override fun handleStatus(status: Byte) {
        if (status.toInt() == 4) {
            playSound(SoundEvents.ENTITY_IRON_GOLEM_ATTACK, 1.0f, 1.0f)
        }
        super.handleStatus(status)
    }

    override fun getHurtSound(source: DamageSource): SoundEvent {
        return SoundEvents.ENTITY_CHICKEN_HURT
    }

    override fun getDeathSound(): SoundEvent {
        return SoundEvents.ENTITY_CHICKEN_DEATH
    }

    fun getEnraged(): Boolean{
        return dataTracker.get(ENRAGED)
    }

    fun setEnraged(bl: Boolean, loading: Boolean){
        if (!loading) {
            if (bl && !getEnraged()) {
                this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE)?.addPersistentModifier(damageModifier)
                this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)?.addPersistentModifier(speedModifier)
                val entityWorld = world
                if (entityWorld is ServerWorld){
                    entityWorld.spawnParticles(ParticleTypes.ANGRY_VILLAGER,this.x,this.eyeY,this.z,15,.25,.25,.25,0.2)
                }
                world.playSound(null,blockPos,SoundEvents.ENTITY_DRAGON_FIREBALL_EXPLODE, SoundCategory.PLAYERS,0.4f,1.0f)
            } else if (!bl && getEnraged()) {
                this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE)?.removeModifier(damageModifier)
                this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)?.removeModifier(speedModifier)
            }
        }
        dataTracker.set(ENRAGED, bl)
    }

    override fun remove(reason: RemovalReason) {
        if (reason == RemovalReason.KILLED) {
            val box = Box(pos.x + 16.0, pos.y + 16.0, pos.z + 16.0, pos.x - 16.0, pos.y - 16.0, pos.z - 16.0)
            for (cholem in world.getOtherEntities(this, box) { it is CholemEntity }.stream().map { it as CholemEntity }) {
                cholem.setEnraged(true, false)
            }
        }
        super.remove(reason)
    }


    override fun playStepSound(pos: BlockPos, state: BlockState) {
        playSound(SoundEvents.ENTITY_IRON_GOLEM_STEP, 0.5f, 1.0f)
    }

    fun getAttackTicks(): Int {
        return attackTicksLeft
    }

    fun getLookingAtVillagerTicks(): Int {
        return lookingAtVillagerTicksLeft
    }

    override fun getLeashOffset(): Vec3d {
        return Vec3d(0.0, (0.875f * standingEyeHeight).toDouble(), (this.width * 0.4f).toDouble())
    }

    fun texture(): Identifier {
        return if(getEnraged()){
            AI.identity("textures/entity/crystal_golem/cholem_enraged.png")
        } else {
            AI.identity("textures/entity/crystal_golem/cholem.png")
        }
    }
}
