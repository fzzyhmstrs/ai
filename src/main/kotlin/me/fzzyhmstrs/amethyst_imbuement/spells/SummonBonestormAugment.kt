package me.fzzyhmstrs.amethyst_imbuement.spells

import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter_util.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter_util.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter_util.SpellType
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.SummonEntityAugment
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.entity.living.BonestormEntity
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Items
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class SummonBonestormAugment: SummonEntityAugment(ScepterTier.TWO,12){

    override val baseEffect: AugmentEffect = super.baseEffect
        .withDamage(AiConfig.entities.bonestorm.baseDamage.get(),AiConfig.entities.bonestorm.perLvlDamage.get())
        .withAmplifier(AiConfig.entities.bonestorm.baseHealth.get().toInt(),0,0)
        .withDuration(AiConfig.entities.bonestorm.baseLifespan.get(),AiConfig.entities.bonestorm.perLvlLifespan.get(),0)
            

    override fun augmentStat(imbueLevel: Int): AugmentDatapoint {
        return AugmentDatapoint(SpellType.FURY,2400,320,
            15,imbueLevel,75,LoreTier.LOW_TIER, Items.BONE_BLOCK)
    }

    override fun placeEntity(
        world: World,
        user: PlayerEntity,
        hit: HitResult,
        level: Int,
        effects: AugmentEffect
    ): Boolean {
        var successes = 0
        val startPos = (hit as BlockHitResult).blockPos
        val spawnPos = findSpawnPos(world,startPos,3,2)
        if (spawnPos == BlockPos.ORIGIN) return false

        val zom = BonestormEntity(RegisterEntity.BONESTORM_ENTITY, world,effects.duration(level), user, effects, level)
        zom.refreshPositionAndAngles(spawnPos.x +0.5, spawnPos.y + 0.05, spawnPos.z + 0.5, user.yaw, user.pitch)
        if (world.spawnEntity(zom)){
            successes++
        }
        return successes > 0
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.ENTITY_WITHER_SKELETON_HURT
    }


}
