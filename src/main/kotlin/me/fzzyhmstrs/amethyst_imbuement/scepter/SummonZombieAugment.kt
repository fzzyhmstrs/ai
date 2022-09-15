@file:Suppress("SpellCheckingInspection")

package me.fzzyhmstrs.amethyst_imbuement.scepter

import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter_util.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter_util.SpellType
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.SummonEntityAugment
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.entity.UnhallowedEntity
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEntity
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Items
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.world.World
import kotlin.math.max
import kotlin.math.min

class SummonZombieAugment(tier: Int, maxLvl: Int, vararg slot: EquipmentSlot): SummonEntityAugment(tier, maxLvl, *slot) {

    override val baseEffect: AugmentEffect
        get() = super.baseEffect.withAmplifier(3,0,0)
            .withDuration(AiConfig.entities.unhallowedBaseLifespan,0,0)
            .withDamage(AiConfig.entities.unhallowedBaseDamage)

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
            val xrnd: Double = (hit as BlockHitResult).blockPos.x + (world.random.nextDouble() * 4.0 - 2.0)
            val zrnd: Double = (hit).blockPos.z + (world.random.nextDouble() * 4.0 - 2.0)
            val yrnd = hit.blockPos.y + 1.0
            val zomAmplifier = max(effects.amplifier(level) - baseEffect.amplifier(level), 0)
            val zom = UnhallowedEntity(RegisterEntity.UNHALLOWED_ENTITY, world,effects.duration(level),user, bonus, effects.damage(level).toDouble(), 4.0 * zomAmplifier)
            zom.setPos(xrnd, yrnd, zrnd)
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

    override fun augmentStat(imbueLevel: Int): AugmentDatapoint {
        return AugmentDatapoint(SpellType.WIT,1200,80,5,imbueLevel,LoreTier.LOW_TIER, Items.ROTTEN_FLESH)
    }
}