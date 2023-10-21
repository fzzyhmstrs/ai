package me.fzzyhmstrs.amethyst_imbuement.entity.goal

import me.fzzyhmstrs.amethyst_imbuement.entity.spell.BoneShardEntity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.ai.goal.Goal
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.mob.MobEntity
import net.minecraft.util.math.Vec3d
import net.minecraft.world.WorldEvents
import java.util.*
import java.util.function.Consumer
import java.util.function.Supplier
import kotlin.math.sqrt

open class ShootProjectileGoal (
    protected val mobEntity: MobEntity,
    protected val ownerGetter: Supplier<LivingEntity>,
    protected val activeConsumer: Consumer<Boolean>,
    protected val meleeCooldown: Int = 20,
    protected val fireballCharge: Int = 60) : Goal() {
    private var fireballsFired = 0
    private var fireballCooldown = 0
    private var targetNotVisibleTicks = 0
    override fun canStart(): Boolean {
        val livingEntity = mobEntity.target
        return livingEntity != null && livingEntity.isAlive && mobEntity.canTarget(livingEntity)
    }

    override fun start() {
        fireballsFired = 0
    }

    override fun stop() {
        fireballCooldown = 0
        activeConsumer.accept(false)
        //bonestorm.setFireActive(false)
        targetNotVisibleTicks = 0
    }

    override fun shouldRunEveryTick(): Boolean {
        return true
    }

    override fun tick() {
        --fireballCooldown
        val livingEntity = mobEntity.target ?: return
        val bl = mobEntity.visibilityCache.canSee(livingEntity)
        targetNotVisibleTicks = if (bl) 0 else ++targetNotVisibleTicks
        val d = mobEntity.squaredDistanceTo(livingEntity)
        if (d < 4.0) {
            if (!bl) {
                return
            }
            if (fireballCooldown <= 0) {
                fireballCooldown = meleeCooldown
                mobEntity.tryAttack(livingEntity)
            }
            moveTo(livingEntity)
        } else if (d < followRange * followRange && bl) {

            if (fireballCooldown <= 0) {
                ++fireballsFired
                if (fireballsFired == 1) {
                    fireballCooldown = fireballCharge
                    activeConsumer.accept(true)
                } else if (fireballsFired <= 4) {
                    fireballCooldown = 6
                } else {
                    fireballCooldown = 100
                    fireballsFired = 0
                    activeConsumer.accept(false)
                    //bonestorm.setFireActive(false)
                }
                if (fireballsFired > 1) {
                    shootProjectile(d,livingEntity)
                }
            }
            lookAt(livingEntity)
        } else if (targetNotVisibleTicks < 5) {
            moveTo(livingEntity)
        }
        super.tick()
    }

    open fun lookAt(livingEntity: LivingEntity){
        mobEntity.lookControl.lookAt(livingEntity, 10f, 10f)
    }

    open fun moveTo(livingEntity: LivingEntity){
        mobEntity.moveControl.moveTo(livingEntity.x, livingEntity.y, livingEntity.z, 1.0)
    }

    protected open fun shootProjectile(d: Double, livingEntity: LivingEntity){
        val h = (sqrt(sqrt(d)) * 0.5f).toFloat()
        if (!mobEntity.isSilent) {
            mobEntity.world.syncWorldEvent(null, WorldEvents.BLAZE_SHOOTS, mobEntity.blockPos, 0)
        }
        val rot = Vec3d(livingEntity.x - mobEntity.x,livingEntity.getBodyY(0.5) - mobEntity.getBodyY(0.5),livingEntity.z - mobEntity.z)
        val pos  = Vec3d(mobEntity.x,mobEntity.getBodyY(0.5) + 0.5,mobEntity.z)
        val owner = ownerGetter.get()
        val bse = BoneShardEntity(mobEntity.world,owner,4.0f,1.75f*h,pos,rot)
        mobEntity.world.spawnEntity(bse)
    }

    private val followRange: Double = mobEntity.getAttributeValue(EntityAttributes.GENERIC_FOLLOW_RANGE)

    init {
        controls = EnumSet.of(Control.MOVE, Control.LOOK)
    }
}