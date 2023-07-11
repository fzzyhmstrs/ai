package me.fzzyhmstrs.amethyst_imbuement.spells

import me.fzzyhmstrs.amethyst_core.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_core.augments.base.SummonAugment
import me.fzzyhmstrs.amethyst_core.augments.data.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.augments.paired.AugmentType
import me.fzzyhmstrs.amethyst_core.modifier.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter.SpellType
import me.fzzyhmstrs.amethyst_imbuement.entity.totem.TotemOfGraceEntity
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
class SummonGraceTotemAugment: SummonAugment<TotemOfGraceEntity>(ScepterTier.TWO){
    override val augmentData: AugmentDatapoint
        get() = TODO("Not yet implemented")

    //ml 11
    override val baseEffect: AugmentEffect
        get() = super.baseEffect
            .withDuration(650,50)
            .withDamage(1.8f,0.2f)
            .withRange(6.8,0.2)

    override fun appendDescription(description: MutableList<Text>, other: ScepterAugment, othersType: AugmentType) {
        TODO("Not yet implemented")
    }

    override fun augmentStat(imbueLevel: Int): AugmentDatapoint {
        return AugmentDatapoint(SpellType.GRACE,2400,240,
            14,imbueLevel,50,LoreTier.LOW_TIER, RegisterItem.DAZZLING_MELON_SLICE)
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
        val zrnd: Double = hit.blockPos.z + 0.5
        val yrnd = hit.blockPos.y + 1.0
        val graceEntity = TotemOfGraceEntity(RegisterEntity.TOTEM_OF_GRACE_ENTITY, world,user,effects.duration(level))
        graceEntity.passEffects(effects,level)
        graceEntity.refreshPositionAndAngles(xrnd, yrnd, zrnd,0.0f,0.0f)
        if (world.spawnEntity(graceEntity)){
            successes++
        } else {
            val direction = Direction.Type.HORIZONTAL.random(world.random)
            val newPos = hit.blockPos.offset(direction,1)
            val xrnd2: Double = newPos.x + 0.5
            val zrnd2: Double = newPos.z + 0.5
            val yrnd2 = newPos.y + 1.0
            graceEntity.refreshPositionAndAngles(xrnd2, yrnd2, zrnd2,0.0f,0.0f)
            if (world.spawnEntity(graceEntity)){
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
