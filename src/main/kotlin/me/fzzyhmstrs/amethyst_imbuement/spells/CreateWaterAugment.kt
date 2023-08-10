package me.fzzyhmstrs.amethyst_imbuement.spells

import me.fzzyhmstrs.amethyst_core.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_core.augments.base.PlaceItemAugment
import me.fzzyhmstrs.amethyst_core.augments.data.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.augments.paired.AugmentType
import me.fzzyhmstrs.amethyst_core.augments.paired.PairedAugments
import me.fzzyhmstrs.amethyst_core.scepter.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter.SpellType
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellAdvancementChecks
import net.minecraft.item.Item
import net.minecraft.item.Items
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text

class CreateWaterAugment: PlaceItemAugment(ScepterTier.ONE,Items.WATER_BUCKET){

    override val augmentData: AugmentDatapoint =
        AugmentDatapoint(AI.identity("create_water"),SpellType.WIT, 30, 8,
            1, 1,1,1, LoreTier.NO_TIER, Items.WATER_BUCKET)

    override fun augmentStat(imbueLevel: Int): AugmentDatapoint {
        return 
    }

    override fun appendDescription(description: MutableList<Text>, other: ScepterAugment, othersType: AugmentType) {
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
        return itemAfterWaterTransform(startItem)
    }

    override fun hitSoundEvent(world: World, blockPos: BlockPos, context: ProcessContext) {
        world.playSound(null,blockPos,SoundEvents.ITEM_BUCKET_EMPTY,SoundCategory.PLAYERS,1.0f,1.0f)
    }

    private fun itemAfterWaterTransform(item: Item): Item {
        return items[item]?:item
    }

    companion object{
        val items: Map<Item, Item> = mapOf(
            Items.SPONGE to Items.MUD,
            Items.LAVA_BUCKET to Items.STONE,
            RegisterBlock.HARD_LIGHT_BLOCK.asItem() to Items.GLASS,
            RegisterBlock.SHINE_LIGHT.asItem() to Items.SEA_LANTERN
        )
    }
}
