package me.fzzyhmstrs.amethyst_imbuement.modifier

import me.fzzyhmstrs.amethyst_core.registry.RegisterTag
import me.fzzyhmstrs.amethyst_core.scepter_util.SpellType
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.AugmentHelper
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.SlashAugment
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.SummonEntityAugment
import me.fzzyhmstrs.fzzy_core.coding_util.FzzyPort
import net.minecraft.enchantment.Enchantment
import net.minecraft.registry.tag.TagKey
import net.minecraft.util.Identifier
import java.util.function.Predicate

object ModifierPredicates {

    /*private val FIRE_AUGMENTS: TagKey<Enchantment> = TagKey.of(RegistryKeys.ENCHANTMENT, Identifier(AC.MOD_ID,"fire_augments"))
    private val ICE_AUGMENTS: TagKey<Enchantment> = TagKey.of(RegistryKeys.ENCHANTMENT, Identifier(AC.MOD_ID,"ice_augments"))
    private val LIGHTNING_AUGMENTS: TagKey<Enchantment> = TagKey.of(RegistryKeys.ENCHANTMENT, Identifier(AC.MOD_ID,"lightning_augments"))
    private val ELEMENTAL_AUGMENTS: TagKey<Enchantment> = TagKey.of(RegistryKeys.ENCHANTMENT, Identifier(AC.MOD_ID,"elemental_augments"))
    private val HEALER_AUGMENTS: TagKey<Enchantment> = TagKey.of(RegistryKeys.ENCHANTMENT, Identifier(AC.MOD_ID,"healer_augments"))
    private val BUILDER_AUGMENTS: TagKey<Enchantment> = TagKey.of(RegistryKeys.ENCHANTMENT, Identifier(AC.MOD_ID,"builder_augments"))
    private val TRAVELER_AUGMENTS: TagKey<Enchantment> = TagKey.of(RegistryKeys.ENCHANTMENT, Identifier(AC.MOD_ID,"traveler_augments"))
    private val BOLT_AUGMENTS: TagKey<Enchantment> = TagKey.of(RegistryKeys.ENCHANTMENT, Identifier(AC.MOD_ID,"bolt_augments"))*/

    val FURIOUS_PREDICATE = Predicate {id: Identifier -> AugmentHelper.getAugmentType(id.toString()) == SpellType.FURY}
    val WITTY_PREDICATE = Predicate {id: Identifier -> AugmentHelper.getAugmentType(id.toString()) == SpellType.WIT}
    val GRACEFUL_PREDICATE = Predicate {id: Identifier -> AugmentHelper.getAugmentType(id.toString()) == SpellType.GRACE}
    val BLADE_PREDICATE = Predicate {id: Identifier -> FzzyPort.ENCHANTMENT.get(id) is SlashAugment}
    val ICE_PREDICATE = Predicate {id: Identifier -> isInTag(id, RegisterTag.ICE_AUGMENTS)}
    val ELEMENTAL_PREDICATE = Predicate {id: Identifier -> isInTag(id, RegisterTag.ELEMENTAL_AUGMENTS)}
    val HEALERS_PREDICATE = Predicate {id: Identifier -> isInTag(id, RegisterTag.HEALER_AUGMENTS)}
    val HEALERS_PACT_PREDICATE = Predicate {id: Identifier -> !isInTag(id, RegisterTag.HEALER_AUGMENTS)}
    val DANGER_PACT_PREDICATE = Predicate {id: Identifier -> AugmentHelper.getAugmentType(id.toString()) != SpellType.FURY}
    val FIRE_PREDICATE = Predicate {id: Identifier -> isInTag(id, RegisterTag.FIRE_AUGMENTS)}
    val LIGHTNING_PREDICATE = Predicate {id: Identifier -> isInTag(id, RegisterTag.LIGHTNING_AUGMENTS)}
    val SUMMONERS_PREDICATE = Predicate {id: Identifier -> FzzyPort.ENCHANTMENT.get(id) is SummonEntityAugment}
    val BUILDERS_PREDICATE = Predicate {id: Identifier -> isInTag(id, RegisterTag.BUILDER_AUGMENTS)}
    val TRAVELER_PREDICATE = Predicate {id: Identifier -> isInTag(id, RegisterTag.TRAVELER_AUGMENTS)}
    val BOLT_PREDICATE = Predicate {id: Identifier -> isInTag(id, RegisterTag.BOLT_AUGMENTS)}
    val SOUL_PREDICATE = Predicate {id: Identifier -> isInTag(id, RegisterTag.SOUL_AUGMENTS)}

    private fun isInTag(id: Identifier,tag: TagKey<Enchantment>): Boolean{
        val augment = FzzyPort.ENCHANTMENT.get(id)?:return false
        return FzzyPort.ENCHANTMENT.getEntry(augment).isIn(tag)
    }

}
