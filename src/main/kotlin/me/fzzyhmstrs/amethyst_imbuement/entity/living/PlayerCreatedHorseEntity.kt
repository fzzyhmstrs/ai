package me.fzzyhmstrs.amethyst_imbuement.entity.living

import me.fzzyhmstrs.amethyst_core.augments.paired.PairedAugments
import me.fzzyhmstrs.amethyst_core.augments.paired.ProcessContext
import me.fzzyhmstrs.amethyst_core.entity.ModifiableEffectContainer
import me.fzzyhmstrs.amethyst_core.entity.ModifiableEffectEntity
import me.fzzyhmstrs.amethyst_core.entity.Scalable
import me.fzzyhmstrs.amethyst_core.modifier.AugmentEffect
import me.fzzyhmstrs.fzzy_core.entity_util.PlayerCreatable
import net.minecraft.entity.*
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.data.DataTracker
import net.minecraft.entity.data.TrackedDataHandlerRegistry
import net.minecraft.entity.passive.AbstractHorseEntity
import net.minecraft.entity.passive.AnimalEntity
import net.minecraft.inventory.Inventory
import net.minecraft.item.HorseArmorItem
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
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

open class PlayerCreatedHorseEntity(entityType: EntityType<out AbstractHorseEntity>?, world: World?) : AbstractHorseEntity(entityType, world),
    ModifiableEffectEntity, PlayerCreatable, Scalable {
    companion object{

        protected val HORSE_ARMOR_BONUS_ID = UUID.fromString("556E1665-8B10-40C8-8F9D-CF9B1667F295")

        protected val SCALE = DataTracker.registerData(PlayerCreatedHorseEntity::class.java, TrackedDataHandlerRegistry.FLOAT)
    }

    override var entityEffects: AugmentEffect = AugmentEffect()
    override var level: Int = 1
    override var modifiableEffects: ModifiableEffectContainer = ModifiableEffectContainer()
    override var processContext: ProcessContext = ProcessContext.FROM_ENTITY_CONTEXT
    override var spells: PairedAugments = PairedAugments()

    override var createdBy: UUID? = null
    override var entityOwner: LivingEntity? = null
    override var maxAge: Int = -1

    fun setPlayerHorseOwner(owner:LivingEntity?){
        this.createdBy = owner?.uuid
        this.entityOwner = owner
    }

    override fun passEffects(spells: PairedAugments, ae: AugmentEffect, level: Int) {
        super.passEffects(spells, ae, level)
        val chk = world
        if (chk is ServerWorld) {
            initialize(chk,chk.getLocalDifficulty(this.blockPos), SpawnReason.MOB_SUMMONED,null,null)
        }
    }

    override fun initDataTracker() {
        super.initDataTracker()
        dataTracker.startTracking(SCALE,1f)
    }

    override fun writeCustomDataToNbt(nbt: NbtCompound) {
        super.writeCustomDataToNbt(nbt)
        writePlayerCreatedNbt(nbt)
        writeModifiableNbt(nbt)
        nbt.putFloat("scale_factor",getScale())
        if (!items.getStack(1).isEmpty) {
            nbt.put("ArmorItem", items.getStack(1).writeNbt(NbtCompound()))
        }
    }

    override fun readCustomDataFromNbt(nbt: NbtCompound) {
        super.readCustomDataFromNbt(nbt)
        readPlayerCreatedNbt(world, nbt)
        readModifiableNbt(nbt)
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

    override fun getScale(): Float {
        return dataTracker.get(SCALE)
    }

    override fun setScale(scale: Float) {
        dataTracker.set(SCALE, MathHelper.clamp(scale,0.0f,20.0f))
        this.calculateDimensions()
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
                entityOwner = o
                o
            } else {
                null
            }
        }else {
            null
        }
    }
}