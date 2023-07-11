package me.fzzyhmstrs.amethyst_imbuement.spells

import me.fzzyhmstrs.amethyst_core.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_core.augments.SummonEntityAugment
import me.fzzyhmstrs.amethyst_core.augments.base.SummonAugment
import me.fzzyhmstrs.amethyst_core.augments.data.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.augments.paired.AugmentType
import me.fzzyhmstrs.amethyst_core.modifier.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter.SpellType
import me.fzzyhmstrs.amethyst_imbuement.entity.totem.TotemOfFuryEntity
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEntity
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.Direction
import net.minecraft.world.World

@Suppress("SpellCheckingInspection")
class SummonFuryTotemAugment: SummonAugment<TotemOfFuryEntity>(ScepterTier.TWO) {
    override val augmentData: AugmentDatapoint
        get() = TODO("Not yet implemented")

    //ml 5
    override val baseEffect: AugmentEffect
        get() = super.baseEffect
            .withDuration(750,50)
            .withDamage(2.75f,0.25f)
            .withRange(4.5,0.5)
            .withAmplifier(8,1)

    override fun appendDescription(description: MutableList<Text>, other: ScepterAugment, othersType: AugmentType) {
        TODO("Not yet implemented")
    }

    override fun augmentStat(imbueLevel: Int): AugmentDatapoint {
        return AugmentDatapoint(SpellType.FURY,2400,240,
            14,imbueLevel,50,LoreTier.LOW_TIER, RegisterItem.RESONANT_ROD)
    }

    override fun placeEntity(
        world: World,
        user: PlayerEntity,
        hit: HitResult,
        level: Int,
        effects: AugmentEffect
    ): Boolean {
        var successes = 0
        val xrnd: Double = (hit as BlockHitResult).blockPos.x + 0.5
        val zrnd: Double = hit.blockPos.z.toDouble() + 0.5
        val yrnd = hit.blockPos.y + 1.0
        val furyEntity = TotemOfFuryEntity(RegisterEntity.TOTEM_OF_FURY_ENTITY, world,user,effects.duration(level),effects.amplifier(level)/2)
        furyEntity.passEffects(effects,level)
        furyEntity.refreshPositionAndAngles(xrnd, yrnd, zrnd,0.0f,0.0f)
        if (world.spawnEntity(furyEntity)){
            successes++
        } else {
            val direction = Direction.Type.HORIZONTAL.random(world.random)
            val newPos = hit.blockPos.offset(direction,1)
            val xrnd2: Double = newPos.x + 0.5
            val zrnd2: Double = newPos.z + 0.5
            val yrnd2 = newPos.y + 1.0
            furyEntity.refreshPositionAndAngles(xrnd2, yrnd2, zrnd2,0.0f,0.0f)
            if (world.spawnEntity(furyEntity)){
                successes++
            }
        }
        if (successes > 0) {
            return super.placeEntity(world, user, hit, level, effects)
        }
        return false
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.BLOCK_DEEPSLATE_BRICKS_PLACE
    }
}
