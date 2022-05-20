package me.fzzyhmstrs.amethyst_imbuement.scepter.base_augments

import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.item.ScepterItem
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterModifier
import me.fzzyhmstrs.amethyst_imbuement.scepter.ScepterObject
import me.fzzyhmstrs.amethyst_imbuement.tool.ScepterLvl2ToolMaterial
import me.fzzyhmstrs.amethyst_imbuement.tool.ScepterLvl3ToolMaterial
import me.fzzyhmstrs.amethyst_imbuement.util.AcceptableItemStacks
import me.fzzyhmstrs.amethyst_imbuement.util.SpellType
import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier
import kotlin.math.max

@Suppress("MemberVisibilityCanBePrivate", "CanBeParameter")
    //alternative version with the AugmentEffect directly included

object AugmentModifierDefaults{
    val blankId = Identifier(AI.MOD_ID,"blank_modifier")
    val EMPTY = AugmentModifier(blankId)
    val EMPTY_COMPILED = CompiledAugmentModifier(blankId)
}


open class AugmentModifier(
    val modifierId: Identifier,
    open val levelModifier: Int = 0,
    open val cooldownModifier: Double = 0.0,
    open val manaCostModifier: Double = 0.0
    ) {

    protected val effects: AugmentEffect = AugmentEffect()
    protected val xpModifier: XpModifiers = XpModifiers()
    private val spellsToAffect: MutableList<Identifier> = mutableListOf()
    private var secondaryEffect: ScepterAugment? = null
    private var descendant: Identifier = AugmentModifierDefaults.blankId
    private var lineage: MutableList<Identifier> = mutableListOf(modifierId)
        
    private var hasSpell: Boolean = false
    private var hasSecondEffect: Boolean = false
    private var hasDesc: Boolean = false

    fun hasSpellToAffect(): Boolean{
        return hasSpell
    }
    fun getSpellsToAffect(): MutableList<Identifier>{
        return spellsToAffect
    }
    fun hasSecondaryEffect(): Boolean{
        return hasSecondEffect
    }
    fun hasDescendant(): Boolean{
        return hasDesc
    }
    fun getDescendant(): Identifier{
        return descendant
    }
    fun getLineage(): List<Identifier>{
        if (!hasDesc) return lineage
        val nextInLineage = RegisterModifier.ENTRIES.get(descendant)
        lineage.addAll(nextInLineage?.getLineage() ?: listOf())
        return lineage
    }
    fun getSecondaryEffect(): ScepterAugment?{
        return secondaryEffect
    }

    fun getEffectModifier(): AugmentEffect{
        return effects
    }

    fun getXpModifiers(): XpModifiers{
        return xpModifier
    }
    
    fun withDamage(damage: Float = 0.0F, damagePerLevel: Float = 0.0F, damagePercent: Float = 0.0F): AugmentModifier{
        effects.plus(ScepterObject.BLANK_EFFECT.withDamage(damage, damagePerLevel, damagePercent))
        return this
    }
    fun withAmplifier(amplifier: Int = 0, amplifierPerLevel: Int = 0): AugmentModifier{
        effects.plus(ScepterObject.BLANK_EFFECT.withAmplifier(amplifier, amplifierPerLevel))
        return this
    }
    fun withDuration(duration: Int = 0, durationPerLevel: Int = 0, durationPercent: Int = 0): AugmentModifier{
        effects.plus(ScepterObject.BLANK_EFFECT.withDuration(duration, durationPerLevel, durationPercent))
        return this
    }
    fun withRange(range: Double = 0.0, rangePerLevel: Double = 0.0, rangePercent: Double = 0.0): AugmentModifier{
        effects.plus(ScepterObject.BLANK_EFFECT.withRange(range, rangePerLevel, rangePercent))
        return this
    }
    fun withEffect(effect: AugmentEffect): AugmentModifier{
        effects.plus(effect)
        return this
    }
    fun withXpMod(type: SpellType, xpMod: Int): AugmentModifier{
        val xpMods = when(type){
                SpellType.FURY ->{ScepterObject.BLANK_XP_MOD.withFuryMod(xpMod)}
                SpellType.WIT ->{ScepterObject.BLANK_XP_MOD.withWitMod(xpMod)}
                SpellType.GRACE ->{ScepterObject.BLANK_XP_MOD.withGraceMod(xpMod)}
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
        hasSecondEffect = true
        return this
    }
    fun withDescendant(modifier: AugmentModifier): AugmentModifier{
        val id = modifier.modifierId
        descendant = id
        hasDesc = true
        return this
    }

    open fun isAcceptableItem(stack: ItemStack): Boolean{
        acceptableItemStacks().forEach {
            if (stack.isOf(it.item)){
                return true
            }
        }
        return false
    }
    open fun acceptableItemStacks(): MutableList<ItemStack>{
        return AcceptableItemStacks.scepterAcceptableItemStacks(1)
    }

    //data class SecondaryEffect(val overridesEffect: Boolean = false, val secondaryEffect: ScepterAugment? = null)
}

class CompiledAugmentModifier(
    modifierId: Identifier = AugmentModifierDefaults.blankId,
    override var levelModifier: Int = 0,
    override var cooldownModifier: Double = 0.0,
    override var manaCostModifier: Double = 0.0)
    : AugmentModifier(modifierId, levelModifier, cooldownModifier, manaCostModifier){

          fun plus(am: AugmentModifier) {
              levelModifier += am.levelModifier
              cooldownModifier += am.cooldownModifier
              manaCostModifier += am.manaCostModifier
              xpModifier.plus(am.getXpModifiers())
              effects.plus(am.getEffectModifier())
          }
    }

data class AugmentEffect(
    private var damageData: PerLvlF = PerLvlF(),
    private var amplifierData: PerLvlI = PerLvlI(),
    private var durationData: PerLvlI = PerLvlI(),
    private var rangeData: PerLvlD = PerLvlD()){

    fun plus(ae: AugmentEffect){
        damageData = damageData.plus(ae.damageData)
        amplifierData = amplifierData.plus(ae.amplifierData)
        durationData = durationData.plus(ae.durationData)
        rangeData = rangeData.plus(ae.rangeData)
    }
    fun damage(level: Int): Float{
        return max(0.0F, damageData.value(level))
    }
    fun amplifier(level: Int): Int{
        return max(0,amplifierData.value(level))
    }
    fun duration(level: Int): Int{
        return max(0,durationData.value(level))
    }
    fun range(level: Int): Double{
        return max(1.0,rangeData.value(level))
    }
    fun withDamage(damage: Float = 0.0F, damagePerLevel: Float = 0.0F, damagePercent: Float = 0.0F): AugmentEffect{
        return this.copy(damageData = PerLvlF(damage, damagePerLevel, damagePercent))
    }
    fun withAmplifier(amplifier: Int = 0, amplifierPerLevel: Int = 0, amplifierPercent: Int = 0): AugmentEffect{
        return this.copy(amplifierData = PerLvlI(amplifier,amplifierPerLevel,amplifierPercent))
    }
    fun withDuration(duration: Int = 0, durationPerLevel: Int = 0, durationPercent: Int = 0): AugmentEffect{
        return this.copy(durationData = PerLvlI(duration, durationPerLevel, durationPercent))
    }
    fun withRange(range: Double = 0.0, rangePerLevel: Double = 0.0, rangePercent: Double = 0.0): AugmentEffect {
        return this.copy(rangeData = PerLvlD(range, rangePerLevel, rangePercent))
    }
}

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

data class PerLvlI(val base: Int = 0, val perLevel: Int = 0, val percent: Int = 0){
    fun value(level: Int): Int{
        return (base + perLevel * level) * (100 + percent) / 100
    }
    fun plus(ldi: PerLvlI): PerLvlI{
        return PerLvlI(base + ldi.base, perLevel + ldi.perLevel, percent + ldi.percent)
    }
}

data class PerLvlF(val base: Float = 0.0F, val perLevel: Float = 0.0F, val percent: Float = 0.0F){
    fun value(level: Int): Float{
        return (base + perLevel * level) * (100 + percent) / 100
    }
    fun plus(ldf: PerLvlF): PerLvlF{
        return PerLvlF(base + ldf.base, perLevel + ldf.perLevel, percent + ldf.percent)
    }
}

data class PerLvlD(val base: Double = 0.0, val perLevel: Double = 0.0, val percent: Double = 0.0){
    fun value(level: Int): Double{
        return (base + perLevel * level) * (100 + percent) / 100
    }
    fun plus(ldd: PerLvlD): PerLvlD{
        return PerLvlD(base + ldd.base, perLevel + ldd.perLevel, percent + ldd.percent)
    }
}