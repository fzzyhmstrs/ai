package me.fzzyhmstrs.amethyst_imbuement.scepter

import me.fzzyhmstrs.amethyst_core.augments.MinorSupportAugment
import me.fzzyhmstrs.amethyst_core.augments.data.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.interfaces.SpellCastingEntity
import me.fzzyhmstrs.amethyst_core.modifier.AugmentConsumer
import me.fzzyhmstrs.amethyst_core.modifier.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter.SpellType
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.fzzy_core.coding_util.PerLvlI
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.passive.GolemEntity
import net.minecraft.entity.passive.PassiveEntity
import net.minecraft.item.Items
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.world.World
import kotlin.math.max

class FortifyAugment: MinorSupportAugment(ScepterTier.TWO,11){

    override val baseEffect: AugmentEffect
        get() = super.baseEffect.withDuration(210,90).withAmplifier(-1,1)

    override fun augmentStat(imbueLevel: Int): AugmentDatapoint {
        return AugmentDatapoint(SpellType.GRACE, PerLvlI(1320,-20),135,
            13,imbueLevel,20, LoreTier.LOW_TIER, Items.GOLDEN_APPLE)
    }

    override fun supportEffect(
        world: World,
        target: Entity?,
        user: LivingEntity,
        level: Int,
        effects: AugmentEffect
    ): Boolean {
        if(target != null) {
            if ((target is PassiveEntity || target is GolemEntity || target is SpellCastingEntity && AiConfig.entities.isEntityPvpTeammate(user,target,this)) && target is LivingEntity) {
                target.addStatusEffect(StatusEffectInstance(StatusEffects.RESISTANCE, effects.duration(level), max(effects.amplifier(level)/4,3)))
                target.addStatusEffect(StatusEffectInstance(StatusEffects.STRENGTH, effects.duration(level), effects.amplifier(level)/4))
                effects.accept(target, AugmentConsumer.Type.BENEFICIAL)
                world.playSound(null, target.blockPos, soundEvent(), SoundCategory.PLAYERS, 0.6F, 1.2F)
                return true
            }
        }
        user.addStatusEffect(StatusEffectInstance(StatusEffects.RESISTANCE, effects.duration(level), max((effects.amplifier(level)/4)-1,3)))
        user.addStatusEffect(StatusEffectInstance(StatusEffects.STRENGTH, effects.duration(level), effects.amplifier(level)/4))
        effects.accept(user, AugmentConsumer.Type.BENEFICIAL)
        world.playSound(null, user.blockPos, soundEvent(), SoundCategory.PLAYERS, 0.6F, 1.2F)
        return true
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.BLOCK_RESPAWN_ANCHOR_SET_SPAWN
    }
}
