package me.fzzyhmstrs.amethyst_imbuement.entity.living

import me.fzzyhmstrs.amethyst_core.entity_util.ModifiableEffectEntity
import me.fzzyhmstrs.amethyst_core.entity_util.Scalable
import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter_util.SpellDamageSource
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.entity.goal.CallForConstructHelpGoal
import me.fzzyhmstrs.amethyst_imbuement.entity.goal.FollowSummonerGoal
import me.fzzyhmstrs.fzzy_core.entity_util.PlayerCreatable
import net.minecraft.entity.*
import net.minecraft.entity.ai.goal.*
import net.minecraft.entity.attribute.DefaultAttributeContainer
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.data.DataTracker
import net.minecraft.entity.data.TrackedData
import net.minecraft.entity.data.TrackedDataHandlerRegistry
import net.minecraft.entity.passive.AbstractHorseEntity
import net.minecraft.entity.passive.AnimalEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.Inventory
import net.minecraft.item.HorseArmorItem
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.recipe.Ingredient
import net.minecraft.registry.tag.DamageTypeTags
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.BlockSoundGroup
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.math.MathHelper
import net.minecraft.world.EntityView
import net.minecraft.world.LocalDifficulty
import net.minecraft.world.ServerWorldAccess
import net.minecraft.world.World
import java.util.*

@Suppress("LeakingThis")
open class ChorseEntity(entityType: EntityType<out ChorseEntity>, world: World): AbstractHorseEntity(entityType,world),
    PlayerCreatable, ModifiableEffectEntity, Scalable {

    constructor(entityType: EntityType<out ChorseEntity>, world: World, createdBy: LivingEntity? = null, augmentEffect: AugmentEffect? = null, level: Int = 1) : this(entityType, world){
        this.createdBy = createdBy?.uuid
        this.entityOwner = createdBy
        if (createdBy != null) {
            startTrackingOwner(createdBy)
        }

        if (augmentEffect != null){
            this.passEffects(augmentEffect,level)
        }

        if (world is ServerWorld) {
            initialize(world,world.getLocalDifficulty(this.blockPos),SpawnReason.MOB_SUMMONED,null,null)
        }
    }

    companion object{
        protected val HORSE_ARMOR_BONUS_ID = UUID.fromString("556E1665-8B10-40C8-8F9D-CF9B1667F295")
        protected val BREEDING_INGREDIENT = Ingredient.ofItems(
            Items.WHEAT_SEEDS,
            Items.MELON_SEEDS,
            Items.PUMPKIN_SEEDS,
            Items.BEETROOT_SEEDS,
            Items.TORCHFLOWER_SEEDS,
            Items.PITCHER_POD
        )
        internal val SCALE = DataTracker.registerData(ChorseEntity::class.java,TrackedDataHandlerRegistry.FLOAT)
        fun createChorseBaseAttributes(): DefaultAttributeContainer.Builder{
            return createMobAttributes()
                .add(EntityAttributes.HORSE_JUMP_STRENGTH, AiConfig.entities.chorse.baseJumpStrength.get())
                .add(EntityAttributes.GENERIC_MAX_HEALTH, AiConfig.entities.chorse.baseHealth.get())
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, AiConfig.entities.chorse.baseMoveSpeed.get())
        }
    }

    private val followSummonerGoal = FollowSummonerGoal(this, null, 1.0, 12.0f, 2.0f, false)
    private val callForConstructHelpGoal = CallForConstructHelpGoal(this)

    override var maxAge = -1
    override var createdBy: UUID? = null
    override var entityOwner: LivingEntity? = null
    override var entityEffects: AugmentEffect = AugmentEffect()
    private var level = 1
    open var entityGroup: EntityGroup = EntityGroup.DEFAULT
    internal var entityScale = 1f
    protected var trackingOwner = false

    var flapProgress = 0f
    var maxWingDeviation = 0f
    var prevMaxWingDeviation = 0f
    var prevFlapProgress = 0f
    private var flapSpeed = 1.0f

    override fun tickMovement() {
        super.tickMovement()
        prevFlapProgress = flapProgress
        prevMaxWingDeviation = maxWingDeviation
        maxWingDeviation += (if (this.isOnGround) -1.0f else 4.0f) * 0.3f
        maxWingDeviation = MathHelper.clamp(maxWingDeviation, 0.0f, 1.0f)
        if (!this.isOnGround && flapSpeed < 1.0f) {
            flapSpeed = 1.0f
        }
        flapSpeed *= 0.9f
        val vec3d = velocity
        if (!this.isOnGround && vec3d.y < 0.0) {
            velocity = if (hasPassengers()) {
                vec3d.multiply(1.0, 0.8, 1.0)
            } else {
                vec3d.multiply(1.0, 0.6, 1.0)
            }
        }
        flapProgress += flapSpeed * 2.0f
    }

    override fun handleFallDamage(fallDistance: Float, damageMultiplier: Float, damageSource: DamageSource?): Boolean {
        return false
    }

    override fun passEffects(ae: AugmentEffect, level: Int) {
        this.entityEffects = ae.copy()
        this.level = level
    }

    init{
        stepHeight = 1.0f
        if (!world.isClient)
            initOwnerGoals()
    }

    override fun initGoals() {
        goalSelector.add(0, SwimGoal(this))
        goalSelector.add(1, EscapeDangerGoal(this, 1.2))
        goalSelector.add(6, WanderAroundFarGoal(this, 0.7))
        goalSelector.add(7, LookAtEntityGoal(this, PlayerEntity::class.java, 6.0f))
        goalSelector.add(8, LookAroundGoal(this))
    }

    private fun initOwnerGoals(){
        goalSelector.add(2,followSummonerGoal)
        goalSelector.add(3,callForConstructHelpGoal)
    }

    override fun initDataTracker() {
        super.initDataTracker()
        dataTracker.startTracking(SCALE,1f)
    }

    override fun damage(source: DamageSource, amount: Float): Boolean {
        if (isInvulnerableTo(source)) {
            return false
        }
        val attacker = source.attacker
        if (attacker is LivingEntity){
            val spell = if (source is SpellDamageSource) source.getSpell() else null
            if(!AiConfig.entities.shouldItHitBase(attacker, this,AiConfig.Entities.Options.NONE, spell)) return false
        }
        if (source.isIn(DamageTypeTags.ALWAYS_TRIGGERS_SILVERFISH)) {
            this.callForConstructHelpGoal.onHurt()
        }
        return super.damage(source, amount)
    }

    override fun getGroup(): EntityGroup {
        return entityGroup
    }

    open fun setGroup(entityGroup: EntityGroup){
        this.entityGroup = entityGroup
    }

    override fun isBreedingItem(stack: ItemStack?): Boolean {
        return BREEDING_INGREDIENT.test(stack)
    }

    override fun writeCustomDataToNbt(nbt: NbtCompound) {
        super.writeCustomDataToNbt(nbt)
        writePlayerCreatedNbt(nbt)
        nbt.putFloat("scale_factor",getScale())
        if (!items.getStack(1).isEmpty) {
            nbt.put("ArmorItem", items.getStack(1).writeNbt(NbtCompound()))
        }
    }

    override fun readCustomDataFromNbt(nbt: NbtCompound) {
        super.readCustomDataFromNbt(nbt)
        readPlayerCreatedNbt(world, nbt)
        setScale(nbt.getFloat("scale_factor").takeIf { it > 0f } ?: 1f)
        if (nbt.contains("ArmorItem", NbtElement.COMPOUND_TYPE.toInt())){
            val itemStack = ItemStack.fromNbt(nbt.getCompound("ArmorItem"))
            if (isHorseArmor(itemStack))
                items.setStack(1, itemStack)
        }
    }

    override fun canBreedWith(other: AnimalEntity): Boolean {
        return false
    }

    override fun hasArmorSlot(): Boolean {
        return true
    }

    fun getArmorType(): ItemStack? {
        return getEquippedStack(EquipmentSlot.CHEST)
    }

    private fun equipArmor(stack: ItemStack) {
        equipStack(EquipmentSlot.CHEST, stack)
        setEquipmentDropChance(EquipmentSlot.CHEST, 0.0f)
    }

    override fun updateSaddle() {
        if (world.isClient) {
            return
        }
        super.updateSaddle()
        setArmorTypeFromStack(items.getStack(1))
        setEquipmentDropChance(EquipmentSlot.CHEST, 0.0f)
    }

    private fun setArmorTypeFromStack(stack: ItemStack) {
        this.equipArmor(stack)
        if (!world.isClient) {
            getAttributeInstance(EntityAttributes.GENERIC_ARMOR)?.removeModifier(HORSE_ARMOR_BONUS_ID)
            if (!isHorseArmor(stack)) return
            val i: Int = (stack.item as HorseArmorItem).bonus
            getAttributeInstance(EntityAttributes.GENERIC_ARMOR)?.addTemporaryModifier(
                EntityAttributeModifier(
                    HORSE_ARMOR_BONUS_ID,
                    "Horse armor bonus",
                    i.toDouble(),
                    EntityAttributeModifier.Operation.ADDITION
                )
            )

        }
    }

    override fun onInventoryChanged(sender: Inventory?) {
        val itemStack = getArmorType()
        super.onInventoryChanged(sender)
        val itemStack2 = getArmorType()
        if (age > 20 && isHorseArmor(itemStack2!!) && itemStack != itemStack2) {
            playSound(SoundEvents.ENTITY_HORSE_ARMOR, 0.5f, 1.0f)
        }
    }

    override fun playWalkSound(group: BlockSoundGroup) {
        super.playWalkSound(group)
        if (random.nextInt(10) == 0) {
            playSound(SoundEvents.ENTITY_CHICKEN_AMBIENT, group.getVolume() * 0.6f, group.getPitch())
        }
    }

    override fun getAmbientSound(): SoundEvent? {
        return SoundEvents.ENTITY_CHICKEN_AMBIENT
    }

    override fun getDeathSound(): SoundEvent? {
        return SoundEvents.ENTITY_CHICKEN_DEATH
    }

    override fun getEatSound(): SoundEvent? {
        return SoundEvents.ENTITY_HORSE_EAT
    }

    override fun getHurtSound(source: DamageSource?): SoundEvent? {
        return SoundEvents.ENTITY_CHICKEN_HURT
    }

    override fun getAngrySound(): SoundEvent? {
        return SoundEvents.ENTITY_CHICKEN_AMBIENT
    }

    override fun initialize(
        world: ServerWorldAccess?,
        difficulty: LocalDifficulty?,
        spawnReason: SpawnReason?,
        entityData: EntityData?,
        entityNbt: NbtCompound?
    ): EntityData? {
        this.items.setStack(0, ItemStack(Items.SADDLE))
        this.updateSaddle()
        this.isTame = true
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt)
    }

    override fun isHorseArmor(item: ItemStack): Boolean {
        return item.item is HorseArmorItem
    }

    fun setConstructOwner(owner: LivingEntity?){
        createdBy = owner?.uuid
        this.entityOwner = owner
        if (!trackingOwner && owner != null){
            startTrackingOwner(owner)
        }
    }

    override fun getOwnerUuid(): UUID? {
        return createdBy
    }

    override fun method_48926(): EntityView {
        return this.world
    }

    override fun getOwner(): LivingEntity? {
        return if (entityOwner != null) {
            entityOwner
        } else if (world is ServerWorld && createdBy != null) {
            val o = (world as ServerWorld).getEntity(createdBy)
            if (o != null && o is LivingEntity) {
                this.entityOwner = o
                if (!trackingOwner){
                    startTrackingOwner(o)
                }
                o
            } else {
                null
            }

        }else {
            null
        }
    }

    private fun startTrackingOwner(currentOwner: LivingEntity){
        followSummonerGoal.setSummoner(currentOwner)
        trackingOwner = true
    }

    override fun onTrackedDataSet(data: TrackedData<*>?) {
        super.onTrackedDataSet(data)
        if(SCALE.equals(data)){
            entityScale = getScale()
        }
    }

    override fun getScale(): Float {
        return dataTracker.get(SCALE)
    }

    //need to also do the bounding boxxxxxxx
    override fun setScale(scale: Float) {
        dataTracker.set(SCALE,MathHelper.clamp(scale,0.0f,20.0f))
    }

}
