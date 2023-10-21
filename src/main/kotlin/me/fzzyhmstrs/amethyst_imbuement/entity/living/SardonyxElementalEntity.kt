package me.fzzyhmstrs.amethyst_imbuement.entity.living

import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.entity.goal.SardonyxElementalAttackGoal
import me.fzzyhmstrs.amethyst_imbuement.entity.goal.SardonyxElementalPriorityTargetGoal
import net.minecraft.client.render.entity.feature.SkinOverlayOwner
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.ai.goal.*
import net.minecraft.entity.attribute.DefaultAttributeContainer
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.data.DataTracker
import net.minecraft.entity.data.TrackedData
import net.minecraft.entity.data.TrackedDataHandlerRegistry
import net.minecraft.entity.mob.HostileEntity
import net.minecraft.entity.mob.MobEntity
import net.minecraft.entity.passive.IronGolemEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.world.World
import java.util.*

class SardonyxElementalEntity(entityType: EntityType<out HostileEntity>?, world: World?) : HostileEntity(entityType, world), SkinOverlayOwner {

    companion object{

        protected val CHARGING: TrackedData<Boolean> = DataTracker.registerData(SardonyxElementalEntity::class.java, TrackedDataHandlerRegistry.BOOLEAN)
        protected val FIRING_PROJECTILES: TrackedData<Boolean> = DataTracker.registerData(SardonyxElementalEntity::class.java, TrackedDataHandlerRegistry.BOOLEAN)

        fun createElementalAttributes(): DefaultAttributeContainer.Builder {
            return createHostileAttributes()
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 32.0)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, AiConfig.entities.sardonyxElemental.baseHealth.get())
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.1)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 1.0)
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 1.5)
                .add(EntityAttributes.GENERIC_ARMOR, AiConfig.entities.sardonyxElemental.baseArmor.get())
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, AiConfig.entities.sardonyxElemental.baseDamage.get())
        }
    }

    override fun initGoals() {
        goalSelector.add(8, LookAtEntityGoal(this, PlayerEntity::class.java, 8.0f))
        goalSelector.add(8, LookAroundGoal(this))
        initCustomGoals()
    }

    protected fun initCustomGoals() {
        goalSelector.add(1, WanderAroundFarGoal(this, 1.0))
        goalSelector.add(2, SardonyxElementalAttackGoal(this, { this }, {bl -> setFiringProjectiles(bl) }))
        goalSelector.add(7, WanderAroundGoal(this, 1.0))
        targetSelector.add(1, SardonyxElementalPriorityTargetGoal(this))
        targetSelector.add(2, RevengeGoal(this, *arrayOfNulls(0)))
        targetSelector.add(3, ActiveTargetGoal(this as MobEntity, PlayerEntity::class.java, true))
        targetSelector.add(5, ActiveTargetGoal(this as MobEntity, IronGolemEntity::class.java, true))
        targetSelector.add(5, ActiveTargetGoal(this as MobEntity, PlayerCreatedConstructEntity::class.java, true))
    }

    override fun initDataTracker() {
        super.initDataTracker()
        dataTracker.startTracking(CHARGING,false)
        dataTracker.startTracking(FIRING_PROJECTILES,false)
    }

    protected var attackTicksLeft = 0
    protected var quartersReached = 0
    internal val lastDamageTracker = LastDamageTracker()

    override fun readCustomDataFromNbt(nbt: NbtCompound) {
        super.readCustomDataFromNbt(nbt)
        setCharging(nbt.getBoolean("charging"))
        setFiringProjectiles(nbt.getBoolean("firingProjectiles"))
    }

    override fun writeCustomDataToNbt(nbt: NbtCompound) {
        super.writeCustomDataToNbt(nbt)
        nbt.putBoolean("charging", getCharging())
        nbt.putBoolean("firingProjectiles", getFiringProjectiles())
    }

    override fun tickMovement() {
        super.tickMovement()
        if (attackTicksLeft > 0) {
            --attackTicksLeft
        }
    }

    override fun handleStatus(status: Byte) {
        if (status.toInt() == 4) {
            attackTicksLeft = 10
        } else {
            super.handleStatus(status)
        }
    }

    fun getAttackTicks(): Int {
        return attackTicksLeft
    }

    fun getCharging(): Boolean{
        return dataTracker.get(CHARGING)
    }

    fun setCharging(bl: Boolean){
        dataTracker.set(CHARGING,bl)
    }

    fun getFiringProjectiles(): Boolean{
        return dataTracker.get(FIRING_PROJECTILES)
    }

    fun setFiringProjectiles(bl: Boolean){
        dataTracker.set(FIRING_PROJECTILES,bl)
    }

    override fun damage(source: DamageSource, amount: Float): Boolean {
        if (getCharging())
            return false
        val bl = super.damage(source, amount)
        if (bl){
            if (atOrBelowQuarter()){
                setCharging(true)
            }
            source.attacker?.damage(this.damageSources.thorns(this),4f)
            lastDamageTracker.addDamage(source, amount)
        }
        return bl
    }

    private fun atOrBelowQuarter(): Boolean{
        return when (quartersReached){
            0-> {
                if (health/maxHealth <= 0.75f){
                    health = maxHealth * 0.75f
                    quartersReached = 1
                    true
                } else {
                    false
                }
            }
            1-> {
                if (health/maxHealth <= 0.5f){
                    health = maxHealth * 0.5f
                    quartersReached = 2
                    true
                } else {
                    false
                }
            }
            2-> {
                if (health/maxHealth <= 0.25f){
                    health = maxHealth * 0.25f
                    quartersReached = 3
                    true
                } else {
                    false
                }
            }
            else -> false
        }
    }


    internal class LastDamageTracker(private val lastAttacks: LinkedList<Pair<DamageSource,Float>> = LinkedList(),
            private val attackers: MutableMap<LivingEntity, Float> = mutableMapOf(),
            private var averageDamage: Float = 0f,
            private var numberOfAttackers: Int = 0,
            private var standoutAttacker: LivingEntity? = null){


        fun addDamage(source: DamageSource, amount: Float){
            lastAttacks.addLast(Pair(source,amount))
            if (lastAttacks.size > 15)
                lastAttacks.pop()
            attackers.clear()
            var totalDmg = 0f
            for (s in lastAttacks){
                val le = s.first.attacker as? LivingEntity ?: continue
                val currentDmg = attackers.computeIfAbsent(le){0f}
                totalDmg += s.second
                attackers[le] = currentDmg + s.second
            }
            averageDamage = totalDmg / lastAttacks.size
            numberOfAttackers = attackers.size
            var currentStandout = 0f
            standoutAttacker = null
            for (attacker in attackers){
                if (attacker.value > (totalDmg * 0.4f)) {
                    if (attacker.value > currentStandout){
                        standoutAttacker = attacker.key
                        currentStandout = attacker.value
                    }
                }
            }
        }

        fun hasPriority(): Boolean{
            return standoutAttacker != null
        }

        fun getPriority(): LivingEntity?{
            return standoutAttacker
        }

        fun getAverageDamage(): Float{
            return averageDamage
        }

        fun getNumberOfAttackers(): Int{
            return numberOfAttackers
        }
    }

    override fun shouldRenderOverlay(): Boolean {
        return getCharging()
    }


}