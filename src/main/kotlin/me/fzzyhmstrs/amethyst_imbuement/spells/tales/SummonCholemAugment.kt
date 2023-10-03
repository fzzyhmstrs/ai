@file:Suppress("SpellCheckingInspection")

package me.fzzyhmstrs.amethyst_imbuement.spells.tales

import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter_util.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter_util.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter_util.SpellType
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.SummonEntityAugment
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.entity.living.UnhallowedEntity
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

class SummonCholemAugment: SummonEntityAugment(ScepterTier.THREE,9) {

    override val baseEffect: AugmentEffect
        get() = super.baseEffect
            .withAmplifier(AiConfig.entities.cholem.baseHealth.get().toInt(),0,0)
            .withDuration(AiConfig.entities.cholem.baseLifespan.get(),0,0)
            .withDamage(AiConfig.entities.cholem.baseDamage.get())
            .withRange(1.75,0.25)

    override fun augmentStat(imbueLevel: Int): AugmentDatapoint {
        return AugmentDatapoint(SpellType.WIT, 3000, 350,
            25,imbueLevel,55,BookOfTalesItem.TALES_TIER, Items.COOKED_CHICKEN)
    }

    override fun placeEntity(
        world: World,
        user: PlayerEntity,
        hit: HitResult,
        level: Int,
        effects: AugmentEffect
    ): Boolean {
        var successes = 0
        for(i in 1..effects.range(level).toInt()) {
            val startPos = (hit as BlockHitResult).blockPos
            val spawnPos = findSpawnPos(world,startPos,3,2)
            if (spawnPos == BlockPos.ORIGIN) continue

            val cholem = CholemEntity(RegisterEntity.CHOLEM_ENTITY, world,effects.duration(level), user, effects, level)
            cholem.refreshPositionAndAngles(spawnPos.x +0.5, spawnPos.y + 0.05, spawnPos.z + 0.5, user.yaw, user.pitch)
            if (world.spawnEntity(cholem)){
                successes++
            }
        }
        if (successes > 0) {
            return super.placeEntity(world, user, hit, level, effects)
        }
        return false
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.ENTITY_CHICKEN_AMBIENT
    }
}
