package me.fzzyhmstrs.amethyst_imbuement.scepter

import me.fzzyhmstrs.amethyst_core.scepter_util.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentConsumer
import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter_util.HealerAugment
import me.fzzyhmstrs.amethyst_core.scepter_util.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter_util.SpellType
import me.fzzyhmstrs.amethyst_imbuement.scepter.base_augments.*
import net.minecraft.entity.Entity
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.passive.GolemEntity
import net.minecraft.entity.passive.PassiveEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Items
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.world.World

class FortifyAugment(tier: Int, maxLvl: Int, vararg slot: EquipmentSlot): MinorSupportAugment(tier,maxLvl, *slot),
    HealerAugment {

    override val baseEffect: AugmentEffect
        get() = super.baseEffect.withDuration(0,400).withAmplifier(-1,1)

    override fun supportEffect(
        world: World,
        target: Entity?,
        user: LivingEntity,
        level: Int,
        effects: AugmentEffect
    ): Boolean {
        if(target != null) {
            if (target is PassiveEntity || target is GolemEntity || target is PlayerEntity) {
                (target as LivingEntity).addStatusEffect(StatusEffectInstance(StatusEffects.RESISTANCE, effects.duration(level), effects.amplifier(level)))
                target.addStatusEffect(StatusEffectInstance(StatusEffects.STRENGTH, effects.duration(level), effects.amplifier(level)))
                effects.accept(target, AugmentConsumer.Type.BENEFICIAL)
                world.playSound(null, target.blockPos, soundEvent(), SoundCategory.PLAYERS, 0.6F, 1.2F)
                return true
            }
        }
        return if (user is PlayerEntity) {
            user.addStatusEffect(StatusEffectInstance(StatusEffects.RESISTANCE, effects.duration(level), effects.amplifier(level)))
            user.addStatusEffect(StatusEffectInstance(StatusEffects.STRENGTH, effects.duration(level), effects.amplifier(level)))
            effects.accept(user, AugmentConsumer.Type.BENEFICIAL)
            world.playSound(null, user.blockPos, soundEvent(), SoundCategory.PLAYERS, 0.6F, 1.2F)
            true
        } else {
            false
        }
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.BLOCK_RESPAWN_ANCHOR_SET_SPAWN
    }

    override fun augmentStat(imbueLevel: Int): AugmentDatapoint {
        return AugmentDatapoint(SpellType.GRACE,1200,25,1,imbueLevel, LoreTier.LOW_TIER, Items.GOLDEN_APPLE)
    }
}