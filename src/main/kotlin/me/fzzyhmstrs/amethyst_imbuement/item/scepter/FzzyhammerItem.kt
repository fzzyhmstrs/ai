package me.fzzyhmstrs.amethyst_imbuement.item.scepter

import me.fzzyhmstrs.amethyst_core.scepter_util.ScepterToolMaterial
import me.fzzyhmstrs.amethyst_imbuement.tool.FzzyhammerToolMaterial
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.item.ItemStack
import net.minecraft.registry.tag.BlockTags
import net.minecraft.registry.tag.TagKey

class FzzyhammerItem(settings: Settings):
    CustomSpellToolItem(FzzyhammerToolMaterial, 1.5f,FzzyhammerToolMaterial.getAttackSpeed().toFloat(), BlockTags.PICKAXE_MINEABLE, settings) {

}