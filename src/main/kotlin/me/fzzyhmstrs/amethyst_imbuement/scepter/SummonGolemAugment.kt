package me.fzzyhmstrs.amethyst_imbuement.scepter

import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter_util.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter_util.SpellType
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.SummonEntityAugment
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.entity.CrystallineGolemEntity
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEntity
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Items
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.world.World

@Suppress("SpellCheckingInspection")
class SummonGolemAugment(tier: Int, maxLvl: Int, vararg slot: EquipmentSlot): SummonEntityAugment(tier, maxLvl, *slot) {

    override val baseEffect: AugmentEffect
        get() = super.baseEffect.withDuration(AiConfig.entities.crystalGolemSpellBaseLifespan,AiConfig.entities.crystalGolemSpellPerLvlLifespan)
            .withDamage(AiConfig.entities.crystalGolemBaseDamage)

    override fun placeEntity(
        world: World,
        user: PlayerEntity,
        hit: HitResult,
        level: Int,
        effects: AugmentEffect
    ): Boolean {
        val startPos = (hit as BlockHitResult).blockPos
        val spawnPos = findSpawnPos(world,startPos,3,3, tries = 12)
        val golem = CrystallineGolemEntity(RegisterEntity.CRYSTAL_GOLEM_ENTITY, world,effects.duration(level), user)
        golem.setPos(spawnPos.x +0.5, spawnPos.y +0.05, spawnPos.z + 0.5)

        if (world.spawnEntity(golem)) {
            return super.placeEntity(world, user, hit, level, effects)
        }
        return false
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.ENTITY_IRON_GOLEM_REPAIR
    }

    override fun augmentStat(imbueLevel: Int): AugmentDatapoint {
        return AugmentDatapoint(SpellType.WIT,6000,500,22,imbueLevel,LoreTier.HIGH_TIER, Items.AMETHYST_BLOCK)
    }
}