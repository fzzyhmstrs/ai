package me.fzzyhmstrs.amethyst_imbuement.scepter

import me.fzzyhmstrs.amethyst_imbuement.scepter.base_augments.MiscAugment
import me.fzzyhmstrs.amethyst_imbuement.util.SpellType
import net.minecraft.block.CropBlock
import net.minecraft.entity.Entity
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.item.Items
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.hit.HitResult
import net.minecraft.world.World

class AbundanceAugment(tier: Int, maxLvl: Int, vararg slot: EquipmentSlot): MiscAugment(tier,maxLvl, *slot) {
    override fun effect(world: World, target: Entity?, user: LivingEntity, level: Int, hit: HitResult?): Boolean {
        var successes = 0
        val range = (rangeOfEffect() + level-1).toInt()
        val userPos = user.blockPos
        for (i in -range..range){
            for (j in -range..range){
                for (k in -1..1){
                    val bs = world.getBlockState(userPos.add(i,k,j))
                    val bsb = bs.block
                        if (bsb is CropBlock){
                            val rnd1 = world.random.nextDouble()
                            if (rnd1 < 0.15 + 0.05 * level) {
                                successes++
                                if (bsb.isMature(bs)) {
                                    world.breakBlock(userPos.add(i, k, j), true)
                                    world.setBlockState(userPos.add(i, k, j),bsb.defaultState)
                                    continue
                                }
                                bsb.grow(world as ServerWorld,world.random,userPos.add(i,k,j),bs)
                            }
                        }
                    }
            }
        }
        return successes > 0
    }

    override fun rangeOfEffect(): Double {
        return 2.0
    }

    override fun augmentStat(imbueLevel: Int): ScepterObject.AugmentDatapoint {
        return ScepterObject.AugmentDatapoint(SpellType.GRACE,10,2,1,imbueLevel,0, Items.HAY_BLOCK)
    }

}