package me.fzzyhmstrs.amethyst_imbuement.entity.goal

import me.fzzyhmstrs.amethyst_imbuement.entity.living.FloralConstructEntity
import net.minecraft.block.CropBlock
import net.minecraft.entity.ai.NoPenaltyTargeting
import net.minecraft.entity.ai.goal.WanderAroundGoal
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d

class FloralConstructWanderGoal(mob: FloralConstructEntity, speed: Double) : WanderAroundGoal(mob, speed,300,true) {

    override fun getWanderTarget(): Vec3d? {
        val targets: MutableList<BlockPos> = mutableListOf()
        val startPos = mob.blockPos
        for (i in -8..8) {
            for (j in -8..8) {
                for (k in -2..2) {
                    val bp = startPos.add(i, k, j)
                    if (mob.world.getBlockState(bp).block is CropBlock)
                        targets.add(bp)
                }
            }
        }
        if (targets.isNotEmpty()){
            return NoPenaltyTargeting.findTo(mob,10,4,targets[mob.world.random.nextInt(targets.size)].toCenterPos(),1.5707963705062866)
        }
        return super.getWanderTarget()
    }


}