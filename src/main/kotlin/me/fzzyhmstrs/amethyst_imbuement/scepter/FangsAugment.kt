package me.fzzyhmstrs.amethyst_imbuement.scepter

import me.fzzyhmstrs.amethyst_imbuement.entity.PlayerFangsEntity
import me.fzzyhmstrs.amethyst_imbuement.scepter.base_augments.AugmentEffect
import me.fzzyhmstrs.amethyst_imbuement.scepter.base_augments.MiscAugment
import me.fzzyhmstrs.amethyst_imbuement.util.LoreTier
import me.fzzyhmstrs.amethyst_imbuement.util.SpellType
import net.minecraft.block.BlockState
import net.minecraft.entity.*
import net.minecraft.entity.mob.EvokerFangsEntity
import net.minecraft.item.Items
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.MathHelper
import net.minecraft.util.shape.VoxelShape
import net.minecraft.world.World
import kotlin.math.max
import kotlin.math.min

class FangsAugment(tier: Int, maxLvl: Int, vararg slot: EquipmentSlot): MiscAugment(tier, maxLvl, *slot) {

    override val baseEffect: AugmentEffect
        get() = super.baseEffect.withAmplifier(15,0,0).withDamage(6.0F)

    override fun effect(
        world: World,
        target: Entity?,
        user: LivingEntity,
        level: Int,
        hit: HitResult?,
        effect: AugmentEffect
    ): Boolean {
        var successes = 0
        val d: Double
        val e: Double
        if (target != null){
            d = min(target.y, user.y)
            e = max(target.y, user.y) + 1.0
        } else {
            d = user.y
            e = user.y + 1.0
        }
        val f = (user.yaw + 90) * MathHelper.PI / 180
        for (i in 0..effect.amplifier(level)) {
            val g = 1.25 * (i + 1).toDouble()
            val success = PlayerFangsEntity.conjureFangs(
                world,
                user,
                user.x + MathHelper.cos(f).toDouble() * g,
                user.z + MathHelper.sin(f).toDouble() * g,
                d,
                e,
                f,
                i,
                effect,
                level
            )
            if (success) successes++
        }
        return successes > 0
    }

    override fun augmentStat(imbueLevel: Int): ScepterObject.AugmentDatapoint {
        return ScepterObject.AugmentDatapoint(SpellType.FURY,34,10,8,imbueLevel,LoreTier.LOW_TIER, Items.EMERALD)
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.ENTITY_EVOKER_FANGS_ATTACK
    }
}