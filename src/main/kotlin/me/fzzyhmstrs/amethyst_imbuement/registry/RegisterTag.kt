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
    val STEEL_BOOTS_TAG: TagKey<Item> = TagKey.of(Registry.ITEM_KEY,Identifier("c","steel_boots"))
    val STEEL_CHESTPLATES_TAG: TagKey<Item> = TagKey.of(Registry.ITEM_KEY,Identifier("c","steel_chestplates"))
    val STEEL_HELMETS_TAG: TagKey<Item> = TagKey.of(Registry.ITEM_KEY,Identifier("c","steel_helmets"))
    val STEEL_INGOTS_TAG: TagKey<Item> = TagKey.of(Registry.ITEM_KEY,Identifier("c","steel_ingots"))
    val STEEL_LEGGINGS_TAG: TagKey<Item> = TagKey.of(Registry.ITEM_KEY,Identifier("c","steel_leggings"))
    val PILLARS_TAG: TagKey<Block> = TagKey.of(Registry.BLOCK_KEY,Identifier(AI.MOD_ID,"disenchant_pillar_blocks"))

}