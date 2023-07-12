package me.fzzyhmstrs.amethyst_imbuement.registry

import me.fzzyhmstrs.amethyst_imbuement.AI
import net.minecraft.block.Block
import net.minecraft.entity.damage.DamageType
import net.minecraft.item.Item
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.tag.TagKey
import net.minecraft.util.Identifier

@Suppress("unused")
object RegisterTag {

    val GUARDIAN_IGNORES_DAMAGE_TAG: TagKey<DamageType> = TagKey.of(RegistryKeys.DAMAGE_TYPE,AI.identity("guardian_ignores_damage"))

    val GEMS_TAG: TagKey<Item> = TagKey.of(RegistryKeys.ITEM,Identifier("c","gems"))
    val SKULLS_TAG: TagKey<Item> = TagKey.of(RegistryKeys.ITEM,Identifier("c","skulls"))
    val STEEL_BOOTS_TAG: TagKey<Item> = TagKey.of(RegistryKeys.ITEM,Identifier("c","steel_boots"))
    val STEEL_CHESTPLATES_TAG: TagKey<Item> = TagKey.of(RegistryKeys.ITEM,Identifier("c","steel_chestplates"))
    val STEEL_HELMETS_TAG: TagKey<Item> = TagKey.of(RegistryKeys.ITEM,Identifier("c","steel_helmets"))
    val STEEL_LEGGINGS_TAG: TagKey<Item> = TagKey.of(RegistryKeys.ITEM,Identifier("c","steel_leggings"))
    val STEEL_INGOTS_TAG: TagKey<Item> = TagKey.of(RegistryKeys.ITEM,Identifier("c","steel_ingots"))
    val STEEL_BLOCKS_TAG: TagKey<Item> = TagKey.of(RegistryKeys.ITEM,Identifier("c","steel_blocks"))
    val FOCUS_REAGENTS_TAG: TagKey<Item> = TagKey.of(RegistryKeys.ITEM,AI.identity("focus_reagents"))
    val ASPECT_GEMS_TAG: TagKey<Item> = TagKey.of(RegistryKeys.ITEM,AI.identity("aspect_gems"))
    val BASIC_WARDS_TAG: TagKey<Item> = TagKey.of(RegistryKeys.ITEM,AI.identity("basic_wards"))
    val PILLARS_ITEM_TAG: TagKey<Item> = TagKey.of(RegistryKeys.ITEM,AI.identity("disenchant_pillar_blocks"))
    val PILLARS_TAG: TagKey<Block> = TagKey.of(RegistryKeys.BLOCK,AI.identity("disenchant_pillar_blocks"))
    val SHINE_LIGHTS_ITEM_TAG: TagKey<Item> = TagKey.of(RegistryKeys.ITEM,AI.identity("shine_lights"))
    val SHINE_LIGHTS_TAG: TagKey<Block> = TagKey.of(RegistryKeys.BLOCK,AI.identity("shine_lights"))
    val CRYSTALLIZED_LIGHTS_ITEM_TAG: TagKey<Item> = TagKey.of(RegistryKeys.ITEM,AI.identity("crystallized_lights"))
    val CRYSTALLIZED_LIGHTS_TAG: TagKey<Block> = TagKey.of(RegistryKeys.BLOCK,AI.identity("crystallized_lights"))
    
    val EQUINOX_SCEPTERS_TAG: TagKey<Item> = TagKey.of(RegistryKeys.ITEM,AI.identity("equinox_scepters"))
    val LETHALITY_SCEPTERS_TAG: TagKey<Item> = TagKey.of(RegistryKeys.ITEM,AI.identity("lethality_scepters"))
    val RESONANCE_SCEPTERS_TAG: TagKey<Item> = TagKey.of(RegistryKeys.ITEM,AI.identity("resonance_scepters"))
    val SOJOURN_SCEPTERS_TAG: TagKey<Item> = TagKey.of(RegistryKeys.ITEM,AI.identity("sojourn_scepters"))
    val AEGIS_SCEPTERS_TAG: TagKey<Item> = TagKey.of(RegistryKeys.ITEM,AI.identity("aegis_scepters"))
    val REDEMPTION_SCEPTERS_TAG: TagKey<Item> = TagKey.of(RegistryKeys.ITEM,AI.identity("redemption_scepters"))
    val FURY_SCEPTERS_TAG: TagKey<Item> = TagKey.of(RegistryKeys.ITEM,AI.identity("fury_scepters"))
    val WIT_SCEPTERS_TAG: TagKey<Item> = TagKey.of(RegistryKeys.ITEM,AI.identity("wit_scepters"))
    val GRACE_SCEPTERS_TAG: TagKey<Item> = TagKey.of(RegistryKeys.ITEM,AI.identity("grace_scepters"))

    val ALL_FURY_SCEPTERS_TAG: TagKey<Item> = TagKey.of(RegistryKeys.ITEM,AI.identity("all_fury_scepters"))
    val ALL_WIT_SCEPTERS_TAG: TagKey<Item> = TagKey.of(RegistryKeys.ITEM,AI.identity("all_wit_scepters"))
    val ALL_GRACE_SCEPTERS_TAG: TagKey<Item> = TagKey.of(RegistryKeys.ITEM,AI.identity("all_grace_scepters"))
    

}
