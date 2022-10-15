package me.fzzyhmstrs.amethyst_imbuement.scepter

import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter_util.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter_util.SpellType
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.ElementalAugment
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.SummonEntityAugment
import me.fzzyhmstrs.amethyst_imbuement.entity.totem.TotemOfFuryEntity
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
class SummonFuryTotemAugment(tier: Int, maxLvl: Int, vararg slot: EquipmentSlot): SummonEntityAugment(tier, maxLvl, *slot), ElementalAugment {

    override val baseEffect: AugmentEffect
        get() = super.baseEffect.withDuration(750,50).withDamage(2.75f,0.25f).withRange(4.8,0.2)

    override fun placeEntity(
        world: World,
        user: PlayerEntity,
        hit: HitResult,
        level: Int,
        effects: AugmentEffect
    ): Boolean {
        var successes = 0
        val xrnd: Double = (hit as BlockHitResult).blockPos.x + (world.random.nextDouble() * 4.0 - 2.0)
        val zrnd: Double = hit.blockPos.z + (world.random.nextDouble() * 4.0 - 2.0)
        val yrnd = hit.blockPos.y + 1.0
        val chikin = TotemOfFuryEntity(RegisterEntity.TOTEM_OF_FURY_ENTITY, world,user,effects.duration(level))
        chikin.setPos(xrnd, yrnd, zrnd)
        if (world.spawnEntity(chikin)){
            successes++
        }
        if (successes > 0) {
            return super.placeEntity(world, user, hit, level, effects)
        }
        return false
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.ENTITY_CHICKEN_AMBIENT
    }

    override fun augmentStat(imbueLevel: Int): AugmentDatapoint {
        return AugmentDatapoint(SpellType.FURY,2400,100,13,imbueLevel,LoreTier.LOW_TIER, Items.EGG)
    }
}