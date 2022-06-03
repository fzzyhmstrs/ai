package me.fzzyhmstrs.amethyst_imbuement.registry

import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.scepter.base_augments.AugmentConsumer
import me.fzzyhmstrs.amethyst_imbuement.scepter.base_augments.AugmentModifier
import me.fzzyhmstrs.amethyst_imbuement.scepter.base_augments.AugmentModifierDefaults
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.text.LiteralText
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.Identifier

object RegisterModifier {

    val ENTRIES: ModifierRegistry = ModifierRegistry()

    private val DEBUG_NECROTIC_CONSUMER = AugmentConsumer({ list: List<LivingEntity> -> necroticConsumer(list)}, AugmentConsumer.Type.HARMFUL)
    private fun necroticConsumer(list: List<LivingEntity>){
        list.forEach {
            it.addStatusEffect(
                StatusEffectInstance(StatusEffects.WITHER,80)
            )
        }
    }
    private val DEBUG_HEALING_CONSUMER = AugmentConsumer({ list: List<LivingEntity> -> healingConsumer(list)}, AugmentConsumer.Type.BENEFICIAL)
    private fun healingConsumer(list: List<LivingEntity>){
        list.forEach {
            it.addStatusEffect(
                StatusEffectInstance(StatusEffects.REGENERATION,40)
            )
        }
    }

    val GREATER_ATTUNED = AugmentModifier(Identifier(AI.MOD_ID,"greater_attuned"), cooldownModifier = -22.5)
    val ATTUNED = AugmentModifier(Identifier(AI.MOD_ID,"attuned"), cooldownModifier = -15.0).withDescendant(GREATER_ATTUNED)
    val LESSER_ATTUNED = AugmentModifier(Identifier(AI.MOD_ID,"lesser_attuned"), cooldownModifier = -7.5).withDescendant(ATTUNED)
    val GREATER_THRIFTY = AugmentModifier(Identifier(AI.MOD_ID,"greater_thrifty"), manaCostModifier = -15.0)
    val THRIFTY = AugmentModifier(Identifier(AI.MOD_ID,"thrifty"), manaCostModifier = -10.0).withDescendant(GREATER_THRIFTY)
    val LESSER_THRIFTY = AugmentModifier(Identifier(AI.MOD_ID,"lesser_thrifty"), manaCostModifier = -5.0).withDescendant(THRIFTY)
    val MODIFIER_DEBUG = AugmentModifier(Identifier(AI.MOD_ID,"modifier_debug")).withDamage(2.0F,2.0F).withRange(2.75)
    val MODIFIER_DEBUG_2 = AugmentModifier(Identifier(AI.MOD_ID,"modifier_debug_2"), levelModifier = 1).withDuration(10, durationPercent = 15).withAmplifier(1)
    val MODIFIER_DEBUG_3 = AugmentModifier(Identifier(AI.MOD_ID,"modifier_debug_3")).withConsumer(DEBUG_HEALING_CONSUMER).withConsumer(DEBUG_NECROTIC_CONSUMER)

    fun registerAll(){
        ENTRIES.register(GREATER_ATTUNED)
        ENTRIES.register(ATTUNED)
        ENTRIES.register(LESSER_ATTUNED)
        ENTRIES.register(GREATER_THRIFTY)
        ENTRIES.register(THRIFTY)
        ENTRIES.register(LESSER_THRIFTY)
        ENTRIES.register(MODIFIER_DEBUG)
        ENTRIES.register(MODIFIER_DEBUG_2)
        ENTRIES.register(MODIFIER_DEBUG_3)
    }

    class ModifierRegistry(){
        private val registry: MutableMap<Identifier,AugmentModifier> = mutableMapOf()
        fun register(modifier: AugmentModifier){
            val id = modifier.modifierId
            if (registry.containsKey(id)){throw IllegalStateException("Modifier with id $id already present in ModififerRegistry")}
            registry[id] = modifier
        }
        fun get(id: Identifier): AugmentModifier?{
            return registry[id]
        }
        fun getByRawId(rawId: Int): AugmentModifier?{
            return registry[registry.keys.elementAtOrElse(rawId) { AugmentModifierDefaults.blankId }]
        }
        fun getIdByRawId(rawId:Int): Identifier{
            return registry.keys.elementAtOrElse(rawId) { AugmentModifierDefaults.blankId }
        }
        fun getRawId(id: Identifier): Int{
            return registry.keys.indexOf(id)
        }
        fun getName(id: Identifier): Text{
            return TranslatableText("scepter.modifiers.$id")
        }
        fun isModifier(id: Identifier): Boolean{
            return this.get(id) != null
        }
    }



}