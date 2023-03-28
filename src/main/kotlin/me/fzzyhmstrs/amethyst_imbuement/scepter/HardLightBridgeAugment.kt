package me.fzzyhmstrs.amethyst_imbuement.scepter

import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter_util.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter_util.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter_util.SpellType
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.MiscAugment
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterBlock
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.Direction
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
        var successes = 0
        val dir = user.horizontalFacing
        val pos = user.blockPos.add(0,-1,0)
        var xMod = 0
        var zMod = 0
        when (dir){
            Direction.WEST->{
                xMod = -1
            }
            Direction.EAST->{
                xMod = 1
            }
            Direction.NORTH->{
                zMod = -1
            }
            Direction.SOUTH->{
                zMod = 1
            }
            else->{return false}
        }
        for (i in 1..effect.range(level).toInt()){
            val pos2 = pos.add(i * xMod,0,i * zMod)
            if (world.isAir(pos2)){
                world.setBlockState(pos2,RegisterBlock.HARD_LIGHT_BLOCK.defaultState)
                successes++
            }
        }
        if (successes > 0) world.playSound(null, user.blockPos, soundEvent(), SoundCategory.PLAYERS, 1.0F, 1.0F)
        return successes > 0
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.BLOCK_GLASS_PLACE
    }
}