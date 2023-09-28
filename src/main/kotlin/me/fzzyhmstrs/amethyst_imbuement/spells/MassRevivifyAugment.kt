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
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import me.fzzyhmstrs.fzzy_core.trinket_util.EffectQueue
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.mob.Monster
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.world.World

class MassRevivifyAugment: MiscAugment(ScepterTier.THREE,5){

    override val baseEffect: AugmentEffect
        get() = super.baseEffect.withRange(9.0,1.0,0.0)
            .withAmplifier(0,1,0)
            .withDuration(80,180,0)

    override fun augmentStat(imbueLevel: Int): AugmentDatapoint {
        return AugmentDatapoint(SpellType.GRACE,300,150,
            29, imbueLevel,12,LoreTier.HIGH_TIER, RegisterItem.GOLDEN_HEART)
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
            EffectQueue.addStatusToQueue(user,StatusEffects.REGENERATION,effect.duration(level), effect.amplifier(1))
            EffectQueue.addStatusToQueue(user,StatusEffects.ABSORPTION, effect.duration(level + 3), effect.amplifier(level))
            effect.accept(user, AugmentConsumer.Type.BENEFICIAL)
        }
        for (entity3 in entityList) {
            if(entity3 !is Monster && entity3 is LivingEntity){
                if (entity3 is SpellCastingEntity && !AiConfig.entities.isEntityPvpTeammate(user, entity3,this)) continue
                successes++
                EffectQueue.addStatusToQueue(entity3,StatusEffects.REGENERATION,(effect.duration(level) * 0.7).toInt(), effect.amplifier(1))
                EffectQueue.addStatusToQueue(entity3,StatusEffects.ABSORPTION, (effect.duration(level + 3) * 0.7).toInt(), effect.amplifier(level - 1))
                effect.accept(entity3,AugmentConsumer.Type.BENEFICIAL)
            }
        }
        entityList.add(user)
        //removing consumers from the main effect so that I don't get triplicate beneficial consumer effects
        val passedEffect = AugmentEffect()
        passedEffect.plus(effect)
        passedEffect.setConsumers(mutableListOf(),AugmentConsumer.Type.BENEFICIAL)
        passedEffect.setConsumers(mutableListOf(),AugmentConsumer.Type.HARMFUL)
        RegisterEnchantment.MASS_CLEANSE.effect(world, user, entityList, level, passedEffect)
        RegisterEnchantment.MASS_HEAL.effect(world, user, entityList, level, passedEffect)
        return successes > 0
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.ENTITY_ILLUSIONER_PREPARE_BLINDNESS
    }
}
