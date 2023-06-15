package me.fzzyhmstrs.amethyst_imbuement.augment

import dev.emi.trinkets.api.TrinketEnums
import dev.emi.trinkets.api.event.TrinketDropCallback
import me.fzzyhmstrs.amethyst_imbuement.augment.base_augments.TotemPassiveAugment
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterStatus
import me.fzzyhmstrs.fzzy_core.coding_util.AcText
import me.fzzyhmstrs.fzzy_core.trinket_util.EffectQueue
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemStack

class SoulbindingAugment(weight: Rarity, mxLvl: Int = 2, vararg slot: EquipmentSlot): TotemPassiveAugment(weight,mxLvl, *slot) {

    override fun tickEffect(user: LivingEntity, level: Int, stack: ItemStack) {
        val lvl = EnchantmentHelper.getLevel(RegisterEnchantment.CONSECRATED,stack)
        if (lvl == 0) return
        EffectQueue.addStatusToQueue(user, RegisterStatus.BLESSED, 260, lvl - 1)
    }

}
