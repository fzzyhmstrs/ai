package me.fzzyhmstrs.amethyst_imbuement.spells.pieces

import me.fzzyhmstrs.amethyst_core.augments.paired.PairedAugments
import me.fzzyhmstrs.amethyst_core.augments.paired.ProcessContext
import me.fzzyhmstrs.amethyst_core.interfaces.SpellCastingEntity
import me.fzzyhmstrs.amethyst_core.modifier.AugmentEffect
import me.fzzyhmstrs.fzzy_core.coding_util.PersistentEffectHelper
import net.minecraft.entity.LivingEntity
import net.minecraft.util.Hand
import net.minecraft.world.World

class ApplyTaskAugmentData<T>(
    val world: World,
    val context: ProcessContext,
    val user: T,
    val hand: Hand,
    val level: Int,
    val effects: AugmentEffect,
    val spells: PairedAugments
): PersistentEffectHelper.PersistentEffectData where T : SpellCastingEntity, T : LivingEntity