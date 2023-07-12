package me.fzzyhmstrs.amethyst_imbuement.spells.pieces

import eu.pb4.common.protection.api.CommonProtection
import me.fzzyhmstrs.amethyst_core.augments.AugmentHelper
import me.fzzyhmstrs.amethyst_core.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_core.augments.SpellActionResult
import me.fzzyhmstrs.amethyst_core.augments.paired.AugmentType
import me.fzzyhmstrs.amethyst_core.augments.paired.PairedAugments
import me.fzzyhmstrs.amethyst_core.augments.paired.ProcessContext
import me.fzzyhmstrs.amethyst_core.interfaces.SpellCastingEntity
import me.fzzyhmstrs.amethyst_core.modifier.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter.ScepterTier
import me.fzzyhmstrs.fzzy_core.entity_util.PlayerCreatable
import me.fzzyhmstrs.fzzy_core.raycaster_util.RaycasterUtil
import net.minecraft.block.BlockState
import net.minecraft.block.ShapeContext
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.Tameable
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World

abstract class PlaceBlockAugment(tier: ScepterTier, type: AugmentType = AugmentType.BLOCK_TARGET): ScepterAugment(tier,type) {

    override val baseEffect: AugmentEffect
        get() = super.baseEffect.withRange(4.5)

    override fun <T> applyTasks(
        world: World,
        context: ProcessContext,
        user: T,
        hand: Hand,
        level: Int,
        effects: AugmentEffect,
        spells: PairedAugments
    )
            :
            SpellActionResult
            where
            T : SpellCastingEntity,
            T : LivingEntity
    {
        val onCastResults = spells.processOnCast(context,world,null,user, hand, level, effects)
        if (!onCastResults.success()) return  FAIL
        if (onCastResults.overwrite()) return onCastResults
        val owner = if (user is ServerPlayerEntity){
            user
        } else if (user is Tameable && user.owner is ServerPlayerEntity) {
            user.owner as ServerPlayerEntity
        }else if (user is PlayerCreatable && user.entityOwner is ServerPlayerEntity){
            user.entityOwner as ServerPlayerEntity
        } else {
            return FAIL
        }
        val hit = RaycasterUtil.raycastHit(effects.range(level),entity = user)
        if (hit is BlockHitResult && CommonProtection.canPlaceBlock(world,hit.blockPos,owner.gameProfile,owner)){
            val list = spells.processSingleBlockHit(hit,context,world,null,user,hand, level, effects)
            return if (list.isEmpty()) FAIL else SpellActionResult.success(onCastResults).withResults()
        } else {
            var range = effects.range(level)
            do {
                val pos = user.eyePos.subtract(0.0, 0.2, 0.0).add(user.rotationVec3d.multiply(range))
                val blockPos = BlockPos.ofFloored(pos)
                if (CommonProtection.canPlaceBlock(world,blockPos,owner.gameProfile,owner)){
                    //val fluid = world.getFluidState(blockPos)
                    val state = getBlockStateToPlace(context,world,blockPos,spells)
                    if (world.canPlayerModifyAt(owner,blockPos) && world.getBlockState(blockPos).isReplaceable && world.canPlace(state,blockPos, ShapeContext.of(user)) && state.canPlaceAt(world,blockPos)){
                        val list = spells.processSingleBlockHit(BlockHitResult(pos,Direction.UP,blockPos,true),context,world,null,user,hand, level, effects)
                        return if (list.isEmpty()) FAIL else SpellActionResult.success(onCastResults).withResults()
                    }
                }
                range -= 1.0
            }while (range > 0.0)
        }
        return FAIL
    }

    override fun <T> onBlockHit(
        blockHitResult: BlockHitResult,
        context: ProcessContext,
        world: World,
        source: Entity?,
        user: T,
        hand: Hand,
        level: Int,
        effects: AugmentEffect,
        othersType: AugmentType,
        spells: PairedAugments
    )
    :
    SpellActionResult
    where
    T : SpellCastingEntity,
    T : LivingEntity
    {
        if (othersType.empty) {
            val blockPos = if (world.getBlockState(blockHitResult.blockPos).isReplaceable) {
                blockHitResult.blockPos
            } else {
                blockHitResult.blockPos.offset(blockHitResult.side)
            }
            val state = getBlockStateToPlace(context, world, blockPos, spells)
            return if (world.setBlockState(blockPos, state)) {
                spells.hitSoundEvents(world, blockPos, context)
                SpellActionResult.success(AugmentHelper.BLOCK_PLACED)
            } else {
                FAIL
            }
        }
        return SUCCESSFUL_PASS
    }

    abstract fun getBlockStateToPlace(context: ProcessContext,world: World,pos: BlockPos, spells: PairedAugments): BlockState

    override fun hitSoundEvent(world: World, blockPos: BlockPos, context: ProcessContext) {
        val state = world.getBlockState(blockPos)
        val group = state.soundGroup
        val sound = group.placeSound
        world.playSound(null,blockPos,sound, SoundCategory.BLOCKS,(group.volume + 1.0f)/2.0f,group.pitch * 0.8f)
    }

}