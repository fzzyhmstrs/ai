package me.fzzyhmstrs.amethyst_imbuement.item.scepter

import me.fzzyhmstrs.amethyst_core.scepter.ScepterToolMaterial
import net.fabricmc.fabric.api.mininglevel.v1.FabricMineableTags
import net.minecraft.block.BlockState
import net.minecraft.item.ItemStack
import net.minecraft.registry.tag.BlockTags

class ScepterOfHarvestsItem(material: ScepterToolMaterial, settings: Settings
): CustomSpellToolItem(material, 1.5f,-3.0f, BlockTags.HOE_MINEABLE, settings) {
    
    override fun getMiningSpeedMultiplier(stack: ItemStack, state: BlockState): Float {
        return if (state.isIn(BlockTags.HOE_MINEABLE) || state.isIn(FabricMineableTags.SHEARS_MINEABLE)) material.miningSpeedMultiplier else 1.0f
    }
}
