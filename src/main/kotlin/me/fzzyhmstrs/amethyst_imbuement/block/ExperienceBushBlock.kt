package me.fzzyhmstrs.amethyst_imbuement.block

import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import net.minecraft.block.BlockState
import net.minecraft.block.SweetBerryBushBlock
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.random.Random
import net.minecraft.world.BlockView
import net.minecraft.world.World

class ExperienceBushBlock(settings: Settings):SweetBerryBushBlock(settings) {

    @Deprecated("Deprecated in Java")
    override fun onEntityCollision(state: BlockState, world: World, pos: BlockPos, entity: Entity) {
        if (entity !is LivingEntity || entity.getType() === EntityType.FOX || entity.getType() === EntityType.BEE) {
            return
        }
        entity.slowMovement(state, Vec3d(0.9, 0.85, 0.9))
    }

    override fun getPickStack(world: BlockView?, pos: BlockPos?, state: BlockState?): ItemStack {
        return ItemStack(RegisterItem.XP_BUSH_SEED)
    }

    @Deprecated("Deprecated in Java")
    override fun randomTick(state: BlockState, world: ServerWorld, pos: BlockPos, random: Random) {
        val i = state.get(AGE)
        if (i < 3 && random.nextInt(6) == 0 && world.getBaseLightLevel(pos.up(), 0) >= 9) {
            world.setBlockState(pos, state.with(AGE, i + 1) as BlockState, NOTIFY_LISTENERS)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onUse(
        state: BlockState,
        world: World,
        pos: BlockPos,
        player: PlayerEntity,
        hand: Hand,
        hit: BlockHitResult
    ): ActionResult? {
        val bl: Boolean
        val i = state.get(AGE)
        bl = i == 3
        if (!bl && player.getStackInHand(hand).isOf(Items.BONE_MEAL)) {
            return ActionResult.PASS
        }
        if (i > 1) {
            //val j = 1 + world.random.nextInt(9)
            dropStack(world, pos, ItemStack (Items.EXPERIENCE_BOTTLE, if (bl) 2 else 1))
            //ExperienceOrbEntity.spawn(world as ServerWorld, Vec3d.ofCenter(pos), j + if (bl) 2 else 0)
            world.playSound(
                null,
                pos,
                SoundEvents.BLOCK_BREWING_STAND_BREW,
                SoundCategory.BLOCKS,
                0.7f,
                1.0f + world.random.nextFloat() * 0.2f
            )
            world.playSound(
                null,
                pos,
                SoundEvents.BLOCK_SWEET_BERRY_BUSH_PICK_BERRIES,
                SoundCategory.BLOCKS,
                1.0f,
                0.8f + world.random.nextFloat() * 0.4f
            )
            world.setBlockState(pos, state.with(AGE, 1) as BlockState, NOTIFY_LISTENERS)
            return ActionResult.success(world.isClient)
        }
        return ActionResult.PASS
    }

    override fun grow(world: ServerWorld, random: Random, pos: BlockPos, state: BlockState) {
        if (random.nextFloat() > 0.6) {
            val i = 3.coerceAtMost(state.get(AGE) + 1)
            world.setBlockState(pos, state.with(AGE, i) as BlockState, NOTIFY_LISTENERS)
        }
    }
}