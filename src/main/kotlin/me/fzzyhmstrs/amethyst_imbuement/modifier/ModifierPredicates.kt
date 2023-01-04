package me.fzzyhmstrs.amethyst_imbuement.modifier

import me.fzzyhmstrs.amethyst_core.AC
import me.fzzyhmstrs.amethyst_core.scepter_util.SpellType
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.*
import net.minecraft.enchantment.Enchantment
import net.minecraft.registry.Registries
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.tag.TagKey
import net.minecraft.util.Identifier
import java.util.function.Predicate

object ModifierPredicates {

    private val FIRE_AUGMENTS: TagKey<Enchantment> = TagKey.of(RegistryKeys.ENCHANTMENT, Identifier(AC.MOD_ID,"fire_augments"))
    private val ICE_AUGMENTS: TagKey<Enchantment> = TagKey.of(RegistryKeys.ENCHANTMENT, Identifier(AC.MOD_ID,"ice_augments"))
    private val LIGHTNING_AUGMENTS: TagKey<Enchantment> = TagKey.of(RegistryKeys.ENCHANTMENT, Identifier(AC.MOD_ID,"lightning_augments"))
    private val ELEMENTAL_AUGMENTS: TagKey<Enchantment> = TagKey.of(RegistryKeys.ENCHANTMENT, Identifier(AC.MOD_ID,"elemental_augments"))
    private val HEALER_AUGMENTS: TagKey<Enchantment> = TagKey.of(RegistryKeys.ENCHANTMENT, Identifier(AC.MOD_ID,"healer_augments"))
    private val BUILDER_AUGMENTS: TagKey<Enchantment> = TagKey.of(RegistryKeys.ENCHANTMENT, Identifier(AC.MOD_ID,"builder_augments"))
    private val TRAVELER_AUGMENTS: TagKey<Enchantment> = TagKey.of(RegistryKeys.ENCHANTMENT, Identifier(AC.MOD_ID,"traveler_augments"))

    val FURIOUS_PREDICATE = Predicate {id: Identifier -> AugmentHelper.getAugmentType(id.toString()) == SpellType.FURY}
    val WITTY_PREDICATE = Predicate {id: Identifier -> AugmentHelper.getAugmentType(id.toString()) == SpellType.WIT}
    val GRACEFUL_PREDICATE = Predicate {id: Identifier -> AugmentHelper.getAugmentType(id.toString()) == SpellType.GRACE}
    val BLADE_PREDICATE = Predicate {id: Identifier -> Registries.ENCHANTMENT.get(id) is SlashAugment}
    val ICE_PREDICATE = Predicate {id: Identifier -> isInTag(id, ICE_AUGMENTS)}
    val ELEMENTAL_PREDICATE = Predicate {id: Identifier -> isInTag(id, ELEMENTAL_AUGMENTS)}
    val HEALERS_PREDICATE = Predicate {id: Identifier -> isInTag(id, HEALER_AUGMENTS)}
    val HEALERS_PACT_PREDICATE = Predicate {id: Identifier -> !isInTag(id, HEALER_AUGMENTS)}
    val DANGER_PACT_PREDICATE = Predicate {id: Identifier -> AugmentHelper.getAugmentType(id.toString()) != SpellType.FURY}
    val FIRE_PREDICATE = Predicate {id: Identifier -> isInTag(id, FIRE_AUGMENTS)}
    val LIGHTNING_PREDICATE = Predicate {id: Identifier -> isInTag(id, LIGHTNING_AUGMENTS)}
    val SUMMONERS_PREDICATE = Predicate {id: Identifier -> Registries.ENCHANTMENT.get(id) is SummonEntityAugment}
    val BUILDERS_PREDICATE = Predicate {id: Identifier -> isInTag(id, BUILDER_AUGMENTS)}
    val TRAVELER_PREDICATE = Predicate {id: Identifier -> isInTag(id, TRAVELER_AUGMENTS)}

    private fun isInTag(id: Identifier,tag: TagKey<Enchantment>): Boolean{
        val augment = Registries.ENCHANTMENT.get(id)?:return false
        val opt = Registries.ENCHANTMENT.getEntry(Registries.ENCHANTMENT.getRawId(augment))
        var bl = false
        opt.ifPresent { entry -> bl = entry.isIn(tag) }
        return bl
    }

}
