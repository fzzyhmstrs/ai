package me.fzzyhmstrs.amethyst_imbuement.modifier

import me.fzzyhmstrs.amethyst_core.scepter_util.SpellType
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.*
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier
import java.util.function.Predicate

object ModifierPredicates {

    val FURIOUS_PREDICATE = Predicate {id: Identifier -> furiousPredicate(id)}
    private fun furiousPredicate(id: Identifier): Boolean{
        return AugmentHelper.getAugmentType(id.toString()) == SpellType.FURY
    }
    val WITTY_PREDICATE = Predicate {id: Identifier -> wittyPredicate(id)}
    private fun wittyPredicate(id: Identifier): Boolean{
        return AugmentHelper.getAugmentType(id.toString()) == SpellType.WIT
    }
    val GRACEFUL_PREDICATE = Predicate {id: Identifier -> gracefulPredicate(id)}
    private fun gracefulPredicate(id: Identifier): Boolean{
        return AugmentHelper.getAugmentType(id.toString()) == SpellType.GRACE
    }
    val BLADE_PREDICATE = Predicate {id: Identifier -> bladePredicate(id)}
    private fun bladePredicate(id: Identifier): Boolean{
        val augment = Registries.ENCHANTMENT.get(id)?:return false
        return augment is SlashAugment
    }
    val ICE_PREDICATE = Predicate {id: Identifier -> icePredicate(id)}
    private fun icePredicate(id: Identifier): Boolean{
        val augment = Registries.ENCHANTMENT.get(id)?:return false
        return augment is IceAugment
    }
    val ELEMENTAL_PREDICATE = Predicate {id: Identifier -> elementalPredicate(id)}
    private fun elementalPredicate(id: Identifier): Boolean{
        val augment = Registries.ENCHANTMENT.get(id)?:return false
        return augment is ElementalAugment
    }

    val HEALERS_PREDICATE = Predicate {id: Identifier -> healersPredicate(id)}
    private fun healersPredicate(id: Identifier): Boolean{
        val augment = Registries.ENCHANTMENT.get(id)?:return false
        return augment is HealerAugment
    }

    val HEALERS_PACT_PREDICATE = Predicate {id: Identifier -> healersPactPredicate(id)}
    private fun healersPactPredicate(id: Identifier): Boolean{
        val augment = Registries.ENCHANTMENT.get(id)?:return false
        return augment !is HealerAugment
    }

    val DANGER_PACT_PREDICATE = Predicate {id: Identifier -> dangerPactPredicate(id)}
    private fun dangerPactPredicate(id: Identifier): Boolean{
        return  AugmentHelper.getAugmentType(id.toString()) != SpellType.FURY
    }

    val FIRE_PREDICATE = Predicate {id: Identifier -> firePredicate(id)}
    private fun firePredicate(id: Identifier): Boolean{
        val augment = Registries.ENCHANTMENT.get(id)?:return false
        return augment is FireAugment
    }

    val LIGHTNING_PREDICATE = Predicate {id: Identifier -> lightningPredicate(id)}
    private fun lightningPredicate(id: Identifier): Boolean{
        val augment = Registries.ENCHANTMENT.get(id)?:return false
        return augment is LightningAugment
    }

    val SUMMONERS_PREDICATE = Predicate {id: Identifier -> summonersPredicate(id)}
    private fun summonersPredicate(id: Identifier): Boolean{
        val augment = Registries.ENCHANTMENT.get(id)?:return false
        return augment is SummonEntityAugment
    }

    val BUILDERS_PREDICATE = Predicate {id: Identifier -> buildersPredicate(id)}
    private fun buildersPredicate(id: Identifier): Boolean{
        val augment = Registries.ENCHANTMENT.get(id)?:return false
        return augment is BuilderAugment
    }
    
    val TRAVELER_PREDICATE = Predicate {id: Identifier -> travelerPredicate(id)}
    private fun travelerPredicate(id: Identifier): Boolean{
        val augment = Registries.ENCHANTMENT.get(id)?:return false
        return augment is TravelerAugment
    }

}
