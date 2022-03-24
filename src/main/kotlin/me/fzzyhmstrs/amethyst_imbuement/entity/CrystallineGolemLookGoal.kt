package me.fzzyhmstrs.amethyst_imbuement.entity

import net.minecraft.entity.ai.TargetPredicate
import net.minecraft.entity.ai.goal.Goal
import net.minecraft.entity.passive.VillagerEntity

@Suppress("PrivatePropertyName")
class CrystallineGolemLookGoal(_golem: CrystallineGolemEntity): Goal() {

    private val CLOSE_VILLAGER_PREDICATE = TargetPredicate.createNonAttackable().setBaseMaxDistance(6.0)
    private val golem = _golem
    private var targetVillager: VillagerEntity? = null
    private var lookCountdown = 0

    override fun canStart(): Boolean {
        if (!golem.world.isDay) {
            return false
        }
        if (golem.random.nextInt(8000) != 0) {
            return false
        }
        targetVillager = golem.world.getClosestEntity(
            VillagerEntity::class.java, CLOSE_VILLAGER_PREDICATE,
            golem, golem.x, golem.y, golem.z, golem.boundingBox.expand(6.0, 2.0, 6.0)
        )
        return targetVillager != null
    }

    override fun shouldContinue(): Boolean {
        return lookCountdown > 0
    }

    override fun start() {
        lookCountdown = getTickCount(400)
        golem.setLookingAtVillager(true)
    }

    override fun stop() {
        golem.setLookingAtVillager(false)
        targetVillager = null
    }

    override fun tick() {
        golem.lookControl.lookAt(targetVillager, 30.0f, 30.0f)
        --lookCountdown
    }
}