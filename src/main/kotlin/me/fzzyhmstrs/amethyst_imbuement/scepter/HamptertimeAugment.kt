@file:Suppress("SpellCheckingInspection")

package me.fzzyhmstrs.amethyst_imbuement.scepter

import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter_util.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter_util.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter_util.SpellType
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.SummonEntityAugment
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.entity.UnhallowedEntity
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEntity
import me.fzzyhmstrs.fzzy_core.coding_util.PerLvlI
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Items
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import kotlin.math.max
import kotlin.math.min

class HamptertimeAugment: SummonEntityAugment(ScepterTier.THREE,25) {

    override val baseEffect: AugmentEffect
        get() = super.baseEffect.withAmplifier(3,0,0)
            .withDuration(AiConfig.entities.unhallowed.baseLifespan.get(),0,0)
            .withDamage(AiConfig.entities.unhallowed.baseDamage.get())

    override fun augmentStat(imbueLevel: Int): AugmentDatapoint {
        return AugmentDatapoint(SpellType.WIT, PerLvlI(1295,-15),150,
            11,imbueLevel,40,LoreTier.LOW_TIER, Items.ROTTEN_FLESH)
    }

    override fun placeEntity(
        world: World,
        user: PlayerEntity,
        hit: HitResult,
        level: Int,
        effects: AugmentEffect
    ): Boolean {
        val bonus = bonus(level)
        var successes = 0
        for(i in 1..max(min(effects.amplifier(level),level/2),1)) {
            val startPos = (hit as BlockHitResult).blockPos
            val spawnPos = findSpawnPos(world,startPos,3,2)
            if (spawnPos == BlockPos.ORIGIN) continue

            val zomAmplifier = max(effects.amplifier(level) - baseEffect.amplifier(level), 0)
            val zom = UnhallowedEntity(RegisterEntity.UNHALLOWED_ENTITY, world,effects.duration(level),user, bonus, effects.damage(level).toDouble(), 4.0 * zomAmplifier)
            zom.refreshPositionAndAngles(spawnPos.x +0.5, spawnPos.y + 0.05, spawnPos.z + 0.5, user.yaw, user.pitch)
            if (world.spawnEntity(zom)){
                successes++
            }
        }
        if (successes > 0) {
            return super.placeEntity(world, user, hit, level, effects)
        }
        return false
    }

    private fun bonus(level: Int): Int{
        return if (level <= 5){
            0
        } else if (level <= 8){
            1
        } else if (level <= 10){
            2
        } else if (level <= 12){
            3
        } else {
            4
        }
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.ENTITY_ZOMBIE_AMBIENT
    }
}