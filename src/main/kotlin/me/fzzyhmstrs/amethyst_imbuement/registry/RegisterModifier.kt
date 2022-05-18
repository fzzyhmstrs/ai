package me.fzzyhmstrs.amethyst_imbuement.registry

import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.scepter.base_augments.AugmentModifier
import net.minecraft.util.Identifier

object RegisterModifier {

    val ENTRIES: ModifierRegistry = ModifierRegistry()

    val GREATER_ATTUNED = AugmentModifier(Identifier(AI.MOD_ID,"attuned_3"), cooldownModifier = -22.5)
    val ATTUNED = AugmentModifier(Identifier(AI.MOD_ID,"attuned_2"), cooldownModifier = -15.0).withDescendant(GREATER_ATTUNED)
    val LESSER_ATTUNED = AugmentModifier(Identifier(AI.MOD_ID,"attuned_1"), cooldownModifier = -7.5).withDescendant(ATTUNED)
    val GREATER_THRIFTY = AugmentModifier(Identifier(AI.MOD_ID,"thrifty_3"), manaCostModifier = -15.0)
    val THRIFTY = AugmentModifier(Identifier(AI.MOD_ID,"thrifty_2"), manaCostModifier = -10.0).withDescendant(GREATER_THRIFTY)
    val LESSER_THRIFTY = AugmentModifier(Identifier(AI.MOD_ID,"thrifty_1"), manaCostModifier = -5.0).withDescendant(THRIFTY)

    fun registerAll(){
        ENTRIES.register(GREATER_ATTUNED)
        ENTRIES.register(ATTUNED)
        ENTRIES.register(LESSER_ATTUNED)
        ENTRIES.register(GREATER_THRIFTY)
        ENTRIES.register(THRIFTY)
        ENTRIES.register(LESSER_THRIFTY)
    }

    data class ModifierRegistry(private val registry: MutableMap<Identifier,AugmentModifier> = mutableMapOf()){
        fun register(modifier: AugmentModifier){
            val id = modifier.modifierId
            if (registry.containsKey(id)){throw IllegalStateException("Modifier with id $id already present in ModififerRegistry")}
            registry[id] = modifier
        }
        fun get(id: Identifier): AugmentModifier?{
            return registry[id]
        }
        fun isModifier(id: Identifier): Boolean{
            return this.get(id) != null
        }
    }



}