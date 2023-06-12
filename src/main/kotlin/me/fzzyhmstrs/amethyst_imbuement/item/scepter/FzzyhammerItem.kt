package me.fzzyhmstrs.amethyst_imbuement.item.scepter

import me.fzzyhmstrs.amethyst_core.scepter_util.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import me.fzzyhmstrs.amethyst_imbuement.tool.FzzyhammerToolMaterial
import net.minecraft.block.BlockState
import net.minecraft.block.OperatorBlock
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.registry.tag.BlockTags
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World

class FzzyhammerItem(settings: Settings):
    CustomSpellToolItem(FzzyhammerToolMaterial, 1.5f,FzzyhammerToolMaterial.getAttackSpeed().toFloat(), BlockTags.PICKAXE_MINEABLE, settings)

{

    override val fallbackId: Identifier = Identifier(AI.MOD_ID,"hamptertime")

    override fun canAcceptAugment(augment: ScepterAugment): Boolean {
        return augment == RegisterEnchantment.HAMPTERTIME
    }

    override fun postMine(
        stack: ItemStack,
        world: World,
        state: BlockState,
        pos: BlockPos,
        miner: LivingEntity
    ): Boolean {
        if (postHammerMine(stack, world, state, pos, miner)) return true
        val facing = Direction.getEntityFacingOrder(miner)[0]
        val horizontalFacing = miner.horizontalFacing
        if (facing == horizontalFacing){
            val pos1 = pos.offset(facing)
            tryBreakHammerBlock(stack, world, pos1, miner)
            if (postHammerMine(stack, world, state, pos1, miner)) return true
            val pos2 = pos.offset(facing).offset(Direction.DOWN)
            tryBreakHammerBlock(stack, world, pos2, miner)
            if (postHammerMine(stack, world, state, pos2, miner)) return true
            val pos3 = pos.offset(Direction.DOWN)
            tryBreakHammerBlock(stack, world, pos3, miner)
            if (postHammerMine(stack, world, state, pos3, miner)) return true
        } else {
            val pos1 = pos.offset(facing)
            tryBreakHammerBlock(stack, world, pos1, miner)
            if (postHammerMine(stack, world, state, pos1, miner)) return true
            val pos2 = pos.offset(facing).offset(horizontalFacing)
            tryBreakHammerBlock(stack, world, pos2, miner)
            if (postHammerMine(stack, world, state, pos2, miner)) return true
            val pos3 = pos.offset(horizontalFacing)
            tryBreakHammerBlock(stack, world, pos3, miner)
            if (postHammerMine(stack, world, state, pos3, miner)) return true
        }
        return true
    }

    private fun postHammerMine(
        stack: ItemStack,
        world: World,
        state: BlockState,
        pos: BlockPos,
        miner: LivingEntity
    ): Boolean {
        var broken = false
        if (stack.maxDamage - stack.damage > 1) {
            if (!world.isClient && state.getHardness(world, pos) != 0.0f) {
                stack.damage(1, miner) { e: LivingEntity ->
                    broken = true
                    e.sendEquipmentBreakStatus(
                        EquipmentSlot.MAINHAND
                    )
                }
            }
        }
        return broken
    }

    private fun tryBreakHammerBlock(stack: ItemStack, world: World, pos: BlockPos, miner: LivingEntity){
        val blockState = world.getBlockState(pos)
        if (blockState.isAir) {
            return
        }
        val bl1 = !blockState.isToolRequired || stack.isSuitableFor(blockState)
        if(bl1){
            world.removeBlock(pos,false)
            val block = blockState.block
            val blockEntity: BlockEntity? = world.getBlockEntity(pos)
            var bl12 = (miner !is ServerPlayerEntity && (block is OperatorBlock || blockEntity != null))
            bl12 = bl12 || (miner is ServerPlayerEntity && (block is OperatorBlock && !miner.isCreativeLevelTwoOp || miner.isBlockBreakingRestricted(world,pos,miner.interactionManager.gameMode)))
            if(!bl12){
                if (miner is PlayerEntity){
                    block.onBreak(world,pos,blockState,miner)
                }
                val bl2 = world.removeBlock(pos,false)
                if (bl2){
                    block.onBroken(world,pos,blockState)
                    if (miner is PlayerEntity) {
                        block.afterBreak(world,miner,pos,blockState,blockEntity,stack.copy())
                    }
                }
            }
        }
    }

}