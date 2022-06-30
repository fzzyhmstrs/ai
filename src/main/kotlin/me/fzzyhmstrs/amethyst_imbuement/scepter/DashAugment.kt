package me.fzzyhmstrs.amethyst_imbuement.scepter

import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter_util.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter_util.SpellType
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.MiscAugment
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.OceanicAugment
import me.fzzyhmstrs.amethyst_imbuement.AI
import net.minecraft.entity.Entity
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.MovementType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Items
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.Hand
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World

class DashAugment(tier: Int, maxLvl: Int, vararg slot: EquipmentSlot): MiscAugment(tier,maxLvl, *slot), OceanicAugment {

    override val baseEffect: AugmentEffect
        get() = super.baseEffect.withAmplifier(1,1).withDuration(20)

    override fun effect(
        world: World,
        target: Entity?,
        user: LivingEntity,
        level: Int,
        hit: HitResult?,
        effect: AugmentEffect
    ): Boolean {
        if (user !is PlayerEntity) return false
        val y: Float = user.getYaw()
        val p: Float = user.getPitch()
        var g =
            -MathHelper.sin(y * (Math.PI.toFloat() / 180)) * MathHelper.cos(p * (Math.PI.toFloat() / 180))
        var h = -MathHelper.sin(p * (Math.PI.toFloat() / 180))
        var k =
            MathHelper.cos(y * (Math.PI.toFloat() / 180)) * MathHelper.cos(p * (Math.PI.toFloat() / 180))
        val l = MathHelper.sqrt(g * g + h * h + k * k)
        val m: Float = 3.0f * (effect.amplifier(level).toFloat() / 4.0f)

        g *= m / l
        h *= m / l
        k *= m / l
        user.addVelocity(g.toDouble(),h.toDouble(),k.toDouble())
        user.useRiptide(effect.duration(level))

        if (user.isOnGround()) {
            user.move(MovementType.SELF, Vec3d(0.0, 1.1999999284744263, 0.0))
        }
        world.playSoundFromEntity(null,user,soundEvent(),SoundCategory.PLAYERS,1.0F,1.0F)
        return true
    }

    override fun clientTask(world: World, user: LivingEntity, hand: Hand, level: Int) {
        effect(world, null, user, level, null, baseEffect)
    }

    override fun augmentStat(imbueLevel: Int): AugmentDatapoint {
        return AugmentDatapoint(SpellType.WIT,32,10,4,imbueLevel, LoreTier.LOW_TIER, Items.SUGAR)
    }

    override fun soundEvent(): SoundEvent {
        return when(AI.aiRandom().nextInt(3)){
            0-> SoundEvents.ITEM_TRIDENT_RIPTIDE_1
            1-> SoundEvents.ITEM_TRIDENT_RIPTIDE_2
            2-> SoundEvents.ITEM_TRIDENT_RIPTIDE_3
            else-> SoundEvents.ITEM_TRIDENT_RIPTIDE_1
        }

    }
}