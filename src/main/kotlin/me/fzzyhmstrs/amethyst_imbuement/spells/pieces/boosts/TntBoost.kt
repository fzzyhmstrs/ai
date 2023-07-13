package me.fzzyhmstrs.amethyst_imbuement.spells.pieces.boosts

import me.fzzyhmstrs.amethyst_core.augments.paired.ExplosionBuilder
import me.fzzyhmstrs.amethyst_core.augments.paired.PairedAugments
import me.fzzyhmstrs.amethyst_core.augments.paired.ProcessContext
import me.fzzyhmstrs.amethyst_core.boost.ItemAugmentBoost
import me.fzzyhmstrs.amethyst_core.interfaces.SpellCastingEntity
import me.fzzyhmstrs.amethyst_imbuement.AI
import net.minecraft.block.Blocks
import net.minecraft.entity.LivingEntity
import net.minecraft.util.Hand
import net.minecraft.world.World

class TntBoost: ItemAugmentBoost(AI.identity("tnt_boost"), Blocks.TNT.asItem()) {

    override fun <T> modifyExplosion(
        builder: ExplosionBuilder,
        context: ProcessContext,
        user: T,
        world: World,
        hand: Hand,
        spells: PairedAugments
    )
    :
    ExplosionBuilder
    where
    T : SpellCastingEntity,
    T : LivingEntity
    {
        return builder.modifyPower {power -> power * 1.25f}
    }


}