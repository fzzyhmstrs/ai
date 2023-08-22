package me.fzzyhmstrs.amethyst_imbuement.spells

import me.fzzyhmstrs.amethyst_core.interfaces.SpellCastingEntity
import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentConsumer
import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter_util.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter_util.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter_util.SpellType
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.MiscAugment
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterStatus
import me.fzzyhmstrs.fzzy_core.trinket_util.EffectQueue
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.mob.Monster
import net.minecraft.entity.passive.PassiveEntity
import net.minecraft.item.Items
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.world.World
import kotlin.math.max

class InspiringSongAugment: MiscAugment(ScepterTier.TWO,13){

    override val baseEffect: AugmentEffect
        get() = super.baseEffect.withDuration(175,125,0)
            .withAmplifier(0,0,0)
            .withRange(3.5,0.5,0.0)

    override fun augmentStat(imbueLevel: Int): AugmentDatapoint {
        return AugmentDatapoint(SpellType.GRACE,750,125,
            10,imbueLevel,20,LoreTier.NO_TIER, Items.NOTE_BLOCK)
    }

    override fun effect(
        world: World,
        user: LivingEntity,
        entityList: MutableList<Entity>,
        level: Int,
        effect: AugmentEffect
    ): Boolean {
        var successes = 0

        if (entityList.isEmpty()){
            successes++
            inspire(user,level+1,effect)
            effect.accept(user, AugmentConsumer.Type.BENEFICIAL)
        } else {
            entityList.add(user)
            for (entity3 in entityList) {
                if (entity3 !is Monster && entity3 !is PassiveEntity && entity3 is LivingEntity) {
                    if (entity3 is SpellCastingEntity && !AiConfig.entities.isEntityPvpTeammate(user,entity3,this)) continue
                    successes++
                    inspire(entity3,max(1,level-1),effect)
                    effect.accept(entity3,AugmentConsumer.Type.BENEFICIAL)
                }
            }
        }
        return successes > 0
    }
    
    private fun inspire(entity: LivingEntity,level: Int, effect: AugmentEffect){
        if(level < 6){
            EffectQueue.addStatusToQueue(entity,StatusEffects.SPEED, effect.duration(level), effect.amplifier(level))
            EffectQueue.addStatusToQueue(entity,StatusEffects.REGENERATION, effect.duration(3), effect.amplifier(level))
        } else if(level < 11){
            EffectQueue.addStatusToQueue(entity,StatusEffects.SPEED, effect.duration(level), effect.amplifier(level))
            EffectQueue.addStatusToQueue(entity,StatusEffects.JUMP_BOOST, effect.duration(level), effect.amplifier(level))
            EffectQueue.addStatusToQueue(entity,StatusEffects.REGENERATION, effect.duration(8), effect.amplifier(level))
        } else {
            EffectQueue.addStatusToQueue(entity,StatusEffects.SPEED, effect.duration(level), effect.amplifier(level))
            EffectQueue.addStatusToQueue(entity,StatusEffects.JUMP_BOOST, effect.duration(level), effect.amplifier(level))
            EffectQueue.addStatusToQueue(entity,StatusEffects.REGENERATION, effect.duration(12), effect.amplifier(level))
            EffectQueue.addStatusToQueue(entity, RegisterStatus.INSPIRED, effect.duration(level), 0)
        }
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.BLOCK_BEACON_ACTIVATE
    }
}
