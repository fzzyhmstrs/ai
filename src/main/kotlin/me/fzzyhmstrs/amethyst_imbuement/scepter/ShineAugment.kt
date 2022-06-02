package me.fzzyhmstrs.amethyst_imbuement.scepter

import me.fzzyhmstrs.amethyst_imbuement.scepter.base_augments.AugmentEffect
import me.fzzyhmstrs.amethyst_imbuement.scepter.base_augments.PlaceItemAugment
import me.fzzyhmstrs.amethyst_imbuement.util.LoreTier
import me.fzzyhmstrs.amethyst_imbuement.util.RaycasterUtil
import me.fzzyhmstrs.amethyst_imbuement.util.SpellType
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.item.Item
import net.minecraft.item.Items
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.Hand
import net.minecraft.world.World

class ShineAugment(tier: Int, maxLvl: Int, item: Item, vararg slot: EquipmentSlot): PlaceItemAugment(tier, maxLvl, item, *slot) {

    override fun applyTasks(world: World, user: LivingEntity, hand: Hand, level: Int, effects: AugmentEffect): Boolean {
        return RaycasterUtil.raycastBlock(entity = user) != null
    }

    override fun augmentStat(imbueLevel: Int): ScepterObject.AugmentDatapoint {
        return ScepterObject.AugmentDatapoint(SpellType.WIT,5,2,1,imbueLevel,LoreTier.NO_TIER, Items.TORCH)
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.BLOCK_WOOD_PLACE
    }
}