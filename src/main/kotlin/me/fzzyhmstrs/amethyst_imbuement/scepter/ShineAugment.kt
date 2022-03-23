package me.fzzyhmstrs.amethyst_imbuement.scepter

import me.fzzyhmstrs.amethyst_imbuement.item.ImbuedJewelryItem
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import me.fzzyhmstrs.amethyst_imbuement.scepter.base_augments.PlaceItemAugment
import me.fzzyhmstrs.amethyst_imbuement.util.ScepterObject
import me.fzzyhmstrs.amethyst_imbuement.util.SpellType
import net.minecraft.enchantment.Enchantment
import net.minecraft.enchantment.EnchantmentTarget
import net.minecraft.entity.EquipmentSlot
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents

class ShineAugment(tier: Int, maxLvl: Int, item: Item, vararg slot: EquipmentSlot): PlaceItemAugment(tier, maxLvl, item, *slot) {
    override fun augmentStat(imbueLevel: Int): ScepterObject.AugmentDatapoint {
        return ScepterObject.AugmentDatapoint(SpellType.WIT,5,2,1,imbueLevel,0, Items.TORCH)
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.BLOCK_WOOD_PLACE
    }
}