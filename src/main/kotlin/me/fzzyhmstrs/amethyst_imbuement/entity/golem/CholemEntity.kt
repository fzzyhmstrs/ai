package me.fzzyhmstrs.amethyst_imbuement.entity.golem

import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.entity.goal.ConstructLookGoal
import me.fzzyhmstrs.amethyst_imbuement.entity.living.PlayerCreatedConstructEntity
import me.fzzyhmstrs.fzzy_config.config_util.ConfigSection
import me.fzzyhmstrs.fzzy_config.config_util.ReadMeText
import me.fzzyhmstrs.fzzy_config.validated_field.ValidatedDouble
import me.fzzyhmstrs.fzzy_config.validated_field.ValidatedEntityAttributes
import me.fzzyhmstrs.fzzy_config.validated_field.ValidatedInt
import net.minecraft.block.BlockState
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.data.DataTracker
import net.minecraft.entity.data.TrackedData
import net.minecraft.entity.data.TrackedDataHandlerRegistry
import net.minecraft.entity.mob.Monster
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

open class CholemEntity: PlayerCreatedConstructEntity {

    constructor(entityType: EntityType<CholemEntity>, world: World): super(entityType, world)

    constructor(entityType: EntityType<CholemEntity>, world: World, ageLimit: Int = AiConfig.entities.cholem.baseLifespan.get(), createdBy: LivingEntity?) : super(entityType, world, ageLimit, createdBy)

    class Cholem: ConfigSection(Header.Builder().space().add("readme.entities.cholem_1").build()){
        var baseLifespan = ValidatedInt(3600, Int.MAX_VALUE-120000,20)
        var baseAttributes = ValidatedEntityAttributes(mapOf(
            EntityAttributes.GENERIC_MAX_HEALTH to 80.0,
            EntityAttributes.GENERIC_MOVEMENT_SPEED to 0.3,
            EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE to 0.8,
            EntityAttributes.GENERIC_ATTACK_DAMAGE to 10.0,
            EntityAttributes.GENERIC_ARMOR to 4.0))
        @ReadMeText("readme.entities.cholem.enragedDamage")
        var enragedDamage = ValidatedDouble(4.0,125.0,0.0)
        @ReadMeText("readme.entities.cholem.enragedSpeed")
        var enragedSpeed = ValidatedDouble(0.15,1.0,0.0)
    }

    companion object {

        protected val DAMAGE_UUID = UUID.fromString("71c5ccf4-2d8a-11ee-be56-0242ac120002")
        protected val SPEED_UUID = UUID.fromString("78ee5708-2d8a-11ee-be56-0242ac120002")

        protected val ENRAGED: TrackedData<Boolean> = DataTracker.registerData(CholemEntity::class.java, TrackedDataHandlerRegistry.BOOLEAN)
    }

    val damageModifier = EntityAttributeModifier(
        DAMAGE_UUID,
        "Cholem Damage Bonus",
        AiConfig.entities.cholem.enragedDamage.get(),
        EntityAttributeModifier.Operation.ADDITION)
    val speedModifier = EntityAttributeModifier(
        SPEED_UUID,
        "Cholem Speed Bonus",
        AiConfig.entities.cholem.enragedSpeed.get(),
        EntityAttributeModifier.Operation.MULTIPLY_TOTAL)

    override fun initGoals() {
        super.initGoals()
        goalSelector.add(6, ConstructLookGoal(this))
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
        setEnraged(nbt.getBoolean("enraged"))
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

    override fun getBaseDamage(): Float {
        val f = super.getBaseDamage()
        val g = if (f.toInt() > 0) f / 2.0f + random.nextInt(f.toInt()).toFloat() else f
        return g
    }

    override fun applyKnockback(g: Float, target: Entity){
        target.velocity = target.velocity.add(0.0, 0.5, 0.0)
    }

    override fun tryAttack(target: Entity): Boolean {
        attackTicksLeft = 10
        world.sendEntityStatus(this, 4.toByte())
        playSound(SoundEvents.ENTITY_IRON_GOLEM_ATTACK, 1.0f, 1.0f)
        return super.tryAttack(target)
    }

    override fun handleStatus(status: Byte) {
        if (status.toInt() == 4) {
            playSound(SoundEvents.ENTITY_IRON_GOLEM_ATTACK, 1.0f, 1.0f)
        }
        super.handleStatus(status)
    }

    override fun getHurtSound(source: DamageSource): SoundEvent {
        return SoundEvents.ENTITY_IRON_GOLEM_HURT
    }

    override fun getDeathSound(): SoundEvent {
        return SoundEvents.ENTITY_IRON_GOLEM_DEATH
    }

    override fun playStepSound(pos: BlockPos, state: BlockState) {
        playSound(SoundEvents.ENTITY_IRON_GOLEM_STEP, 1.0f, 1.0f)
    }

    fun texture(): Identifier{
        return if(getEnraged()){
            AI.identity("textures/entity/crystal_golem/cholem.png")
        } else {
            AI.identity("textures/entity/crystal_golem/cholem_enraged.png")
        }
    }

    fun getEnraged(): Boolean{
        return dataTracker.get(ENRAGED)
    }

    fun setEnraged(bl: Boolean){
        if (bl && !getEnraged()) {
            this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE)?.addPersistentModifier(damageModifier)
            this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)?.addPersistentModifier(speedModifier)
            val entityWorld = world
            if (entityWorld is ServerWorld){
                entityWorld.spawnParticles(ParticleTypes.ANGRY_VILLAGER,this.x,this.eyeY,this.z,15,.25,.25,.25,0.2)
            }
            world.playSound(null,blockPos,SoundEvents.ENTITY_DRAGON_FIREBALL_EXPLODE,SoundCategory.PLAYERS,0.4f,1.0f)
        } else if (!bl && getEnraged()) {
            this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE)?.removeModifier(damageModifier)
            this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)?.removeModifier(speedModifier)
        }
        dataTracker.set(ENRAGED, bl)
    }

    override fun remove(reason: RemovalReason) {
        if (reason.shouldDestroy()) {
            val box = Box(pos.x + 16.0, pos.y + 16.0, pos.z + 16.0, pos.x - 16.0, pos.y - 16.0, pos.z - 16.0)
            val entities = world.getOtherEntities(this, box) { it is CholemEntity }.stream().map { it as CholemEntity }
            for (cholem in entities) {
                cholem.setEnraged(true)
            }
        }
        super.remove(reason)
    }

    fun getLookingAtVillagerTicks(): Int {
        return lookingAtVillagerTicksLeft
    }

    override fun getLeashOffset(): Vec3d {
        return Vec3d(0.0, (0.875f * standingEyeHeight).toDouble(), (this.width * 0.4f).toDouble())
    }
}
