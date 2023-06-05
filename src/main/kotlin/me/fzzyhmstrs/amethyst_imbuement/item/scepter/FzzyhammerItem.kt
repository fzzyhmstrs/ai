package me.fzzyhmstrs.amethyst_imbuement.item.scepter

import me.fzzyhmstrs.amethyst_core.scepter_util.ScepterToolMaterial
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import me.fzzyhmstrs.amethyst_imbuement.tool.FzzyhammerToolMaterial
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemStack
import net.minecraft.registry.tag.BlockTags
import net.minecraft.registry.tag.TagKey
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class FzzyhammerItem(settings: Settings):
    CustomSpellToolItem(FzzyhammerToolMaterial, 1.5f,FzzyhammerToolMaterial.getAttackSpeed().toFloat(), BlockTags.PICKAXE_MINEABLE, settings)

{
    override fun canAcceptAugment(augment: ScepterAugment): Boolean {
        return augment == RegisterEnchantment.HAMPTERTIME
    }

    override fun postMine(
        stack: ItemStack,
        world: World?,
        state: BlockState,
        pos: BlockPos?,
        miner: LivingEntity
    ): Boolean {
        return super.postMine(stack, world, state, pos, miner)
    }

}