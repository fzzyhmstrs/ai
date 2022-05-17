package me.fzzyhmstrs.amethyst_imbuement.scepter.base_augments

import me.fzzyhmstrs.amethyst_imbuement.scepter.ScepterObject
import me.fzzyhmstrs.amethyst_imbuement.util.SpellType
import net.minecraft.util.Identifier

@Suppress("MemberVisibilityCanBePrivate", "CanBeParameter")
open class AugmentModifier(
    val modifierId: Identifier,
    val levelModifier: Int = 0,
    val cooldownModifier: Int = 0,
    val manaCostModifier: Int = 0,
    val damageModifier: Float = 0.0F,
    val damagePerLevelModifier: Float = 0.0F,
    val amplifierModifier: Int = 0,
    val amplifierPerLevelModifier: Int = 0,
    val durationModifier: Int = 0,
    val durationPerLevelModifier: Int = 0,
    val rangeModifier: Double = 0.0,
    val rangePerLevelModifier: Double = 0.0,
    val xpModifier: XpModifiers? = null,
    val modifierLifespan: Int = 0,
    val secondaryEffect: ScepterAugment? = null,
    val spellsToAffect: List<Identifier>? = null
    ) {

    private val hasSpell: Boolean = spellsToAffect != null
    private val hasEffect: Boolean = secondaryEffect != null
    private val hasLife: Boolean = modifierLifespan != 0
    private val effectModifier = ScepterObject.AugmentEffect(
        damageModifier,
        damagePerLevelModifier,
        amplifierModifier,
        amplifierPerLevelModifier,
        durationModifier,
        durationPerLevelModifier,
        rangeModifier,
        rangePerLevelModifier)

    fun hasSpellToAffect(): Boolean{
        return hasSpell
    }

    fun hasSecondaryEffect(): Boolean{
        return hasEffect
    }

    fun hasLifespan(): Boolean{
        return hasLife
    }

    fun getEffectModifier(): ScepterObject.AugmentEffect{
        return effectModifier
    }

    //data class SecondaryEffect(val overridesEffect: Boolean = false, val secondaryEffect: ScepterAugment? = null)
    
    data class XpModifiers(var furyXpMod: Int = 0, var witXpMod: Int = 0, var graceXpMod: Int = 0){
        fun plus(xpMods: XpModifiers?){
            if(xpMods == null) return
            this.furyXpMod += xpMods.furyXpMod
            this.witXpMod += xpMods.witXpMod
            this.graceXpMod += xpMods.graceXpMod
        }
        fun getMod(spellKey: String): Int{
            return when(spellKey){
                SpellType.FURY.name ->{this.furyXpMod}
                SpellType.WIT.name ->{this.witXpMod}
                SpellType.GRACE.name ->{this.graceXpMod}
                else -> 0
            }
        }
    }
}
