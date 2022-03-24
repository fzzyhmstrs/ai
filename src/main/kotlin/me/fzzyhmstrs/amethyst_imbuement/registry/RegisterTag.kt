package me.fzzyhmstrs.amethyst_imbuement.registry

import me.fzzyhmstrs.amethyst_imbuement.AI
import net.fabricmc.fabric.api.tag.TagFactory
import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.tag.Tag
import net.minecraft.util.Identifier

object RegisterTag {

    val GEMS_TAG: Tag<Item> = TagFactory.ITEM.create(Identifier("c","gems"))
    val STEEL_BOOTS_TAG: Tag<Item> = TagFactory.ITEM.create(Identifier("c","steel_boots"))
    val STEEL_CHESTPLATES_TAG: Tag<Item> = TagFactory.ITEM.create(Identifier("c","steel_chestplates"))
    val STEEL_HELMETS_TAG: Tag<Item> = TagFactory.ITEM.create(Identifier("c","steel_helmets"))
    val STEEL_INGOTS_TAG: Tag<Item> = TagFactory.ITEM.create(Identifier("c","steel_ingots"))
    val STEEL_LEGGINGS_TAG: Tag<Item> = TagFactory.ITEM.create(Identifier("c","steel_leggings"))
    val PILLARS_TAG: Tag<Block> = TagFactory.BLOCK.create(Identifier(AI.MOD_ID,"disenchant_pillar_blocks"))

}