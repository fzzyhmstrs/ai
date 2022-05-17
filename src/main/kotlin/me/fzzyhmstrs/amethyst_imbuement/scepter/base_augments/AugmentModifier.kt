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
    val damagePercentModifier: Float = 0.0F,
    val amplifierModifier: Int = 0,
    val amplifierPerLevelModifier: Int = 0,
    val durationModifier: Int = 0,
    val durationPerLevelModifier: Int = 0,
    val durationPercentModifier: Int = 0,
    val rangeModifier: Double = 0.0,
    val rangePerLevelModifier: Double = 0.0,
    val rangePercentModifier: Double = 0.0,
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
        damagePercentModifier,
        amplifierModifier,
        amplifierPerLevelModifier,
        durationModifier,
        durationPerLevelModifier,
        durationPercentModifier,
        rangeModifier,
        rangePerLevelModifier,
        rangePercentModifier)

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
    
    //alternative version with the AugmentEffect directly included
    open class AugmentModifier(
    val modifierId: Identifier,
    val levelModifier: Int = 0,
    val cooldownModifier: Int = 0,
    val manaCostModifier: Int = 0,
    val modifierLifespan: Int = 0,
    ) {

    private val effects: ScepterObject.AugmentEffect = ScepterObject.AugmentEffect
    private val xpModifier: XpModifiers? = null
    private val spellsToAffect: MutableList<Identifier> = mutableListOf()
    private val secondaryEffect: ScepterAugment? = null
        
    private var hasSpell: Boolean = false
    private var hasEffect: Boolean = false
    private var hasLife: Boolean = modifierLifespan != 0

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
        return effects
    }
    
    fun withDamage(damage: Float = 0.0F, damagePerLevel: Float = 0.0F, damagePercent: Float = 0.0F): AugmentModifier{
        efects.plus(ScepterObject.BLANK_EFFECT.withDamage(damage, damagePerLevel, damagePercent))
        return this
    }
    fun withAmplifier(amplifier: Int = 0, amplifierPerLevel: Int = 0): AugmentModifier{
        effects.plus(ScepterObject.BLANK_EFFECT.withDamage(amplifier, amplifierPerLevel))
        return this
    }
    fun withDuration(duration: Int = 0, durationPerLevel: Int = 0, durationPercent: Int = 0): AugmentModifier{
        effects.plus(ScepterObject.BLANK_EFFECT.withDamage(duration, durationPerLevel, durationPercent))
        return this
    }
    fun withRange(range: Double = 0.0, rangePerLevel: Double = 0.0, rangePercent: Double = 0.0): AugmentModifier{
        effects.plus(ScepterObject.BLANK_EFFECT.withDamage(range, rangePerLevel, rangePercent))
        return this
    }
    fun withXpMod(type: SpellType, xpMod: Int): AugmentModifier{
        val xpMods = when(type){
                SpellType.FURY ->{XpModifier().withFuryMod(xpMod)}
                SpellType.WIT ->{XpModifier().withWitMod(xpMod)}
                SpellType.GRACE ->{XpModifier().withGraceMod(xpMod)}
                else -> return this
            }
        xpModifier.plus(xpMods)
        return this
    }
    fun withSpellToAffect(id: Identifier): AugmentModifier{
        spellsToAffect.add(id)
        hasSpell = true
        return this
    }
    fun withSecondaryEffect(augment: ScepterAugment): AugmentModifier{
        secondaryEffect = augment
        hasEffect = true
        return this
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
        fun withFuryMod(furyXpMod: Int = 0): XpModifiers{
            return this.copy(furyXpMod = furyXpMod)
        }
        fun withWitMod(witXpMod: Int = 0): XpModifiers{
            return this.copy(witXpMod = witXpMod)
        }
        fun withGraceMod(graceXpMod: Int = 0): XpModifiers{
            return this.copy(graceXpMod = graceXpMod)
        }
    }
}
