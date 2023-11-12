package me.fzzyhmstrs.amethyst_imbuement.entity.living

import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentEffect
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.entity.goal.ConstructLookGoal
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterArmor
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterTool
import net.minecraft.entity.EntityGroup
import net.minecraft.entity.EntityType
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.attribute.DefaultAttributeContainer
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.damage.DamageSource
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.math.random.Random
import net.minecraft.world.LocalDifficulty
import net.minecraft.world.World

@Suppress("PrivatePropertyName")
open class UnhallowedEntity: PlayerCreatedConstructEntity {

    constructor(entityType: EntityType<UnhallowedEntity>, world: World): super(entityType, world)

    constructor(entityType: EntityType<UnhallowedEntity>, world: World, ageLimit: Int, createdBy: LivingEntity? = null, augmentEffect: AugmentEffect? = null, level: Int = 1, bonusEquipment: Int = 0) : super(entityType, world, ageLimit, createdBy, augmentEffect, level){
        this.bonusEquipment = bonusEquipment
    }

    companion object {
        private  val baseMaxHealth = AiConfig.entities.unhallowed.baseHealth.get()
        private const val baseMoveSpeed = 0.4
        private  val baseAttackDamage = AiConfig.entities.unhallowed.baseDamage.get()

        fun createUnhallowedAttributes(): DefaultAttributeContainer.Builder {
            return createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, baseMaxHealth)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, baseMoveSpeed)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, baseAttackDamage.toDouble())
                .add(EntityAttributes.GENERIC_ARMOR, 2.0)
        }
    }

    private var bonusEquipment = 0
    override var entityGroup: EntityGroup = EntityGroup.UNDEAD

    override fun initGoals() {
        super.initGoals()
        goalSelector.add(6, ConstructLookGoal(this))
    }


    override fun getAmbientSound(): SoundEvent? {
        return SoundEvents.ENTITY_ZOMBIE_AMBIENT
    }

    override fun getHurtSound(source: DamageSource): SoundEvent {
        return SoundEvents.ENTITY_ZOMBIE_HURT
    }

    override fun getDeathSound(): SoundEvent {
        return SoundEvents.ENTITY_ZOMBIE_DEATH
    }

    override fun getStepSound(): SoundEvent {
        return SoundEvents.ENTITY_ZOMBIE_STEP
    }

    override fun initEquipment(random: Random ,difficulty: LocalDifficulty) {
        bonusEquipment = bonus(this.level)
        when (bonusEquipment) {
            1 -> {
                this.equipStack(EquipmentSlot.HEAD, ItemStack(Items.LEATHER_HELMET))
                this.equipStack(EquipmentSlot.CHEST, ItemStack(Items.LEATHER_CHESTPLATE))
            }
            2 -> {
                this.equipStack(EquipmentSlot.HEAD, ItemStack(Items.CHAINMAIL_HELMET))
                this.equipStack(EquipmentSlot.CHEST, ItemStack(Items.CHAINMAIL_CHESTPLATE))
                this.equipStack(EquipmentSlot.MAINHAND, ItemStack(Items.WOODEN_SWORD))
            }
            3 -> {
                this.equipStack(EquipmentSlot.HEAD, ItemStack(Items.IRON_HELMET))
                this.equipStack(EquipmentSlot.CHEST, ItemStack(Items.IRON_CHESTPLATE))
                this.equipStack(EquipmentSlot.FEET, ItemStack(Items.IRON_BOOTS))
                this.equipStack(EquipmentSlot.MAINHAND, ItemStack(Items.STONE_SWORD))
            }
            4 -> {
                this.equipStack(EquipmentSlot.HEAD, ItemStack(RegisterArmor.STEEL_HELMET))
                this.equipStack(EquipmentSlot.CHEST, ItemStack(RegisterArmor.STEEL_CHESTPLATE))
                this.equipStack(EquipmentSlot.LEGS, ItemStack(RegisterArmor.STEEL_LEGGINGS))
                this.equipStack(EquipmentSlot.FEET, ItemStack(RegisterArmor.STEEL_BOOTS))
                this.equipStack(EquipmentSlot.MAINHAND, ItemStack(RegisterTool.GLOWING_BLADE))
            }
        }
        println(this.itemsEquipped)
    }

    private fun bonus(level: Int): Int{
        return if (level <= 5){
            0
        } else if (level <= 8){
            1
        } else if (level <= 10){
            2
        } else if (level <= 12){
            3
        } else {
            4
        }
    }

}