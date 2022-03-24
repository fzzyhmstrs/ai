package me.fzzyhmstrs.amethyst_imbuement.scepter

import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterBlock
import me.fzzyhmstrs.amethyst_imbuement.scepter.base_augments.MiscAugment
import me.fzzyhmstrs.amethyst_imbuement.util.ScepterObject
import me.fzzyhmstrs.amethyst_imbuement.util.SpellType
import net.minecraft.block.Blocks
import net.minecraft.block.PlantBlock
import net.minecraft.entity.Entity
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.item.Items
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.hit.HitResult
import net.minecraft.world.World

class ForcefieldAugment(tier: Int, maxLvl: Int, vararg slot: EquipmentSlot): MiscAugment(tier, maxLvl, *slot) {

    private val offset = intArrayOf(-2,2)

    override fun effect(world: World, target: Entity?, user: LivingEntity, level: Int, hit: HitResult?): Boolean {
        val pos = user.blockPos.add(0,1,0)
        var successes = 0
        for (i in offset){
            val posX = pos.add(i,0,0)
            val posY = pos.add(0,i,0)
            val posZ = pos.add(0,0,i)
            for (j in -1..1){
                for (k in -1..1){
                    val bsX = world.getBlockState(posX.add(0,j,k))
                    val bsY = world.getBlockState(posY.add(j,0,k))
                    val bsZ = world.getBlockState(posZ.add(j,k,0))
                    if (bsX.isAir || bsX.block is PlantBlock){
                        if (!bsX.isAir) world.breakBlock(posX.add(0,j,k),true)
                        //Block.replace(bsX,RegisterBlock.FORCEFIELD_BLOCK.defaultState,world,posX.add(0,j,k),Block.NOTIFY_ALL)
                        world.setBlockState(posX.add(0,j,k),RegisterBlock.FORCEFIELD_BLOCK.getWaterState(false))
                        successes++
                    } else if (bsX.isOf(Blocks.WATER)){
                        world.setBlockState(posX.add(0,j,k),RegisterBlock.FORCEFIELD_BLOCK.getWaterState(true))
                        successes++
                    }
                    if (bsY.isAir || bsY.block is PlantBlock ){
                        if (!bsY.isAir) world.breakBlock(posY.add(j,0,k),true)
                        //Block.replace(bsY,RegisterBlock.FORCEFIELD_BLOCK.defaultState,world,posY.add(j,0,k),Block.NOTIFY_ALL)
                        world.setBlockState(posY.add(j,0,k),RegisterBlock.FORCEFIELD_BLOCK.getWaterState(false))
                        successes++
                    } else if (bsY.isOf(Blocks.WATER)){
                        world.setBlockState(posY.add(j,0,k),RegisterBlock.FORCEFIELD_BLOCK.getWaterState(true))
                        successes++
                    }
                    if (bsZ.isAir || bsZ.block is PlantBlock || bsX.isOf(Blocks.WATER)){
                        if (!bsZ.isAir) world.breakBlock(posZ.add(j,k,0),true)
                        //Block.replace(bsZ,RegisterBlock.FORCEFIELD_BLOCK.defaultState,world,posZ.add(j,k,0),Block.NOTIFY_ALL)
                        world.setBlockState(posZ.add(j,k,0),RegisterBlock.FORCEFIELD_BLOCK.getWaterState(false))
                        successes++
                    } else if (bsZ.isOf(Blocks.WATER)){
                        world.setBlockState(posZ.add(j,k,0),RegisterBlock.FORCEFIELD_BLOCK.getWaterState(true))
                        successes++
                    }
                }
            }
        }
        if (successes > 0) {
            world.playSound(null,user.blockPos,soundEvent(),SoundCategory.NEUTRAL,1.0f,1.0f)
        }
        return successes > 0
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.BLOCK_BEACON_POWER_SELECT
    }

    override fun augmentStat(imbueLevel: Int): ScepterObject.AugmentDatapoint {
        return ScepterObject.AugmentDatapoint(SpellType.GRACE,600,60,6,imbueLevel,1, Items.SHIELD)
    }
}