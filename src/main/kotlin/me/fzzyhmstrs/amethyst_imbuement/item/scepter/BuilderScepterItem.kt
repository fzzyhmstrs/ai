package me.fzzyhmstrs.amethyst_imbuement.item.scepter

import me.fzzyhmstrs.amethyst_core.scepter_util.ScepterToolMaterial
import net.minecraft.block.BlockState
import net.minecraft.item.ItemStack
import net.minecraft.registry.tag.BlockTags

class BuilderScepterItem(material: ScepterToolMaterial, settings: Settings): CustomScepterItem(material, settings) {

    override fun getMiningSpeedMultiplier(stack: ItemStack, state: BlockState): Float {
        return if (state.isIn(BlockTags.AXE_MINEABLE) || state.isIn(BlockTags.SHOVEL_MINEABLE)) 6.0f else 1.0f
    }

}