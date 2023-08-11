package me.fzzyhmstrs.amethyst_imbuement.spells

import me.fzzyhmstrs.amethyst_core.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_core.augments.base.PlaceItemAugment
import me.fzzyhmstrs.amethyst_core.augments.data.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.augments.paired.AugmentType
import me.fzzyhmstrs.amethyst_core.augments.paired.PairedAugments
import me.fzzyhmstrs.amethyst_core.augments.paired.ProcessContext
import me.fzzyhmstrs.amethyst_core.interfaces.SpellCastingEntity
import me.fzzyhmstrs.amethyst_core.modifier.AugmentEffect
import me.fzzyhmstrs.amethyst_core.modifier.addLang
import me.fzzyhmstrs.amethyst_core.scepter.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter.SpellType
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterBlock
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellAdvancementChecks
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.item.Item
import net.minecraft.item.Items
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class CreateSpongeAugment: PlaceItemAugment(ScepterTier.ONE,Items.SPONGE){

    override val augmentData: AugmentDatapoint =
        AugmentDatapoint(
            AI.identity("create_sponge"),SpellType.WIT,30,8,
            5,1,1,1, LoreTier.LOW_TIER, Items.SPONGE)

    override fun appendDescription(description: MutableList<Text>, other: ScepterAugment, othersType: AugmentType) {
        when (other){
            is PlaceItemAugment -> {
                description.addLang("enchantment.amethyst_imbuement.create_sponge.desc.block", arrayOf(other.item(),itemAfterSpongeTransform(other.item())), SpellAdvancementChecks.BLOCK)
            }
        }
        description.addLang("amethyst_imbuement.todo")
    }

    override fun onPaired(player: ServerPlayerEntity, pair: PairedAugments) {
        SpellAdvancementChecks.uniqueOrDouble(player, pair)
    }

    override fun provideArgs(pairedSpell: ScepterAugment): Array<Text> {
        return arrayOf(pairedSpell.provideAdjective(this))
    }
    
    override fun <T> customItemPlaceOnBlockHit(
        startItem: Item,
        blockHitResult: BlockHitResult,
        context: ProcessContext,
        world: World,
        source: Entity?,
        user: T,
        hand: Hand,
        level: Int,
        effects: AugmentEffect,
        othersType: AugmentType,
        spells: PairedAugments
    ): Item where T : SpellCastingEntity, T : LivingEntity {
        return items[startItem] ?: startItem
    }

    override fun hitSoundEvent(world: World, blockPos: BlockPos, context: ProcessContext) {
        world.playSound(null,blockPos,SoundEvents.ITEM_BUCKET_EMPTY_LAVA,SoundCategory.PLAYERS,1.0f,1.0f)
    }

    private fun itemAfterSpongeTransform(item: Item): Item {
        return items[item]?:item
    }

    companion object{
        val items: Map<Item, Item> = mapOf(
            Items.WATER_BUCKET to Items.WET_SPONGE,
            Items.LAVA_BUCKET to Items.BLACKSTONE,
            RegisterBlock.HARD_LIGHT_BLOCK.asItem() to Items.GLASS,
            RegisterBlock.SHINE_LIGHT.asItem() to Items.LANTERN
        )
    }
}
