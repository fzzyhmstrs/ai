package me.fzzyhmstrs.amethyst_imbuement.entity.living

import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentEffect
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterArmor
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterSound
import net.minecraft.entity.*
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
open class BaseHamsterEntity: PlayerCreatedConstructEntity {

    constructor(entityType: EntityType<BaseHamsterEntity>, world: World): super(entityType, world)

    constructor(entityType: EntityType<BaseHamsterEntity>, world: World, ageLimit: Int, createdBy: LivingEntity? = null, augmentEffect: AugmentEffect? = null, level: Int = 1, bonusEquipment: Int = 0) : super(entityType, world, ageLimit, createdBy, augmentEffect, level){
        this.bonusEquipment = bonusEquipment
    }

    companion object {
        private  val baseMaxHealth = AiConfig.entities.hamster.baseHealth.get()
        private const val baseMoveSpeed = 0.3
        private  val baseAttackDamage = AiConfig.entities.hamster.baseDamage.get()

        fun createBasHamsterAttributes(): DefaultAttributeContainer.Builder {
            return createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, baseMaxHealth)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, baseMoveSpeed)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, baseAttackDamage.toDouble())
        }
    }

    private var bonusEquipment = 0

    override fun initGoals() {
        super.initGoals()
        goalSelector.add(5, ConstructLookGoal(this))
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

    override fun initEquipment(random: Random ,difficulty: LocalDifficulty) {
        this.equipStack(EquipmentSlot.MAINHAND, ItemStack(RegisterItem.GLOWING_BLADE))
    }

}