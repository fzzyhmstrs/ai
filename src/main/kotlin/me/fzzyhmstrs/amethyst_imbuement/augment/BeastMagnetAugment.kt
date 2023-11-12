package me.fzzyhmstrs.amethyst_imbuement.augment
import me.fzzyhmstrs.amethyst_core.item_util.AbstractAugmentJewelryItem
import me.fzzyhmstrs.amethyst_imbuement.augment.base_augments.PassiveAugment
import net.minecraft.entity.EquipmentSlot
import net.minecraft.item.ItemStack

class BeastMagnetAugment(weight: Rarity, mxLvl: Int = 1, vararg slot: EquipmentSlot): PassiveAugment(weight,mxLvl,*slot) {

    override fun isAcceptableItem(stack: ItemStack): Boolean {
        return stack.item is AbstractAugmentJewelryItem
    }

}