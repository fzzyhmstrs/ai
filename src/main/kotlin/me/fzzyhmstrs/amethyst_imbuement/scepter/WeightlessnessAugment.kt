package me.fzzyhmstrs.amethyst_imbuement.scepter

import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentConsumer
import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter_util.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter_util.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter_util.SpellType
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.MinorSupportAugment
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.fzzy_core.coding_util.PerLvlI
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.mob.HostileEntity
import net.minecraft.item.Items
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.world.World

class WeightlessnessAugment: MinorSupportAugment(ScepterTier.TWO,5){

    override val baseEffect: AugmentEffect
        get() = super.baseEffect.withDuration(120,40)

    override fun augmentStat(imbueLevel: Int): AugmentDatapoint {
        return AugmentDatapoint(SpellType.WIT, PerLvlI(725,-25),125,
            9,imbueLevel,10, LoreTier.LOW_TIER, Items.PHANTOM_MEMBRANE)
    }

    override fun supportEffect(
        world: World,
        target: Entity?,
        user: LivingEntity,
        level: Int,
        effects: AugmentEffect
    ): Boolean {
        if(target != null) {
            if (target is HostileEntity || !AiConfig.entities.isEntityPvpTeammate(user,target,this) && target is LivingEntity) {
                (target as LivingEntity).addStatusEffect(StatusEffectInstance(StatusEffects.LEVITATION, effects.duration(level), 0))
                target.addStatusEffect(StatusEffectInstance(StatusEffects.SLOW_FALLING, effects.duration(level+2), 0))
                effects.accept(user, AugmentConsumer.Type.BENEFICIAL)
                world.playSound(null, target.blockPos, soundEvent(), SoundCategory.PLAYERS, 0.6F, 1.0F)
                return true
            }
        }
        user.addStatusEffect(StatusEffectInstance(StatusEffects.LEVITATION, effects.duration(level), 0))
        user.addStatusEffect(StatusEffectInstance(StatusEffects.SLOW_FALLING, effects.duration(level+2), 0))
        effects.accept(user, AugmentConsumer.Type.BENEFICIAL)
        world.playSound(null, user.blockPos, soundEvent(), SoundCategory.PLAYERS, 0.6F, 1.0F)
        return true
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.ENTITY_SHULKER_BULLET_HIT
    }
}
