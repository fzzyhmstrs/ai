package me.fzzyhmstrs.amethyst_imbuement.spells

import eu.pb4.common.protection.api.CommonProtection
import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentConsumer
import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter_util.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter_util.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter_util.SpellType
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.MiscAugment
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterBlock
import net.minecraft.block.ShapeContext
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class HardLightBridgeAugment: MiscAugment(ScepterTier.TWO,11){

    override val baseEffect: AugmentEffect
        get() = super.baseEffect.withRange(7.8,0.2,0.0)

    override fun augmentStat(imbueLevel: Int): AugmentDatapoint {
        return AugmentDatapoint(SpellType.WIT,8,2,
            9,imbueLevel,1,LoreTier.LOW_TIER, RegisterBlock.HARD_LIGHT_BLOCK.asItem())
    }

    override fun effect(
        world: World,
        target: Entity?,
        user: LivingEntity,
        level: Int,
        hit: HitResult?,
        effect: AugmentEffect
    ): Boolean {
        if (user !is ServerPlayerEntity) return false
        var successes = 0
        var range = effect.range(level)
        do {
            val pos = user.pos.subtract(0.0, 0.5, 0.0).add(user.rotationVector.multiply(range))
            val blockPos = BlockPos.ofFloored(pos)
            if (CommonProtection.canPlaceBlock(world,blockPos,user.gameProfile,user)){
                val state = RegisterBlock.HARD_LIGHT_BLOCK.getHardLightState()
                if (world.canPlayerModifyAt(user,blockPos) && world.getBlockState(blockPos).isReplaceable && world.canPlace(state,blockPos, ShapeContext.of(user)) && state.canPlaceAt(world,blockPos)){
                    world.setBlockState(blockPos,state)
                    if (AiConfig.blocks.isBridgeBlockTemporary()){
                        world.scheduleBlockTick(blockPos, RegisterBlock.HARD_LIGHT_BLOCK, AiConfig.blocks.hardLight.temporaryDuration.get())
                    }
                    successes++
                }
            }
            range -= 1.0
        }while (range > 0.0)
        if (successes > 0) {
            effect.accept(user, AugmentConsumer.Type.BENEFICIAL)
            world.playSound(null, user.blockPos, soundEvent(), SoundCategory.PLAYERS, 1.0F, 1.0F)
        }
        return successes > 0
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.BLOCK_GLASS_PLACE
    }
}