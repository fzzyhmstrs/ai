package me.fzzyhmstrs.amethyst_imbuement.scepter

import me.fzzyhmstrs.amethyst_core.modifier.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter.SpellType
import me.fzzyhmstrs.amethyst_core.augments.data.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.augments.SummonEntityAugment
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.entity.living.CrystallineGolemEntity
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Items
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

@Suppress("SpellCheckingInspection")
class SummonGolemAugment: SummonEntityAugment(ScepterTier.THREE,5) {

    override val baseEffect: AugmentEffect
        get() = super.baseEffect
            .withDuration(AiConfig.entities.crystalGolem.spellBaseLifespan.get(),AiConfig.entities.crystalGolem.spellPerLvlLifespan.get())
            .withAmplifier(AiConfig.entities.crystalGolem.baseHealth.get().toInt())
            .withDamage(AiConfig.entities.crystalGolem.baseDamage.get())

    override fun augmentStat(imbueLevel: Int): AugmentDatapoint {
        return AugmentDatapoint(SpellType.WIT,6000,600,
            25,imbueLevel,100,LoreTier.HIGH_TIER, Items.AMETHYST_BLOCK)
    }

    override fun placeEntity(
        world: World,
        user: PlayerEntity,
        hit: HitResult,
        level: Int,
        effects: AugmentEffect
    ): Boolean {
        val startPos = (hit as BlockHitResult).blockPos
        val spawnPos = findSpawnPos(world,startPos,3,3, tries = 12)
        if (spawnPos == BlockPos.ORIGIN) return false
        val golem = CrystallineGolemEntity(RegisterEntity.CRYSTAL_GOLEM_ENTITY, world,effects.duration(level), user, effects)
        golem.setConstructOwner(user)
        golem.setPos(spawnPos.x +0.5, spawnPos.y +0.05, spawnPos.z + 0.5)
        golem.refreshPositionAndAngles(spawnPos.x +0.5, spawnPos.y +0.05, spawnPos.z + 0.5,(world.random.nextFloat() * 360f) - 180f,user.pitch)
        if (world.spawnEntity(golem)) {
            return super.placeEntity(world, user, hit, level, effects)
        }
        return false
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.ENTITY_IRON_GOLEM_REPAIR
    }
}