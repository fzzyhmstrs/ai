package me.fzzyhmstrs.amethyst_imbuement.augment

import me.fzzyhmstrs.amethyst_imbuement.augment.base_augments.TotemPassiveAugment
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterTool
import me.fzzyhmstrs.fzzy_core.coding_util.AcText
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack

class UndyingAugment(weight: Rarity, mxLvl: Int = 1, vararg slot: EquipmentSlot): TotemPassiveAugment(weight,mxLvl, *slot) {

    override fun specialEffect(user: LivingEntity, level: Int, stack: ItemStack): Boolean {
        if (user !is PlayerEntity || !checkEnabled()) return false
        if (!AiConfig.trinkets.enableBurnout.get()){
            if (!RegisterTool.TOTEM_OF_AMETHYST.checkCanUse(stack,user.world,user,360)) return false
        }
        if (RegisterTool.TOTEM_OF_AMETHYST.manaDamage(stack, user.world, user, 360)) {
            if (AiConfig.trinkets.enableBurnout.get())
                RegisterTool.TOTEM_OF_AMETHYST.burnOutHandler(stack, RegisterEnchantment.UNDYING,user, AcText.translatable("augment_damage.undying.burnout"))
        }
        return true
    }

}