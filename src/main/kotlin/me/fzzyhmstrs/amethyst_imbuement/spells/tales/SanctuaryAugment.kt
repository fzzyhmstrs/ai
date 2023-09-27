package me.fzzyhmstrs.amethyst_imbuement.spells.tales

import me.fzzyhmstrs.amethyst_core.interfaces.SpellCastingEntity
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
import net.minecraft.entity.passive.GolemEntity
import net.minecraft.entity.passive.PassiveEntity
import net.minecraft.item.Items
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.world.World
import kotlin.math.max

class SanctuaryAugment: MinorSupportAugment(ScepterTier.THREE,13){

    override val baseEffect: AugmentEffect
        get() = super.baseEffect
            .withDuration(1100,100)
            .withRange(9.5,0.5)

    override fun augmentStat(imbueLevel: Int): AugmentDatapoint {
        return AugmentDatapoint(SpellType.GRACE, 12000,600,
            23,imbueLevel,85, BookOfTales.TALES_TIER, Items.SHIELD)
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
                target.addStatusEffect(StatusEffectInstance(RegisterStatus.IMMUNITY, effects.duration(level), 0))
                target.addStatusEffect(StatusEffectInstance(RegisterStatus.SANCTUARY, effects.duration(level), 0))
                effects.accept(target, AugmentConsumer.Type.BENEFICIAL)
                world.playSound(null, target.blockPos, soundEvent(), SoundCategory.PLAYERS, 0.6F, 1.2F)
                return true
            }
        }
        user.addStatusEffect(StatusEffectInstance(RegisterStatus.IMMUNITY, effects.duration(level), 0))
        user.addStatusEffect(StatusEffectInstance(RegisterStatus.SANCTUARY, effects.duration(level), 0))
        effects.accept(user, AugmentConsumer.Type.BENEFICIAL)
        world.playSound(null, user.blockPos, soundEvent(), SoundCategory.PLAYERS, 0.6F, 1.2F)
        return true
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.BLOCK_RESPAWN_ANCHOR_SET_SPAWN
    }
}
