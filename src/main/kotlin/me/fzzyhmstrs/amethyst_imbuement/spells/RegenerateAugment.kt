package me.fzzyhmstrs.amethyst_imbuement.spells

import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentConsumer
import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter_util.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter_util.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter_util.SpellType
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.MinorSupportAugment
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.fzzy_core.coding_util.PerLvlI
import me.fzzyhmstrs.fzzy_core.trinket_util.EffectQueue
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.item.Items
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.world.World

class RegenerateAugment: MinorSupportAugment(ScepterTier.ONE,17){

    override val baseEffect: AugmentEffect = super.baseEffect
                                                .withDamage(-1.0f,0.2f)
                                                .withAmplifier(0)
                                                .withDuration(384,12)
            

    override fun augmentStat(imbueLevel: Int): AugmentDatapoint {
        return AugmentDatapoint(SpellType.GRACE, PerLvlI(835,-5),60,
            1,imbueLevel,15,LoreTier.NO_TIER, Items.GHAST_TEAR)
    }

    override fun supportEffect(world: World, target: Entity?, user: LivingEntity, level: Int, effects: AugmentEffect): Boolean {
        if (target is LivingEntity && AiConfig.entities.shouldItHitFriend(user,target,this)) {
            EffectQueue.addStatusToQueue(target,StatusEffects.REGENERATION, effects.duration(level), effects.amplifier(level) + effects.damage(level).toInt())
            world.playSound(null, target.blockPos, soundEvent(), SoundCategory.PLAYERS, 0.6F, 1.2F)
            effects.accept(target, AugmentConsumer.Type.BENEFICIAL)
            return true
        }
        EffectQueue.addStatusToQueue(user,StatusEffects.REGENERATION, effects.duration(level), effects.amplifier(level) + effects.damage(level).toInt())
        world.playSound(null, user.blockPos, soundEvent(), SoundCategory.PLAYERS, 0.6F, 1.2F)
        effects.accept(user,AugmentConsumer.Type.BENEFICIAL)
        return true
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.BLOCK_CONDUIT_AMBIENT
    }
}
