package me.fzzyhmstrs.amethyst_imbuement.scepter

import me.fzzyhmstrs.amethyst_imbuement.scepter.base_augments.MiscAugment
import net.minecraft.block.CropBlock
import net.minecraft.entity.Entity
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.hit.HitResult
import net.minecraft.world.Heightmap
import net.minecraft.world.World

class AbundanceAugment(weight: Rarity, tier: Int, maxLvl: Int, vararg slot: EquipmentSlot): MiscAugment(weight,tier,maxLvl, *slot) {
    override fun effect(world: World, target: Entity?, user: LivingEntity, level: Int, hit: HitResult?): Boolean {
        var successes = 0
        val range = (rangeOfEffect() + level-1).toInt()
        val userPos = user.blockPos
        for (i in -range..range){
            for (j in -range..range){
                val bs = world.getBlockState(userPos.add(i,0,j))
                val bsb = bs.block
                if (bsb is CropBlock){
                    val rnd1 = world.random.nextDouble()
                    if (rnd1 < 0.25 + 0.05 * level) {
                        successes++
                        if (bsb.isMature(bs)) {
                            world.breakBlock(userPos.add(i, 0, j), true)
                            world.setBlockState(userPos.add(i, 0, j),bsb.defaultState)
                            continue
                        }
                        bsb.grow(world as ServerWorld,world.random,userPos.add(i,0,j),bs)
                    }
                }
            }
        }
        return successes > 0
    }

    override fun rangeOfEffect(): Double {
        return 2.0
    }

}