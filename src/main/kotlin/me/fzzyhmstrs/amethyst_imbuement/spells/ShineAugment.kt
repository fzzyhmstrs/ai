package me.fzzyhmstrs.amethyst_imbuement.spells

import eu.pb4.common.protection.api.CommonProtection
import me.fzzyhmstrs.amethyst_core.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_core.augments.data.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.modifier.AugmentConsumer
import me.fzzyhmstrs.amethyst_core.modifier.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter.SpellType
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterBlock
import me.fzzyhmstrs.fzzy_core.raycaster_util.RaycasterUtil
import net.minecraft.block.ShapeContext
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemPlacementContext
import net.minecraft.item.ItemStack
import net.minecraft.registry.tag.FluidTags
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class ShineAugment: ScepterAugment(ScepterTier.ONE,1) {

    override val baseEffect: AugmentEffect
        get() = super.baseEffect.withRange(4.5)

    override fun augmentStat(imbueLevel: Int): AugmentDatapoint {
        return AugmentDatapoint(SpellType.WIT,10,2,
            1,imbueLevel,1,LoreTier.NO_TIER,RegisterBlock.SHINE_LIGHT.asItem())
    }

    override fun applyTasks(world: World, user: LivingEntity, hand: Hand, level: Int, effects: AugmentEffect): Boolean {
        if (user !is ServerPlayerEntity) return false
        val hit = RaycasterUtil.raycastHit(effects.range(level),entity = user)
        if (hit is BlockHitResult && CommonProtection.canPlaceBlock(world,hit.blockPos,user.gameProfile,user)){
            /*val item = RegisterBlock.SHINE_LIGHT.asItem() as BlockItem
            if (!item.place(ItemPlacementContext(user, hand, ItemStack(RegisterBlock.SHINE_LIGHT),hit)).isAccepted) return false*/
            val context = ItemPlacementContext(user, hand, ItemStack(RegisterBlock.SHINE_LIGHT),hit)
            if (!RegisterBlock.SHINE_LIGHT.isEnabled(world.enabledFeatures)) {
                return false
            }
            if (!context.canPlace()) {
                return false
            }
            val blockPos = context.blockPos
            val fluid = world.getFluidState(blockPos)
            val state = RegisterBlock.SHINE_LIGHT.getShineState(fluid.isIn(FluidTags.WATER))
            world.setBlockState(blockPos,state)
            val group = RegisterBlock.SHINE_LIGHT.defaultState.soundGroup
            val sound = group.placeSound
            world.playSound(null,hit.blockPos,sound, SoundCategory.BLOCKS,(group.volume + 1.0f)/2.0f,group.pitch * 0.8f)
            //sendItemPacket(user, stack, hand, hit)
            effects.accept(user, AugmentConsumer.Type.BENEFICIAL)
            return true
        } else {
            var range = effects.range(level)
            do {
                val pos = user.eyePos.subtract(0.0, 0.2, 0.0).add(user.rotationVector.multiply(range))
                val blockPos = BlockPos.ofFloored(pos)
                if (CommonProtection.canPlaceBlock(world,blockPos,user.gameProfile,user)){
                    val fluid = world.getFluidState(blockPos)
                    val state = RegisterBlock.SHINE_LIGHT.getShineState(fluid.isIn(FluidTags.WATER))
                    if (world.canPlayerModifyAt(user,blockPos) && world.getBlockState(blockPos).isReplaceable && world.canPlace(state,blockPos, ShapeContext.of(user)) && state.canPlaceAt(world,blockPos)){
                        world.setBlockState(blockPos,state)
                        val group = state.soundGroup
                        val sound = group.placeSound
                        world.playSound(null,blockPos,sound, SoundCategory.BLOCKS,(group.volume + 1.0f)/2.0f,group.pitch * 0.8f)
                        effects.accept(user, AugmentConsumer.Type.BENEFICIAL)
                        return true
                    }
                }
                range -= 1.0
            }while (range > 0.0)
        }
        return false
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.BLOCK_CANDLE_PLACE
    }
}