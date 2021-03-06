package me.fzzyhmstrs.amethyst_imbuement.augment

import me.fzzyhmstrs.amethyst_imbuement.augment.base_augments.TotemPassiveAugment
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.text.Text

class UndyingAugment(weight: Rarity, mxLvl: Int = 1, vararg slot: EquipmentSlot): TotemPassiveAugment(weight,mxLvl, *slot) {

    override fun specialEffect(user: LivingEntity, level: Int, stack: ItemStack): Boolean {
        if (user !is PlayerEntity) return false
        if (RegisterItem.TOTEM_OF_AMETHYST.manaDamage(stack, user.world, user, 360)) {
            RegisterItem.TOTEM_OF_AMETHYST.burnOutHandler(stack, RegisterEnchantment.UNDYING,user, Text.literal("Undying augment burnt out!"))
        }
        return true
    }

}