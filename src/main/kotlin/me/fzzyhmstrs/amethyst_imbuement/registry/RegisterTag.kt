package me.fzzyhmstrs.amethyst_imbuement.registry

import me.fzzyhmstrs.amethyst_imbuement.AI
import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.tag.TagKey
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

@Suppress("unused")
object RegisterTag {

    val GEMS_TAG: TagKey<Item> = TagKey.of(Registry.ITEM_KEY,Identifier("c","gems"))
    val SKULLS_TAG: TagKey<Item> = TagKey.of(Registry.ITEM_KEY,Identifier("c","skulls"))
    val STEEL_BOOTS_TAG: TagKey<Item> = TagKey.of(Registry.ITEM_KEY,Identifier("c","steel_boots"))
    val STEEL_CHESTPLATES_TAG: TagKey<Item> = TagKey.of(Registry.ITEM_KEY,Identifier("c","steel_chestplates"))
    val STEEL_HELMETS_TAG: TagKey<Item> = TagKey.of(Registry.ITEM_KEY,Identifier("c","steel_helmets"))
    val STEEL_LEGGINGS_TAG: TagKey<Item> = TagKey.of(Registry.ITEM_KEY,Identifier("c","steel_leggings"))
    val STEEL_INGOTS_TAG: TagKey<Item> = TagKey.of(Registry.ITEM_KEY,Identifier("c","steel_ingots"))
    val STEEL_BLOCKS_TAG: TagKey<Item> = TagKey.of(Registry.ITEM_KEY,Identifier("c","steel_blocks"))
    val PILLARS_TAG: TagKey<Block> = TagKey.of(Registry.BLOCK_KEY,Identifier(AI.MOD_ID,"disenchant_pillar_blocks"))
    
    val EQUINOX_SCEPTERS_TAG: TagKey<Item> = TagKey.of(Registry.ITEM_KEY,Identifier(AI.MOD_ID,"equinox_scepters"))
    val LETHALITY_SCEPTERS_TAG: TagKey<Item> = TagKey.of(Registry.ITEM_KEY,Identifier(AI.MOD_ID,"lethality_scepters"))
    val RESONANCE_SCEPTERS_TAG: TagKey<Item> = TagKey.of(Registry.ITEM_KEY,Identifier(AI.MOD_ID,"resonance_scepters"))
    val SOJOURN_SCEPTERS_TAG: TagKey<Item> = TagKey.of(Registry.ITEM_KEY,Identifier(AI.MOD_ID,"sojourn_scepters"))
    val AEGIS_SCEPTERS_TAG: TagKey<Item> = TagKey.of(Registry.ITEM_KEY,Identifier(AI.MOD_ID,"aegis_scepters"))
    val REDEMPTION_SCEPTERS_TAG: TagKey<Item> = TagKey.of(Registry.ITEM_KEY,Identifier(AI.MOD_ID,"redemption_scepters"))
    val FURY_SCEPTERS_TAG: TagKey<Item> = TagKey.of(Registry.ITEM_KEY,Identifier(AI.MOD_ID,"fury_scepters"))
    val WIT_SCEPTERS_TAG: TagKey<Item> = TagKey.of(Registry.ITEM_KEY,Identifier(AI.MOD_ID,"wit_scepters"))
    val GRACE_SCEPTERS_TAG: TagKey<Item> = TagKey.of(Registry.ITEM_KEY,Identifier(AI.MOD_ID,"grace_scepters"))

    val ALL_FURY_SCEPTERS_TAG: TagKey<Item> = TagKey.of(Registry.ITEM_KEY,Identifier(AI.MOD_ID,"all_fury_scepters"))
    val ALL_WIT_SCEPTERS_TAG: TagKey<Item> = TagKey.of(Registry.ITEM_KEY,Identifier(AI.MOD_ID,"all_wit_scepters"))
    val ALL_GRACE_SCEPTERS_TAG: TagKey<Item> = TagKey.of(Registry.ITEM_KEY,Identifier(AI.MOD_ID,"all_grace_scepters"))
    

}
