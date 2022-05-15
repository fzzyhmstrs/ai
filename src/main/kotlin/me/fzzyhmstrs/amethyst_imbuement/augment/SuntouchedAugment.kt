package me.fzzyhmstrs.amethyst_imbuement.augment

import me.fzzyhmstrs.amethyst_imbuement.augment.base_augments.PassiveAugment
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.item.ItemStack
import kotlin.math.abs

class SuntouchedAugment(weight: Rarity, mxLvl: Int = 1, vararg slot: EquipmentSlot): PassiveAugment(weight,mxLvl, *slot) {

    override fun tickEffect(user: LivingEntity, level: Int, stack: ItemStack) {
        val world = user.world
        if (world.isDay){
            val tod = world.timeOfDay%24000
            val comp1 = abs(tod - 1000L)
            val comp2 = abs(tod - 11000L)
            val comp3 = abs(tod - 6000L)
            if((comp3 < comp1) && (comp3 < comp2)){
                addStatusToQueue(user, StatusEffects.STRENGTH, 400, level)
                addStatusToQueue(user, StatusEffects.SPEED, 400, level-1)
            } else {
                addStatusToQueue(user, StatusEffects.STRENGTH, 400, level-1)
            }
        }
    }
}