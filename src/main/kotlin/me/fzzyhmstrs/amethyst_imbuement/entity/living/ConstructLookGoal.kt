package me.fzzyhmstrs.amethyst_imbuement.entity.living

import net.minecraft.entity.ai.TargetPredicate
import net.minecraft.entity.ai.goal.Goal
import net.minecraft.entity.passive.VillagerEntity

@Suppress("PrivatePropertyName")
class ConstructLookGoal(private val construct: PlayerCreatedConstructEntity): Goal() {

    private val CLOSE_VILLAGER_PREDICATE = TargetPredicate.createNonAttackable().setBaseMaxDistance(6.0)
    private var targetVillager: VillagerEntity? = null
    private var lookCountdown = 0

    override fun canStart(): Boolean {
        if (!construct.world.isDay) {
            return false
        }
        if (construct.random.nextInt(8000) != 0) {
            return false
        }
        targetVillager = construct.world.getClosestEntity(
            VillagerEntity::class.java, CLOSE_VILLAGER_PREDICATE,
            construct, construct.x, construct.y, construct.z, construct.boundingBox.expand(6.0, 2.0, 6.0)
        )
        return targetVillager != null
    }

    override fun shouldContinue(): Boolean {
        return lookCountdown > 0
    }

    override fun start() {
        lookCountdown = getTickCount(400)
        construct.setLookingAtVillager(true)
    }

    override fun stop() {
        construct.setLookingAtVillager(false)
        targetVillager = null
    }

    override fun tick() {
        construct.lookControl.lookAt(targetVillager, 30.0f, 30.0f)
        --lookCountdown
    }
}