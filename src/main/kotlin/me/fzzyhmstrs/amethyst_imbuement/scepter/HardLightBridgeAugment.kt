package me.fzzyhmstrs.amethyst_imbuement.scepter

import me.fzzyhmstrs.amethyst_imbuement.item.ImbuedJewelryItem
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterBlock
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import me.fzzyhmstrs.amethyst_imbuement.scepter.base_augments.MiscAugment
import net.minecraft.enchantment.Enchantment
import net.minecraft.enchantment.EnchantmentTarget
import net.minecraft.entity.Entity
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemStack
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World

class HardLightBridgeAugment(weight: Rarity, tier: Int, maxLvl: Int, vararg slot: EquipmentSlot): MiscAugment(weight,tier,maxLvl, *slot) {
    override fun effect(world: World, target: Entity?, user: LivingEntity, level: Int, hit: HitResult?): Boolean {
        var successes = 0
        val dir = user.horizontalFacing
        val pos = user.blockPos.add(0,-1,0)
        var xMod = 0
        var zMod = 0
        when (dir){
            Direction.WEST->{
                xMod = -1
            }
            Direction.EAST->{
                xMod = 1
            }
            Direction.NORTH->{
                zMod = -1
            }
            Direction.SOUTH->{
                zMod = 1
            }
            else->{return false}
        }
        for (i in 1..rangeOfEffect().toInt()){
            val pos2 = pos.add(i * xMod,0,i * zMod)
            if (world.isAir(pos2)){
                world.setBlockState(pos2,RegisterBlock.HARD_LIGHT_BLOCK.defaultState)
                successes++
            }
        }
        if (successes > 0) world.playSound(null, user.blockPos, soundEvent(), SoundCategory.PLAYERS, 1.0F, 1.0F)
        return successes > 0
    }

    override fun rangeOfEffect(): Double {
        return 8.0
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.BLOCK_GLASS_PLACE
    }
}