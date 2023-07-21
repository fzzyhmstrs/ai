package me.fzzyhmstrs.amethyst_imbuement.entity.living

import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.entity.goal.ConstructLookGoal
import me.fzzyhmstrs.amethyst_imbuement.entity.goal.ShootProjectileGoal
import net.minecraft.entity.EntityGroup
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.ai.goal.*
import net.minecraft.entity.attribute.DefaultAttributeContainer
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.mob.MobEntity
import net.minecraft.entity.mob.Monster
import net.minecraft.entity.mob.PathAwareEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.world.World

open class BonesEntity: PlayerCreatedConstructEntity {

    constructor(entityType: EntityType<BonesEntity>, world: World): super(entityType, world)

    constructor(entityType: EntityType<BonesEntity>, world: World, ageLimit: Int, createdBy: LivingEntity? = null) : super(entityType, world, ageLimit, createdBy)

    companion object {
        private  val baseMaxHealth = AiConfig.entities.bones.baseHealth.get()
        private const val baseMoveSpeed = 0.3
        private  val baseAttackDamage = AiConfig.entities.bones.baseDamage.get()

        fun createBonesAttributes(): DefaultAttributeContainer.Builder {
            return createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, baseMaxHealth)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, baseMoveSpeed)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, baseAttackDamage.toDouble())
        }
    }

    override fun initGoals() {
        goalSelector.add(1, ShootProjectileGoal(this))
        goalSelector.add(4, WanderNearTargetGoal(this, 0.9, 16.0f))
        goalSelector.add(4, WanderAroundPointOfInterestGoal(this as PathAwareEntity, 0.6, false))
        goalSelector.add(5, IronGolemWanderAroundGoal(this, 0.6))
        goalSelector.add(6, ConstructLookGoal(this))
        goalSelector.add(7, LookAtEntityGoal(this, PlayerEntity::class.java, 6.0f))
        goalSelector.add(8, LookAroundGoal(this))

        targetSelector.add(2, RevengeGoal(this, *arrayOfNulls(0)))
        targetSelector.add(3, ActiveTargetGoal(this, PlayerEntity::class.java, 10, true, false) { entity: LivingEntity? -> shouldAngerAt(entity) })
        targetSelector.add(3, ActiveTargetGoal(this, MobEntity::class.java, 5, false, false) { entity: LivingEntity? -> entity is Monster })
        targetSelector.add(4, UniversalAngerGoal(this, true))
    }

    override var entityGroup: EntityGroup = EntityGroup.UNDEAD

    override fun getAmbientSound(): SoundEvent? {
        return SoundEvents.ENTITY_ZOMBIE_AMBIENT
    }

    override fun getHurtSound(source: DamageSource): SoundEvent {
        return SoundEvents.ENTITY_SKELETON_HURT
    }

    override fun getDeathSound(): SoundEvent {
        return SoundEvents.ENTITY_ZOMBIE_DEATH
    }

    override fun getStepSound(): SoundEvent {
        return SoundEvents.ENTITY_ZOMBIE_STEP
    }
}