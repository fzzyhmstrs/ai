package me.fzzyhmstrs.amethyst_imbuement.spells.tales

import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter_util.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter_util.SpellType
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.PlaceItemAugment
import me.fzzyhmstrs.amethyst_imbuement.item.book.BookOfTalesItem
import net.minecraft.block.TntBlock
import net.minecraft.item.Items
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.world.World

class CreateTntAugment: PlaceItemAugment(ScepterTier.THREE, 1, Items.TNT){
    
    override fun augmentStat(imbueLevel: Int): AugmentDatapoint {
        return AugmentDatapoint(SpellType.WIT,100,50,
            20,imbueLevel,6, BookOfTalesItem.TALES_TIER, Items.TNT)
    }

    override fun blockPlacing(hit: BlockHitResult, world: World, user: ServerPlayerEntity, hand: Hand, level: Int, effects: AugmentEffect): Boolean{
        val bl = super.blockPlacing(hit,world,user,hand,level,effects)
        if (bl){
            TntBlock.primeTnt(world,hit.blockPos)
        }
        return bl
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.BLOCK_GRASS_PLACE
    }

}
