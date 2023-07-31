package me.fzzyhmstrs.amethyst_imbuement.entity.hamster

import me.fzzyhmstrs.amethyst_core.interfaces.SpellCastingEntity
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.entity.goal.ConstructLookGoal
import me.fzzyhmstrs.amethyst_imbuement.entity.living.PlayerCreatedConstructEntity
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterSound
import me.fzzyhmstrs.fzzy_config.config_util.ConfigSection
import me.fzzyhmstrs.fzzy_config.config_util.ReadMeText
import me.fzzyhmstrs.fzzy_config.validated_field.ValidatedDouble
import me.fzzyhmstrs.fzzy_config.validated_field.ValidatedFloat
import me.fzzyhmstrs.fzzy_config.validated_field.ValidatedInt
import me.fzzyhmstrs.fzzy_core.coding_util.AcText
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

    constructor(entityType: EntityType<out BaseHamsterEntity>, world: World): super(entityType, world)

    constructor(entityType: EntityType<out BaseHamsterEntity>, world: World, ageLimit: Int, createdBy: LivingEntity? = null) : super(entityType, world, ageLimit, createdBy){
    }

    class Hamster: ConfigSection(Header.Builder().space().add("readme.entities.hamster_1").add("readme.entities.hamster_2").build()){
        @ReadMeText("readme.entities.hamster.baseLifespan")
        var baseLifespan = ValidatedInt(3600,180000,-1)
        var baseMoveSpeed = ValidatedDouble(0.25,1.0,0.01)
        var baseHealth = ValidatedDouble(8.0,40.0,1.0)
        @ReadMeText("readme.entities.hamster.baseDamage")
        var baseSummonDamage = ValidatedFloat(1.0f,10.0f,0.0f)
        var baseHamptertimeDamage = ValidatedFloat(2.0f,10.0f,0.0f)
        var perLvlDamage = ValidatedFloat(0.1f,1.0f,0.0f)
        var hamptertimeBaseSpawnCount = ValidatedDouble(10.0,100.0,1.0)
        var hamptertimePerLvlSpawnCount = ValidatedDouble(0.5,5.0,0.0)
    }

    companion object {
        internal val HAMSTER_VARIANT = DataTracker.registerData(
            BaseHamsterEntity::class.java,
            HamsterVariant.TRACKED_HAMSTER
        )

        fun createBaseHamsterAttributes(): DefaultAttributeContainer.Builder {
            return createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, AiConfig.entities.hamster.baseHealth.get())
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, AiConfig.entities.hamster.baseMoveSpeed.get())
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, AiConfig.entities.hamster.baseSummonDamage.get().toDouble())
        }
    }

    private var jamsterName: Text? = null

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

        val hamster = HamsterVariant.randomVariant(world.random) ?:return super.initialize(world, difficulty, spawnReason, entityData, entityNbt)
        setVariant(hamster)

        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt)
    }

    override fun initDataTracker() {
        super.initDataTracker()
        dataTracker.startTracking(HAMSTER_VARIANT, HamsterVariant.DWARF)
    }

    fun getVariant(): HamsterVariant {
        return dataTracker.get(HAMSTER_VARIANT)
    }

    fun setVariant(variant: HamsterVariant){
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
        if (getVariant() == HamsterVariant.JAMSTER){
            if (jamsterName == null){
                jamsterName = AcText.translatable(this.type.translationKey + ".jeans")
            }
            return jamsterName as Text
        }
        return super.getName()
    }

    override fun writeCustomDataToNbt(nbt: NbtCompound) {
        super.writeCustomDataToNbt(nbt)
        val id = HamsterVariant.HAMSTERS.getId(getVariant())
        nbt.putString("hamster_variant",id.toString())
    }

    override fun readCustomDataFromNbt(nbt: NbtCompound) {
        super.readCustomDataFromNbt(nbt)
        val id = Identifier.tryParse(nbt.getString("hamster_variant"))
        val variant = HamsterVariant.HAMSTERS.get(id)
        setVariant(variant)
    }

}
