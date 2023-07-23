package me.fzzyhmstrs.amethyst_imbuement.modifier

import me.fzzyhmstrs.amethyst_core.AC
import me.fzzyhmstrs.amethyst_core.augments.AugmentHelper
import me.fzzyhmstrs.amethyst_core.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_core.augments.base.SlashAugment
import me.fzzyhmstrs.amethyst_core.augments.base.SummonAugment
import me.fzzyhmstrs.amethyst_core.scepter.SpellType
import net.minecraft.enchantment.Enchantment
import net.minecraft.registry.Registries
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.tag.TagKey
import net.minecraft.util.Identifier
import java.util.function.Predicate

object ModifierPredicates {

    val FIRE_AUGMENTS: TagKey<Enchantment> = TagKey.of(RegistryKeys.ENCHANTMENT, AC.identity("fire_augments"))
    val ICE_AUGMENTS: TagKey<Enchantment> = TagKey.of(RegistryKeys.ENCHANTMENT, AC.identity("ice_augments"))
    val LIGHTNING_AUGMENTS: TagKey<Enchantment> = TagKey.of(RegistryKeys.ENCHANTMENT, AC.identity("lightning_augments"))
    val ELEMENTAL_AUGMENTS: TagKey<Enchantment> = TagKey.of(RegistryKeys.ENCHANTMENT, AC.identity("elemental_augments"))
    val HEALER_AUGMENTS: TagKey<Enchantment> = TagKey.of(RegistryKeys.ENCHANTMENT, AC.identity("healer_augments"))
    val BUILDER_AUGMENTS: TagKey<Enchantment> = TagKey.of(RegistryKeys.ENCHANTMENT, AC.identity("builder_augments"))
    val TRAVELER_AUGMENTS: TagKey<Enchantment> = TagKey.of(RegistryKeys.ENCHANTMENT, AC.identity("traveler_augments"))
    val BOLT_AUGMENTS: TagKey<Enchantment> = TagKey.of(RegistryKeys.ENCHANTMENT, AC.identity("bolt_augments"))
    val CHICKEN_AUGMENTS: TagKey<Enchantment> = TagKey.of(RegistryKeys.ENCHANTMENT, AC.identity("chicken_augments"))

    val FURIOUS_PREDICATE = Predicate {id: Identifier -> AugmentHelper.getAugmentType(id.toString()) == SpellType.FURY}
    val WITTY_PREDICATE = Predicate {id: Identifier -> AugmentHelper.getAugmentType(id.toString()) == SpellType.WIT}
    val GRACEFUL_PREDICATE = Predicate {id: Identifier -> AugmentHelper.getAugmentType(id.toString()) == SpellType.GRACE}
    val BLADE_PREDICATE = Predicate {id: Identifier -> Registries.ENCHANTMENT.get(id) is SlashAugment }
    val ICE_PREDICATE = Predicate {id: Identifier -> isInTag(id, ICE_AUGMENTS)}
    val ELEMENTAL_PREDICATE = Predicate {id: Identifier -> isInTag(id, ELEMENTAL_AUGMENTS)}
    val HEALERS_PREDICATE = Predicate {id: Identifier -> isInTag(id, HEALER_AUGMENTS)}
    val HEALERS_PACT_PREDICATE = Predicate {id: Identifier -> !isInTag(id, HEALER_AUGMENTS)}
    val DANGER_PACT_PREDICATE = Predicate {id: Identifier -> AugmentHelper.getAugmentType(id.toString()) != SpellType.FURY}
    val FIRE_PREDICATE = Predicate {id: Identifier -> isInTag(id, FIRE_AUGMENTS)}
    val LIGHTNING_PREDICATE = Predicate {id: Identifier -> isInTag(id, LIGHTNING_AUGMENTS)}
    val SUMMONERS_PREDICATE = Predicate {id: Identifier -> Registries.ENCHANTMENT.get(id) is SummonAugment<*>}
    val BUILDERS_PREDICATE = Predicate {id: Identifier -> isInTag(id, BUILDER_AUGMENTS)}
    val TRAVELER_PREDICATE = Predicate {id: Identifier -> isInTag(id, TRAVELER_AUGMENTS)}
    val BOLT_PREDICATE = Predicate {id: Identifier -> isInTag(id, BOLT_AUGMENTS)}

    private fun isInTag(id: Identifier,tag: TagKey<Enchantment>): Boolean{
        val augment = Registries.ENCHANTMENT.get(id)?:return false
        val opt = Registries.ENCHANTMENT.getEntry(Registries.ENCHANTMENT.getRawId(augment))
        var bl = false
        opt.ifPresent { entry -> bl = entry.isIn(tag) }
        return bl
    }

    fun ScepterAugment.isIn(tag: TagKey<Enchantment>): Boolean{
        val opt = Registries.ENCHANTMENT.getEntry(Registries.ENCHANTMENT.getRawId(this))
        var bl = false
        opt.ifPresent { entry -> bl = entry.isIn(tag) }
        return bl
    }

}
