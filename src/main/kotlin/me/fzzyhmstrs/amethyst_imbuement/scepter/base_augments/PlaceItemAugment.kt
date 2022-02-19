package me.fzzyhmstrs.amethyst_imbuement.scepter.base_augments

import net.minecraft.enchantment.EnchantmentTarget
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.world.World

open class PlaceItemAugment(weight: Rarity, tier: Int, maxLvl: Int,item: Item, vararg slot: EquipmentSlot): ScepterAugment(weight,tier,maxLvl, EnchantmentTarget.WEAPON, *slot) {
    private val _item = item

    open fun itemToPlace(world: World, user: PlayerEntity): ItemStack {
        /*val nbt = NbtCompound()
        nbt.putString("level","14")
        stack.setSubNbt("BlockStateTag",nbt)*/
        return ItemStack(_item)
    }

    open fun soundEvent(): SoundEvent{
        return SoundEvents.BLOCK_WOOD_PLACE
    }

}