package me.fzzyhmstrs.amethyst_imbuement.spells

import me.fzzyhmstrs.amethyst_core.augments.SummonEntityAugment
import me.fzzyhmstrs.amethyst_core.augments.data.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.modifier.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter.SpellType
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.entity.living.BaseHamsterEntity
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEntity
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterSound
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Items
import net.minecraft.sound.SoundEvent
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.world.World

@Suppress("SpellCheckingInspection")
class SummonHamsterAugment: SummonEntityAugment(ScepterTier.ONE,10) {

    override val baseEffect: AugmentEffect
        get() = super.baseEffect
            .withDuration(AiConfig.entities.hamster.baseLifespan.get())
            .withAmplifier(AiConfig.entities.hamster.baseHealth.get().toInt())
            .withDamage(AiConfig.entities.hamster.baseSummonDamage.get(),AiConfig.entities.hamster.perLvlDamage.get())

    override fun augmentStat(imbueLevel: Int): AugmentDatapoint {
        return AugmentDatapoint(SpellType.WIT,600,50,
            1,imbueLevel,10,LoreTier.NO_TIER, Items.WHITE_WOOL)
    }

    override fun placeEntity(
        world: World,
        user: PlayerEntity,
        hit: HitResult,
        level: Int,
        effects: AugmentEffect
    ): Boolean {
        val startPos = (hit as BlockHitResult).blockPos
        val spawnPos = findSpawnPos(world,startPos,1,1, tries = 12)
        val hampter = BaseHamsterEntity(RegisterEntity.BASIC_HAMSTER_ENTITY, world, effects.duration(level), user, effects, level)
        hampter.setPos(spawnPos.x +0.5, spawnPos.y +0.05, spawnPos.z + 0.5)
        hampter.refreshPositionAndAngles(spawnPos.x +0.5, spawnPos.y +0.005, spawnPos.z + 0.5,(world.random.nextFloat() * 360f) - 180f,user.pitch)
        if (world.spawnEntity(hampter)) {
            return super.placeEntity(world, user, hit, level, effects)
        }
        return false
    }

    override fun soundEvent(): SoundEvent {
        return RegisterSound.HAMSTER_AMBIENT
    }
}
