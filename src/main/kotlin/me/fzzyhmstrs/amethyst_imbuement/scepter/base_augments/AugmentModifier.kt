package me.fzzyhmstrs.amethyst_imbuement.scepter.base_augments

import net.minecraft.util.Identifier

@Suppress("MemberVisibilityCanBePrivate", "CanBeParameter")
open class AugmentModifier(
    val modifierId: Identifier,
    val damageModifier: Float = 0.0F,
    val amplifierModifier: Int = 0,
    val durationModifier: Int = 0,
    val cooldownModifier: Int = 0,
    val manaCostModifier: Int = 0,
    val rangeModifier: Double = 0.0,
    val levelModifier: Int = 0,
    val modifierLifespan: Int = 0,
    val secondaryEffect: SecondaryEffect = SecondaryEffect(),
    val spellsToAffect: List<Identifier>? = null,
) {

    private val hasSpell: Boolean = spellsToAffect != null
    private val hasEffect: Boolean = secondaryEffect.secondaryEffect != null
    private val hasLife: Boolean = modifierLifespan != 0

    fun hasSpellToAffect(): Boolean{
        return hasSpell
    }

    fun hasSecondaryEffect(): Boolean{
        return hasEffect
    }

    fun hasLifespan(): Boolean{
        return hasLife
    }

    data class SecondaryEffect(val overridesEffect: Boolean = false, val secondaryEffect: ScepterAugment? = null){}

}