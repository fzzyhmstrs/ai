package me.fzzyhmstrs.amethyst_imbuement.augment

import me.fzzyhmstrs.amethyst_imbuement.augment.base_augments.EquipmentAugment
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags
import net.minecraft.enchantment.EnchantmentTarget
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries

class BulwarkAugment(weight: Rarity, mxLvl: Int = 1, vararg slot: EquipmentSlot): EquipmentAugment(weight, mxLvl,EnchantmentTarget.CROSSBOW,*slot) {

    override fun isAcceptableItem(stack: ItemStack): Boolean {
        return stack.isIn(ConventionalItemTags.SHIELDS)
    }

    override fun acceptableItemStacks(): MutableList<ItemStack> {
        return Registries.ITEM.iterateEntries(ConventionalItemTags.SHIELDS).map { it.value().defaultStack }.toMutableList()
    }

    override fun specialEffect(user: LivingEntity, level: Int, stack: ItemStack): Boolean {
        //user is the user
        if (user.world.isClient || !checkEnabled()) return false
        user.heal(1.0f)
        return true
    }

}