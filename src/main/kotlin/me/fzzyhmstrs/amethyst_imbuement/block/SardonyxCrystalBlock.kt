package me.fzzyhmstrs.amethyst_imbuement.block

import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.entity.block.PlanarDoorBlockEntity
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEntity
import me.fzzyhmstrs.amethyst_imbuement.spells.tales.PlanarDoorAugment
import net.minecraft.block.*
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.entity.Entity
import net.minecraft.entity.ItemEntity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.passive.AnimalEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.vehicle.MinecartEntity
import net.minecraft.fluid.FluidState
import net.minecraft.fluid.Fluids
import net.minecraft.item.ItemPlacementContext
import net.minecraft.item.ItemStack
import net.minecraft.registry.tag.FluidTags
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.state.StateManager
import net.minecraft.state.property.BooleanProperty
import net.minecraft.state.property.Properties
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView
import net.minecraft.world.World
import net.minecraft.world.WorldAccess

class SardonyxCrystlBlock(settings:Settings):Block(settings) {

    /*override fun createBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return PlanarDoorBlockEntity(RegisterEntity.PLANAR_DOOR_BLOCK_ENTITY, pos, state)
    }*/

    /*override fun <T : BlockEntity> getTicker(
        world: World,
        state: BlockState,
        type: BlockEntityType<T>
    ): BlockEntityTicker<T>? {
        return if (!world.isClient) checkType(
            type, RegisterEntity.PLANAR_DOOR_BLOCK_ENTITY
        ) { wrld: World, pos: BlockPos, state2: BlockState, blockEntity: PlanarDoorBlockEntity ->
            PlanarDoorBlockEntity.tick(
                wrld,
                pos,
                state2,
                blockEntity
            )
        } else null
    }*/

    @Deprecated("Deprecated in Java")
    override fun onStateReplaced(
        state: BlockState,
        world: World,
        pos: BlockPos?,
        newState: BlockState,
        moved: Boolean
    ) {
        if (state.isOf(newState.block)) {
            return
        }
        //summon SardonyxElemental here
        super.onStateReplaced(state, world, pos, newState, moved)
    }

    override fun randomDisplayTick(state: BlockState?, world: World, pos: BlockPos, random: Random) {
        if (world.random.nextFloat() < 0.9f) return
        val player = MinecraftClient.getInstance().player ?: return
        val message = AcText.translatable("block.amethyst_imbuement.sardonyx_crystal.message${world.random.nextInt(5)}")
        player.sendMessage(message, true)
        //sned random messages to player
    }
}
