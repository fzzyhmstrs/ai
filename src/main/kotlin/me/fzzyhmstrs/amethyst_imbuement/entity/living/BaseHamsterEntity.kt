package me.fzzyhmstrs.amethyst_imbuement.entity.living

import me.fzzyhmstrs.amethyst_core.interfaces.SpellCastingEntity
import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentEffect
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.entity.Variants
import me.fzzyhmstrs.amethyst_imbuement.entity.goal.ConstructLookGoal
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterSound
import net.minecraft.entity.*
import net.minecraft.entity.attribute.DefaultAttributeContainer
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.data.DataTracker
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.random.Random
import net.minecraft.world.LocalDifficulty
import net.minecraft.world.ServerWorldAccess
import net.minecraft.world.World

open class BaseHamsterEntity: PlayerCreatedConstructEntity, SpellCastingEntity {

    constructor(entityType: EntityType<BaseHamsterEntity>, world: World): super(entityType, world)

    constructor(entityType: EntityType<BaseHamsterEntity>, world: World, ageLimit: Int, createdBy: LivingEntity? = null, augmentEffect: AugmentEffect? = null, level: Int = 1) : super(entityType, world, ageLimit, createdBy, augmentEffect, level){
    }

    companion object {
        private  val baseMaxHealth = AiConfig.entities.hamster.baseHealth.get()
        private const val baseMoveSpeed = 0.25
        private  val baseAttackDamage = AiConfig.entities.hamster.baseSummonDamage.get()
        internal val HAMSTER_VARIANT = DataTracker.registerData(BaseHamsterEntity::class.java, Variants.HAMSTER.trackedDataHandler())

        fun createBaseHamsterAttributes(): DefaultAttributeContainer.Builder {
            return createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, baseMaxHealth)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, baseMoveSpeed)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, baseAttackDamage.toDouble())
        }
    }

    override fun initGoals() {
        super.initGoals()
        goalSelector.add(6, ConstructLookGoal(this))
    }

    override fun initialize(
        world: ServerWorldAccess,
        difficulty: LocalDifficulty,
        spawnReason: SpawnReason,
        entityData: EntityData?,
        entityNbt: NbtCompound?
    ): EntityData? {
        val hamster = Variants.HAMSTER.randomVariant() ?: return super.initialize(world, difficulty, spawnReason, entityData, entityNbt)
        setVariant(hamster)
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt)
    }

    override fun initDataTracker() {
        super.initDataTracker()
        dataTracker.startTracking(HAMSTER_VARIANT, Variants.DWARF)
    }

    fun getVariant(): Variants.HamsterVariant {
        return dataTracker.get(HAMSTER_VARIANT)
    }

    fun setVariant(variant: Variants.HamsterVariant){
        dataTracker.set(HAMSTER_VARIANT,variant)
    }

    override fun initEquipment(random: Random ,difficulty: LocalDifficulty) {
        for (entry in classEquipment()){
            this.equipStack(entry.key, entry.value.copy())
        }
    }

    open fun classEquipment(): Map<EquipmentSlot, ItemStack>{
        return mapOf()
    }

    fun setMainHand(stack: ItemStack){
        this.equipStack(EquipmentSlot.MAINHAND,stack)
    }

    fun setArmor(stack: ItemStack){
        this.equipStack(EquipmentSlot.HEAD,stack)
    }

    override fun getRotationVec3d(): Vec3d {
        val target = this.target
        return if (target != null){
            val vec1 = target.pos.add(0.0,target.height.toDouble()/2.0,0.0)
            val vec2 = this.eyePos
            vec1.subtract(vec2).normalize()
        } else {
            this.rotationVector
        }
    }

    override fun getAmbientSound(): SoundEvent? {
        return RegisterSound.HAMSTER_AMBIENT
    }

    override fun getHurtSound(source: DamageSource): SoundEvent {
        return RegisterSound.HAMSTER_HIT
    }

    override fun getDeathSound(): SoundEvent {
        return RegisterSound.HAMSTER_DIE
    }

    override fun getStepSound(): SoundEvent {
        return SoundEvents.ENTITY_TURTLE_SHAMBLE_BABY
    }

    override fun getName(): Text {
        return getVariant().getName(this.type)
    }

    override fun writeCustomDataToNbt(nbt: NbtCompound) {
        super.writeCustomDataToNbt(nbt)
        Variants.HAMSTER.writeNbt(nbt, getVariant())
    }

    override fun readCustomDataFromNbt(nbt: NbtCompound) {
        super.readCustomDataFromNbt(nbt)
        if (nbt.contains("hamster_variant")) {
            val id = Identifier.tryParse(nbt.getString("hamster_variant"))
            val variant = Variants.HAMSTER.get(id)
            setVariant(variant)
        } else {
            setVariant(Variants.HAMSTER.readNbt(nbt))
        }
    }

}
