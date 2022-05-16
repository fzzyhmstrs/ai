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
    val xpModifier: XpModifiers? = null,
    val modifierLifespan: Int = 0,
    val secondaryEffect: SecondaryEffect = SecondaryEffect(),
    val spellsToAffect: List<Identifier>? = null,
    
) {

    private val hasSpell: Boolean = spellsToAffect != null
    private val hasEffect: Boolean = secondaryEffect.secondaryEffect != null
    private val hasLife: Boolean = modifierLifespan != 0
    private val hasXp = xpModifier != null

    fun hasSpellToAffect(): Boolean{
        return hasSpell
    }

    fun hasSecondaryEffect(): Boolean{
        return hasEffect
    }

    fun hasLifespan(): Boolean{
        return hasLife
    }
    
    fun hasXpMods(): Boolean{
        return hasXp
    }

    data class SecondaryEffect(val overridesEffect: Boolean = false, val secondaryEffect: ScepterAugment? = null){}
    
    data class XpModifiers(var furyXpMod: Int = 0, var witXpMod: Int = 0, var graceXpMod: Int = 0){
fun plus(xpMods: XpModifiers?){
if(xpMods == null) return
this.furyXpMod += xpMods.furyXpMod
this.witXpMod += xpMods.witXpMod
this.graceXpMod += xpMods.graceXpMod
}
}

}
