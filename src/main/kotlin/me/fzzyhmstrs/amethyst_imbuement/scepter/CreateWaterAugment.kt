package me.fzzyhmstrs.amethyst_imbuement.scepter

import me.fzzyhmstrs.amethyst_imbuement.scepter.base_augments.OceanicAugment
import me.fzzyhmstrs.amethyst_imbuement.scepter.base_augments.PlaceItemAugment
import me.fzzyhmstrs.amethyst_imbuement.scepter.base_augments.ScepterObject
import me.fzzyhmstrs.amethyst_imbuement.util.LoreTier
import me.fzzyhmstrs.amethyst_imbuement.util.SpellType
import net.minecraft.entity.EquipmentSlot
import net.minecraft.item.Item
import net.minecraft.item.Items
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents

class CreateWaterAugment(tier: Int, maxLvl: Int, item: Item, vararg slot: EquipmentSlot): PlaceItemAugment(tier, maxLvl, item, *slot), OceanicAugment {

    override fun augmentStat(imbueLevel: Int): ScepterObject.AugmentDatapoint {
        return ScepterObject.AugmentDatapoint(SpellType.WIT,30,8,1,imbueLevel,LoreTier.NO_TIER, Items.WATER_BUCKET)
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.ITEM_BUCKET_EMPTY
    }

}