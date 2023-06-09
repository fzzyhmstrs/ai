package me.fzzyhmstrs.amethyst_imbuement.entity.living

import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentEffect
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import net.minecraft.entity.EntityGroup
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.ai.goal.*
import net.minecraft.entity.attribute.DefaultAttributeContainer
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.mob.BlazeEntity
import net.minecraft.entity.mob.MobEntity
import net.minecraft.entity.mob.Monster
import net.minecraft.entity.mob.PathAwareEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.projectile.SmallFireballEntity
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.world.World
import net.minecraft.world.WorldEvents
import java.util.*

@Suppress("PrivatePropertyName")
open class BonestormEntity: PlayerCreatedConstructEntity {

    constructor(entityType: EntityType<BonestormEntity>, world: World): super(entityType, world)

    constructor(entityType: EntityType<BonestormEntity>, world: World, ageLimit: Int, createdBy: LivingEntity? = null, augmentEffect: AugmentEffect? = null, level: Int = 1) : super(entityType, world, ageLimit, createdBy, augmentEffect, level)

    companion object {
        private  val baseMaxHealth = AiConfig.entities.bonestorm.baseHealth.get()
        private const val baseMoveSpeed = 0.23
        private  val baseAttackDamage = AiConfig.entities.bonestorm.baseDamage.get()


        fun createBonestormAttributes(): DefaultAttributeContainer.Builder {
            return createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, baseMaxHealth)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, baseMoveSpeed)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, baseAttackDamage.toDouble())
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 24.0)
        }
    }

    override var entityGroup: EntityGroup = EntityGroup.UNDEAD

    override fun initGoals() {
        goalSelector.add(5, GoToWalkTargetGoal(this, 1.0))
        goalSelector.add(7, WanderAroundFarGoal(this as PathAwareEntity, 1.0, 0.0f))
        goalSelector.add(8, LookAtEntityGoal(this, PlayerEntity::class.java, 6.0f))
        goalSelector.add(8, LookAroundGoal(this))
        targetSelector.add(2, RevengeGoal(this, *arrayOfNulls(0)))
        targetSelector.add(3, ActiveTargetGoal(this, PlayerEntity::class.java, 10, true, false) { entity: LivingEntity? -> shouldAngerAt(entity) })
        targetSelector.add(3, ActiveTargetGoal(this, MobEntity::class.java, 5, false, false) { entity: LivingEntity? -> entity is Monster })
        targetSelector.add(4, UniversalAngerGoal(this, true))
    }


    override fun getAmbientSound(): SoundEvent? {
        return SoundEvents.ENTITY_BLAZE_AMBIENT
    }

    override fun getHurtSound(source: DamageSource): SoundEvent {
        return SoundEvents.ENTITY_WITHER_SKELETON_HURT
    }

    override fun getDeathSound(): SoundEvent {
        return SoundEvents.ENTITY_WITHER_SKELETON_DEATH
    }

    override fun getStepSound(): SoundEvent {
        return SoundEvents.ENTITY_ZOMBIE_STEP
    }

    internal class ShootFireballGoal(private val bonestorm: BonestormEntity) : Goal() {
        private var fireballsFired = 0
        private var fireballCooldown = 0
        private var targetNotVisibleTicks = 0
        override fun canStart(): Boolean {
            val livingEntity = bonestorm.target
            return livingEntity != null && livingEntity.isAlive && bonestorm.canTarget(livingEntity)
        }

        override fun start() {
            fireballsFired = 0
        }

        override fun stop() {
            bonestorm.setFireActive(false)
            targetNotVisibleTicks = 0
        }

        override fun shouldRunEveryTick(): Boolean {
            return true
        }

        override fun tick() {
            --fireballCooldown
            val livingEntity = bonestorm.target ?: return
            val bl = bonestorm.visibilityCache.canSee(livingEntity)
            targetNotVisibleTicks = if (bl) 0 else ++targetNotVisibleTicks
            val d = bonestorm.squaredDistanceTo(livingEntity)
            if (d < 4.0) {
                if (!bl) {
                    return
                }
                if (fireballCooldown <= 0) {
                    fireballCooldown = 20
                    bonestorm.tryAttack(livingEntity)
                }
                bonestorm.moveControl.moveTo(livingEntity.x, livingEntity.y, livingEntity.z, 1.0)
            } else if (d < followRange * followRange && bl) {
                val e = livingEntity.x - bonestorm.x
                val f = livingEntity.getBodyY(0.5) - bonestorm.getBodyY(0.5)
                val g = livingEntity.z - bonestorm.z
                if (fireballCooldown <= 0) {
                    ++fireballsFired
                    if (fireballsFired == 1) {
                        fireballCooldown = 60
                        bonestorm.setFireActive(true)
                    } else if (fireballsFired <= 4) {
                        fireballCooldown = 6
                    } else {
                        fireballCooldown = 100
                        fireballsFired = 0
                        bonestorm.setFireActive(false)
                    }
                    if (fireballsFired > 1) {
                        val h = Math.sqrt(Math.sqrt(d)) * 0.5
                        if (!bonestorm.isSilent) {
                            bonestorm.world.syncWorldEvent(null, WorldEvents.BLAZE_SHOOTS, bonestorm.blockPos, 0)
                        }
                        for (i in 0..0) {
                            val smallFireballEntity = SmallFireballEntity(
                                bonestorm.world,
                                bonestorm,
                                bonestorm.random.nextTriangular(e, 2.297 * h),
                                f,
                                bonestorm.random.nextTriangular(g, 2.297 * h)
                            )
                            smallFireballEntity.setPosition(
                                smallFireballEntity.x,
                                bonestorm.getBodyY(0.5) + 0.5,
                                smallFireballEntity.z
                            )
                            bonestorm.world.spawnEntity(smallFireballEntity)
                        }
                    }
                }
                bonestorm.lookControl.lookAt(livingEntity, 10.0f, 10.0f)
            } else if (targetNotVisibleTicks < 5) {
                bonestorm.moveControl.moveTo(livingEntity.x, livingEntity.y, livingEntity.z, 1.0)
            }
            super.tick()
        }

        private val followRange: Double = bonestorm.getAttributeValue(EntityAttributes.GENERIC_FOLLOW_RANGE)

        init {
            controls = EnumSet.of(Control.MOVE, Control.LOOK)
        }
    }

}