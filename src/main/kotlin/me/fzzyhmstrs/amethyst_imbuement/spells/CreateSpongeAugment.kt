package me.fzzyhmstrs.amethyst_imbuement.spells

import me.fzzyhmstrs.amethyst_core.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_core.augments.base.PlaceItemAugment
import me.fzzyhmstrs.amethyst_core.augments.data.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.augments.paired.AugmentType
import me.fzzyhmstrs.amethyst_core.augments.paired.PairedAugments
import me.fzzyhmstrs.amethyst_core.augments.paired.ProcessContext
import me.fzzyhmstrs.amethyst_core.scepter.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter.SpellType
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellAdvancementChecks
import net.minecraft.item.Item
import net.minecraft.item.Items
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class CreateSpongeAugment: PlaceItemAugment(ScepterTier.ONE,Items.SPONGE){

    override val augmentData: AugmentDatapoint =
        AugmentDatapoint(
            AI.identity("create_sponge"),SpellType.WIT,30,8,
            5,1,1,1, LoreTier.LOW_TIER, Items.SPONGE)

    //1
    override fun augmentStat(imbueLevel: Int): AugmentDatapoint {
        return 
    }

    override fun appendDescription(description: MutableList<Text>, other: ScepterAugment, othersType: AugmentType) {
        TODO("Not yet implemented")
    }

    override fun onPaired(player: ServerPlayerEntity, pair: PairedAugments) {
        if (pair.spellsAreEqual()){
            SpellAdvancementChecks.grant(player, SpellAdvancementChecks.DOUBLE_TRIGGER)
        }
        if (pair.spellsAreUnique()){
            SpellAdvancementChecks.grant(player, SpellAdvancementChecks.UNIQUE_TRIGGER)
        }
    }

    override fun provideArgs(pairedSpell: ScepterAugment): Array<Text> {
        return arrayOf(pairedSpell.provideAdjective(this))
    }

    override fun castSoundEvent(world: World, blockPos: BlockPos, context: ProcessContext) {
        world.playSound(null, blockPos, SoundEvents.BLOCK_CONDUIT_AMBIENT_SHORT, SoundCategory.NEUTRAL, 1.0f, 1.0f)
    }

    private fun itemAfterSpongeTransform(item: Item): Item {
        return CreateHardLightAugment.items[item]?:item
    }

    companion object{
        val items: Map<Item, Item> = mapOf(
            Items.WATER_BUCKET to Items.WET_SPONGE,
            Items.LAVA_BUCKET to Items.BLACKSTONE
        )
    }
}
