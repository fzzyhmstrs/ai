package me.fzzyhmstrs.amethyst_imbuement.entity.monster

import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.entity.goal.SardonyxElementalAttackGoal
import me.fzzyhmstrs.amethyst_imbuement.entity.goal.SardonyxElementalPriorityTargetGoal
import me.fzzyhmstrs.amethyst_imbuement.entity.living.PlayerCreatedConstructEntity
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterNetworking
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterSound
import net.minecraft.block.BlockState
import net.minecraft.client.render.entity.feature.SkinOverlayOwner
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.Tameable
import net.minecraft.entity.ai.goal.*
import net.minecraft.entity.attribute.DefaultAttributeContainer
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.data.DataTracker
import net.minecraft.entity.data.TrackedData
import net.minecraft.entity.data.TrackedDataHandlerRegistry
import net.minecraft.entity.mob.HostileEntity
import net.minecraft.entity.mob.MobEntity
import net.minecraft.entity.passive.GolemEntity
import net.minecraft.entity.passive.IronGolemEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtList
import net.minecraft.registry.tag.DamageTypeTags
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundEvent
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
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
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.15)
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
        //goalSelector.add(1, WanderAroundFarGoal(this, 1.0))
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
    internal var lastDamageTracker = LastDamageTracker()
    internal var spellCooldown = 60
    private var lastSpellWasBuff = false

    override fun readCustomDataFromNbt(nbt: NbtCompound) {
        super.readCustomDataFromNbt(nbt)
        setCharging(nbt.getBoolean("charging"))
        setFiringProjectiles(nbt.getBoolean("firingProjectiles"))
        nbt.put("lastDamageTracker", lastDamageTracker.toNbt())
    }

    override fun writeCustomDataToNbt(nbt: NbtCompound) {
        super.writeCustomDataToNbt(nbt)
        nbt.putBoolean("charging", getCharging())
        nbt.putBoolean("firingProjectiles", getFiringProjectiles())
        lastDamageTracker = LastDamageTracker.fromNbt(nbt.getCompound("lastDamageTracker"))
    }

    override fun tickMovement() {
        if (!getCharging())
            super.tickMovement()
        if (spellCooldown > 0) {
            spellCooldown--
        } else if (!lastSpellWasBuff) {
            if (lastDamageTracker.getAverageDamage() > 10f){
                if (lastDamageTracker.isMagic()){
                    Calcify.INSTANCE.magicCalcify(this)
                } else {
                    Calcify.INSTANCE.physicalCalcify(this)
                }
                lastSpellWasBuff = true
                spellCooldown = AiConfig.entities.sardonyxElemental.spellActivationCooldown.get()
            }
        }
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

    override fun getAmbientSound(): SoundEvent {
        return RegisterSound.ELEMENTAL_RUMBLE
    }

    override fun getHurtSound(source: DamageSource?): SoundEvent {
        return RegisterSound.ELEMENTAL_HURT
    }

    override fun getDeathSound(): SoundEvent {
        return RegisterSound.ELEMENTAL_DEATH
    }

    override fun playStepSound(pos: BlockPos?, state: BlockState?) {
        playSound(RegisterSound.STOMP, 1.0f, 1.0f)
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
                if (world is ServerWorld){
                    if (quartersReached <= 2) {
                        val target = this.target
                        if (target != null) {
                            val rot = Vec3d(target.x - this.x,target.getBodyY(0.5) - target.getBodyY(0.5),target.z - target.z).normalize()
                            DevastationBeam.INSTANCE.fire(world as ServerWorld,this,this.pos.add(0.0,this.height/2.0,0.0),rot)
                        }
                    } else {
                        val targetList = lastDamageTracker.getPrioritiesForDevastation(world as ServerWorld)
                        for (target in targetList){
                            val rot = Vec3d(target.x - this.x,target.getBodyY(0.5) - target.getBodyY(0.5),target.z - target.z).normalize()
                            DevastationBeam.INSTANCE.fire(world as ServerWorld,this,this.pos.add(0.0,this.height/2.0,0.0),rot)
                        }
                    }
                }
                setCharging(true)
            }
            source.attacker?.damage(this.damageSources.thorns(this),4f)
            lastDamageTracker.addDamage(source, amount)
        }
        return bl
    }

    override fun tryAttack(target: Entity?): Boolean {
        if (spellCooldown <= 0 && !world.isClient && lastSpellWasBuff){
            val nearbyPossibleAttackers = (world.getOtherEntities(this,this.boundingBox.expand(3.0)) {it is PlayerEntity || it is GolemEntity || it is LivingEntity && it is Tameable}) .map { it as LivingEntity }
            spellCooldown = if (lastDamageTracker.getNumberOfAttackers() < 3 && nearbyPossibleAttackers.size < 4){
                FragmentAid.INSTANCE.summon(world,this.blockPos,this.health/this.maxHealth < 0.5f)
                AiConfig.entities.sardonyxElemental.spellActivationCooldown.get()
            } else {
                for (entity in nearbyPossibleAttackers){
                    RegisterNetworking.sendPlayerKnockback(this.pos.subtract(entity.pos).normalize(),2.5)
                }
                FragmentAid.INSTANCE.summon(world,this.blockPos,this.health/this.maxHealth < 0.5f)
                FragmentAid.INSTANCE.summon(world,this.blockPos,this.health/this.maxHealth < 0.5f)
                AiConfig.entities.sardonyxElemental.spellActivationCooldown.get()
            }
            lastSpellWasBuff = false
        }
        return super.tryAttack(target)
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
            private val attackers: MutableMap<UUID, Float> = mutableMapOf(),
            private var averageDamage: Float = 0f,
            private var numberOfAttackers: Int = 0,
            private var standoutAttacker: UUID? = null,
            private var damageIsMagic: Boolean = false){

        fun toNbt(): NbtCompound{
            val nbt = NbtCompound()
            if (attackers.isNotEmpty()) {
                val attackersList = NbtList()
                for (attacker in attackers) {
                    val attackerNbt = NbtCompound()
                    attackerNbt.putUuid("attackerUuid", attacker.key)
                    attackerNbt.putFloat("attackerDamage", attacker.value)
                    attackersList.add(attackerNbt)
                }
                nbt.put("attackersList", attackersList)
            }
            if (averageDamage != 0f)
                nbt.putFloat("averageDamage",averageDamage)
            if (numberOfAttackers != 0)
                nbt.putInt("numberOfAttackers",numberOfAttackers)
            if (standoutAttacker != null)
                nbt.putUuid("standoutAttacker",standoutAttacker)
            nbt.putBoolean("damageIsMagic",damageIsMagic)

            return nbt
        }

        companion object{
            fun fromNbt(nbt: NbtCompound): LastDamageTracker{
                val attackers: MutableMap<UUID, Float> = mutableMapOf()
                if (nbt.contains("attackersList")) {
                    val attackersList = nbt.getList("attackersList", NbtElement.COMPOUND_TYPE.toInt())
                    for (element in attackersList){
                        val compound = element as? NbtCompound ?: continue
                        val uuid = compound.getUuid("attackerUuid")
                        val damage = compound.getFloat("attackerDamage")
                        attackers[uuid] = damage
                    }
                }
                val averageDamage = nbt.getFloat("averageDamage")
                val numberOfAttackers = nbt.getInt("numberOfAttackers")
                val standoutAttacker = if(nbt.containsUuid("standoutAttacker")) nbt.getUuid("standoutAttacker") else null
                val damageIsMagic = nbt.getBoolean("damageIsMagic")
                return LastDamageTracker(LinkedList(), attackers, averageDamage, numberOfAttackers, standoutAttacker, damageIsMagic)
            }
        }

        fun addDamage(source: DamageSource, amount: Float){
            lastAttacks.addLast(Pair(source,amount))
            if (lastAttacks.size > 15)
                lastAttacks.pop()
            attackers.clear()
            var totalDmg = 0f
            var damageMagic = 0
            for (s in lastAttacks){
                if (s.first.isIn(DamageTypeTags.WITCH_RESISTANT_TO))
                    damageMagic++
                val le = (s.first.attacker as? LivingEntity)?: continue
                val currentDmg = attackers.computeIfAbsent(le.uuid){0f}
                totalDmg += s.second
                attackers[le.uuid] = currentDmg + s.second
            }
            if (damageMagic > lastAttacks.size/2)
                damageIsMagic = true
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

        fun getPriority(world: World): LivingEntity?{
            if (world !is ServerWorld) return null
            return world.getEntity(standoutAttacker) as? LivingEntity
        }

        fun getPrioritiesForDevastation(world: ServerWorld): List<LivingEntity>{
            if (attackers.isEmpty()) return listOf()
            if (attackers.size <= 3){
                return attackers.keys.mapNotNull { world.getEntity(it) as? LivingEntity }
            }
            val targetList: MutableList<LivingEntity> = mutableListOf()
            val sortedMap: SortedMap<Float,UUID> = sortedMapOf(kotlin.Comparator.reverseOrder())
            for (pair in attackers){
                sortedMap[pair.value] = pair.key
            }
            for (pair in sortedMap){
                val entity = world.getEntity(pair.value) as? LivingEntity ?: continue
                targetList.add(entity)
                if (targetList.size >= 3) return targetList
            }
            return targetList
        }

        fun getAverageDamage(): Float{
            return averageDamage
        }

        fun getNumberOfAttackers(): Int{
            return numberOfAttackers
        }

        fun isMagic(): Boolean{
            return damageIsMagic
        }

    }

    override fun shouldRenderOverlay(): Boolean {
        return getCharging()
    }


}