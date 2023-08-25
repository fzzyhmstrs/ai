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

    val GUARDIAN_IGNORES_DAMAGE_TAG: TagKey<DamageType> = TagKey.of(RegistryKeys.DAMAGE_TYPE,Identifier(AI.MOD_ID,"guardian_ignores_damage"))

    val GEMS_TAG: TagKey<Item> = TagKey.of(RegistryKeys.ITEM,Identifier("c","gems"))
    val SKULLS_TAG: TagKey<Item> = TagKey.of(RegistryKeys.ITEM,Identifier("c","skulls"))
    val STEEL_BOOTS_TAG: TagKey<Item> = TagKey.of(RegistryKeys.ITEM,Identifier("c","steel_boots"))
    val STEEL_CHESTPLATES_TAG: TagKey<Item> = TagKey.of(RegistryKeys.ITEM,Identifier("c","steel_chestplates"))
    val STEEL_HELMETS_TAG: TagKey<Item> = TagKey.of(RegistryKeys.ITEM,Identifier("c","steel_helmets"))
    val STEEL_LEGGINGS_TAG: TagKey<Item> = TagKey.of(RegistryKeys.ITEM,Identifier("c","steel_leggings"))
    val STEEL_INGOTS_TAG: TagKey<Item> = TagKey.of(RegistryKeys.ITEM,Identifier("c","steel_ingots"))
    val STEEL_BLOCKS_TAG: TagKey<Item> = TagKey.of(RegistryKeys.ITEM,Identifier("c","steel_blocks"))
    val FOCUS_REAGENTS_TAG: TagKey<Item> = TagKey.of(RegistryKeys.ITEM,Identifier(AI.MOD_ID,"focus_reagents"))
    val ASPECT_GEMS_TAG: TagKey<Item> = TagKey.of(RegistryKeys.ITEM,Identifier(AI.MOD_ID,"aspect_gems"))
    val PILLARS_ITEM_TAG: TagKey<Item> = TagKey.of(RegistryKeys.ITEM,Identifier(AI.MOD_ID,"disenchant_pillar_blocks"))
    val PILLARS_TAG: TagKey<Block> = TagKey.of(RegistryKeys.BLOCK,Identifier(AI.MOD_ID,"disenchant_pillar_blocks"))
    val SHINE_LIGHTS_ITEM_TAG: TagKey<Item> = TagKey.of(RegistryKeys.ITEM,Identifier(AI.MOD_ID,"shine_lights"))
    val SHINE_LIGHTS_TAG: TagKey<Block> = TagKey.of(RegistryKeys.BLOCK,Identifier(AI.MOD_ID,"shine_lights"))
    val CRYSTALLIZED_LIGHTS_ITEM_TAG: TagKey<Item> = TagKey.of(RegistryKeys.ITEM,Identifier(AI.MOD_ID,"crystallized_lights"))
    val CRYSTALLIZED_LIGHTS_TAG: TagKey<Block> = TagKey.of(RegistryKeys.BLOCK,Identifier(AI.MOD_ID,"crystallized_lights"))
    
    val EQUINOX_SCEPTERS_TAG: TagKey<Item> = TagKey.of(RegistryKeys.ITEM,Identifier(AI.MOD_ID,"equinox_scepters"))
    val LETHALITY_SCEPTERS_TAG: TagKey<Item> = TagKey.of(RegistryKeys.ITEM,Identifier(AI.MOD_ID,"lethality_scepters"))
    val RESONANCE_SCEPTERS_TAG: TagKey<Item> = TagKey.of(RegistryKeys.ITEM,Identifier(AI.MOD_ID,"resonance_scepters"))
    val SOJOURN_SCEPTERS_TAG: TagKey<Item> = TagKey.of(RegistryKeys.ITEM,Identifier(AI.MOD_ID,"sojourn_scepters"))
    val AEGIS_SCEPTERS_TAG: TagKey<Item> = TagKey.of(RegistryKeys.ITEM,Identifier(AI.MOD_ID,"aegis_scepters"))
    val REDEMPTION_SCEPTERS_TAG: TagKey<Item> = TagKey.of(RegistryKeys.ITEM,Identifier(AI.MOD_ID,"redemption_scepters"))
    val FURY_SCEPTERS_TAG: TagKey<Item> = TagKey.of(RegistryKeys.ITEM,Identifier(AI.MOD_ID,"fury_scepters"))
    val WIT_SCEPTERS_TAG: TagKey<Item> = TagKey.of(RegistryKeys.ITEM,Identifier(AI.MOD_ID,"wit_scepters"))
    val GRACE_SCEPTERS_TAG: TagKey<Item> = TagKey.of(RegistryKeys.ITEM,Identifier(AI.MOD_ID,"grace_scepters"))

    val ALL_FURY_SCEPTERS_TAG: TagKey<Item> = TagKey.of(RegistryKeys.ITEM,Identifier(AI.MOD_ID,"all_fury_scepters"))
    val ALL_WIT_SCEPTERS_TAG: TagKey<Item> = TagKey.of(RegistryKeys.ITEM,Identifier(AI.MOD_ID,"all_wit_scepters"))
    val ALL_GRACE_SCEPTERS_TAG: TagKey<Item> = TagKey.of(RegistryKeys.ITEM,Identifier(AI.MOD_ID,"all_grace_scepters"))

    val HEADBANDS_TAG: TagKey<Item> = TagKey.of(RegistryKeys.ITEM,Identifier(AI.MOD_ID,"headbands"))
    val AMULETS_TAG: TagKey<Item> = TagKey.of(RegistryKeys.ITEM,Identifier(AI.MOD_ID,"amulets"))
    val RINGS_TAG: TagKey<Item> = TagKey.of(RegistryKeys.ITEM,Identifier(AI.MOD_ID,"rings"))
    val TOTEMS_TAG: TagKey<Item> = TagKey.of(RegistryKeys.ITEM,Identifier(AI.MOD_ID,"totems"))
    val WARDS_TAG: TagKey<Item> = TagKey.of(RegistryKeys.ITEM,Identifier(AI.MOD_ID,"wards"))
    val ALL_WARDS_TAG: TagKey<Item> = TagKey.of(RegistryKeys.ITEM,Identifier(AI.MOD_ID,"all_wards"))

    fun registerAll(){}

}
