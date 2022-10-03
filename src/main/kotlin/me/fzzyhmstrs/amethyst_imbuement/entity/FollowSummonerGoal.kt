package me.fzzyhmstrs.amethyst_imbuement.entity

import net.minecraft.block.LeavesBlock
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.ai.goal.Goal
import net.minecraft.entity.ai.pathing.EntityNavigation
import net.minecraft.entity.ai.pathing.LandPathNodeMaker
import net.minecraft.entity.ai.pathing.PathNodeType
import net.minecraft.entity.mob.PathAwareEntity
import net.minecraft.entity.passive.GolemEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.WorldView
import kotlin.math.abs

open class FollowSummonerGoal(
    private val summoned: PathAwareEntity,
    private val summoner: LivingEntity,
    private val speed: Double,
    private val minDistance: Float,
    private val maxDistance: Float,
    private val leavesAllowed: Boolean):
    Goal() {

    private val teleportDistance = 12.0
    private val world: WorldView
    private val navigation: EntityNavigation
    private var oldWaterPathfindingPenalty = 0f
    private var updateCountdownTicks = 0

    init{
        world = summoned.world
        navigation = summoned.navigation
    }


    override fun canStart(): Boolean {
        if (summoner.isSpectator) return false
        if (summoned.squaredDistanceTo(summoner) < (minDistance * minDistance).toDouble()){
            return false
        }
        return true
    }

    override fun shouldContinue(): Boolean {
        if (this.navigation.isIdle) {
            return false
        }
        return summoned.squaredDistanceTo(summoner) > (maxDistance * maxDistance).toDouble()
    }

    override fun start() {
        this.updateCountdownTicks = 0
        this.oldWaterPathfindingPenalty = summoned.getPathfindingPenalty(PathNodeType.WATER)
         summoned.setPathfindingPenalty(PathNodeType.WATER, 0.0f)
    }

    override fun stop() {
        navigation.stop()
        summoned.setPathfindingPenalty(PathNodeType.WATER, this.oldWaterPathfindingPenalty)
    }

    override fun tick() {
        summoned.lookControl.lookAt(summoner, 10.0f, summoned.maxLookPitchChange.toFloat())
        if (--updateCountdownTicks > 0) {
            return
        }
        updateCountdownTicks = getTickCount(10)
        if (summoned.isLeashed || summoned.hasVehicle()) {
            return
        }
        if (summoned.squaredDistanceTo(summoner) >= (teleportDistance * teleportDistance)) {
            this.tryTeleport()
        } else {
            navigation.startMovingTo(summoner, speed)
        }
    }

    private fun tryTeleport() {
        val blockPos: BlockPos = summoned.blockPos
        for (i in 0..9) {
            val j: Int = this.getRandomInt(-3, 3)
            val k: Int = this.getRandomInt(-1, 1)
            val l: Int = this.getRandomInt(-3, 3)
            val bl: Boolean = this.tryTeleportTo(blockPos.x + j, blockPos.y + k, blockPos.z + l)
            if (!bl) continue
            return
        }
    }

    private fun tryTeleportTo(x: Int, y: Int, z: Int): Boolean {
        if (abs(x.toDouble() - summoned.x) < 2.0 && abs(z.toDouble() - summoned.z) < 2.0) {
            return false
        }
        if (!this.canTeleportTo(BlockPos(x, y, z))) {
            return false
        }
        summoned.refreshPositionAndAngles(
            x.toDouble() + 0.5,
            y.toDouble(),
            z.toDouble() + 0.5,
            summoned.yaw,
            summoned.pitch
        )
        navigation.stop()
        return true
    }

    private fun canTeleportTo(pos: BlockPos): Boolean {
        val pathNodeType = LandPathNodeMaker.getLandNodeType(world, pos.mutableCopy())
        if (pathNodeType != PathNodeType.WALKABLE) {
            return false
        }
        val blockState = world.getBlockState(pos.down())
        if (!leavesAllowed && blockState.block is LeavesBlock) {
            return false
        }
        val blockPos = pos.subtract(summoned.blockPos)
        return world.isSpaceEmpty(summoned, summoned.boundingBox.offset(blockPos))
    }

    private fun getRandomInt(min: Int, max: Int): Int {
        return summoned.random.nextInt(max - min + 1) + min
    }
}