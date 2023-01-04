package me.fzzyhmstrs.amethyst_imbuement.scepter

import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter_util.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter_util.SpellType
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.BuilderAugment
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.MiscAugment
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.TravelerAugment
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterBlock
import net.minecraft.entity.Entity
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.item.Items
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.Direction
import net.minecraft.world.World

class HardLightBridgeAugment(tier: Int, maxLvl: Int, vararg slot: EquipmentSlot): MiscAugment(tier,maxLvl, *slot){

    override val baseEffect: AugmentEffect
        get() = super.baseEffect.withRange(8.0,0.0,0.0)

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

    override fun augmentStat(imbueLevel: Int): AugmentDatapoint {
        return AugmentDatapoint(SpellType.WIT,5,1,5,imbueLevel,LoreTier.LOW_TIER, Items.CRAFTING_TABLE)
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.BLOCK_GLASS_PLACE
    }
}