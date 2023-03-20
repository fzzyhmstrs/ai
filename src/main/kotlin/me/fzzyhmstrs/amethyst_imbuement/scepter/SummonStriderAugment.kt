package me.fzzyhmstrs.amethyst_imbuement.scepter

import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter_util.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter_util.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter_util.SpellType
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.SummonEntityAugment
import net.minecraft.entity.EntityType
import net.minecraft.entity.passive.StriderEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Items
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.world.World

@Suppress("SpellCheckingInspection")
class SummonStriderAugment: SummonEntityAugment(ScepterTier.TWO,1) {

    override fun augmentStat(imbueLevel: Int): AugmentDatapoint {
        return AugmentDatapoint(SpellType.GRACE,1200,75,
            5,imbueLevel,30,LoreTier.LOW_TIER, Items.SADDLE)
    }

    override fun placeEntity(
        world: World,
        user: PlayerEntity,
        hit: HitResult,
        level: Int,
        effects: AugmentEffect
    ): Boolean {
        var successes = 0
        for(i in 1..level) {
            val startPos = (hit as BlockHitResult).blockPos
            val spawnPos = findSpawnPos(world,startPos,2,2)
            val strider = StriderEntity(EntityType.STRIDER, world)
            strider.setPos(spawnPos.x + 0.5, spawnPos.y + 0.05, spawnPos.z + 0.5)
            strider.saddle(SoundCategory.NEUTRAL)
            if (world.spawnEntity(strider)){
                successes++
            }
        }
        if (successes > 0) {
            return super.placeEntity(world, user, hit, level, effects)
        }
        return false
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.ENTITY_STRIDER_AMBIENT
    }
}