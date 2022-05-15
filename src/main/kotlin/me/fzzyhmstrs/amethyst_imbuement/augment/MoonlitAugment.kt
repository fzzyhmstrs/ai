package me.fzzyhmstrs.amethyst_imbuement.augment

import me.fzzyhmstrs.amethyst_imbuement.augment.base_augments.PassiveAugment
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.item.ItemStack
import kotlin.math.abs

class MoonlitAugment(weight: Rarity, mxLvl: Int = 1, vararg slot: EquipmentSlot): PassiveAugment(weight,mxLvl, *slot) {

    override fun tickEffect(user: LivingEntity, level: Int, stack: ItemStack) {
        val world = user.world
        if (world.isNight){
            val lvl = EnchantmentHelper.getLevel(RegisterEnchantment.MOONLIT,stack)
            val tod = world.timeOfDay%24000
            val comp1 = abs(tod - 13000L)
            val comp2 = abs(tod - 23000L)
            val comp3 = abs(tod - 18000L)
            if((comp3 < comp1) && (comp3 < comp2)){
                addStatusToQueue(user,StatusEffects.STRENGTH, 400, lvl)
                addStatusToQueue(user,StatusEffects.RESISTANCE, 400, lvl-1)
            } else {
                addStatusToQueue(user,StatusEffects.STRENGTH, 400, lvl-1)
            }
        }
    }

}