package me.fzzyhmstrs.amethyst_imbuement.spells

import eu.pb4.common.protection.api.CommonProtection
import me.fzzyhmstrs.amethyst_core.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_core.augments.data.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.modifier.AugmentConsumer
import me.fzzyhmstrs.amethyst_core.modifier.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter.SpellType
import me.fzzyhmstrs.fzzy_core.raycaster_util.RaycasterUtil
import net.fabricmc.fabric.api.mininglevel.v1.MiningLevelManager
import net.minecraft.block.AbstractFireBlock
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.fluid.FluidState
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.WorldEvents
import net.minecraft.world.event.GameEvent

class ExcavateAugment: ScepterAugment(ScepterTier.ONE,25) {

    override val baseEffect: AugmentEffect
        get() = super.baseEffect.withRange(4.5)

    override fun augmentStat(imbueLevel: Int): AugmentDatapoint {
        return AugmentDatapoint(SpellType.WIT, 8,6,
            1,imbueLevel,1, LoreTier.LOW_TIER, Items.IRON_SHOVEL)
    }

    override fun applyTasks(world: World, user: LivingEntity, hand: Hand, level: Int, effects: AugmentEffect): Boolean {
        if (user !is ServerPlayerEntity) return false
        val hit = RaycasterUtil.raycastHit(effects.range(level),entity = user)
        if (hit is BlockHitResult  && CommonProtection.canBreakBlock(world,hit.blockPos,user.gameProfile,user)){
            val state = world.getBlockState(hit.blockPos)
            if (state.getHardness(world,hit.blockPos) == -1.0f) return false
            if (canBreak(state, level)) {
                world.breakBlock(hit.blockPos,true,user)
                val group = state.soundGroup
                val sound = group.breakSound
                world.playSound(
                    null,
                    hit.blockPos,
                    sound,
                    SoundCategory.BLOCKS,
                    (group.volume + 1.0f) / 2.0f,
                    group.pitch * 0.8f
                )
                //sendItemPacket(user, stack, hand, hit)
                effects.accept(user, AugmentConsumer.Type.BENEFICIAL)
            }
            return true
        }
        return false
    }

    private fun canBreak(state: BlockState, level: Int): Boolean{
        val miningLevel = if(level < 10){
            1
        } else if (level < 15){
            2
        }else if (level < 20){
            3
        } else {
            4
        }
        val requiredLevel = MiningLevelManager.getRequiredMiningLevel(state)
        return miningLevel >= requiredLevel
    }

    fun breakBlock(world: World,pos: BlockPos, drop: Boolean, breakingEntity: Entity): Boolean {
        var bl: Boolean
        val blockState: BlockState = world.getBlockState(pos)
        if (blockState.isAir) {
            return false
        }
        val fluidState: FluidState = world.getFluidState(pos)
        if (blockState.block !is AbstractFireBlock) {
            world.syncWorldEvent(WorldEvents.BLOCK_BROKEN, pos, Block.getRawIdFromState(blockState))
        }
        if (drop) {
            val blockEntity: BlockEntity? = if (blockState.hasBlockEntity()) world.getBlockEntity(pos) else null
            Block.dropStacks(blockState, world, pos, blockEntity, breakingEntity, ItemStack.EMPTY)
        }
        if (world.setBlockState(pos, fluidState.blockState, Block.NOTIFY_ALL, 512).also {
                bl = it
            }) {
            world.emitGameEvent(GameEvent.BLOCK_DESTROY, pos, GameEvent.Emitter.of(breakingEntity, blockState))
        }
        return bl
    }
}