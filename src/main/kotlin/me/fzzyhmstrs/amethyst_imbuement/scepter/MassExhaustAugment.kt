package me.fzzyhmstrs.amethyst_imbuement.scepter

import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentConsumer
import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter_util.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter_util.SpellType
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.MiscAugment
import me.fzzyhmstrs.amethyst_core.trinket_util.EffectQueue
import net.minecraft.entity.Entity
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.mob.Monster
import net.minecraft.item.Items
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.world.World

class MassExhaustAugment(tier: Int, maxLvl: Int, vararg slot: EquipmentSlot): MiscAugment(tier,maxLvl, *slot) {

    override val baseEffect: AugmentEffect
        get() = super.baseEffect.withRange(12.0,0.0,0.0)
            .withDuration(240,100,0)
            .withAmplifier(0,1,0)

    override fun effect(
        world: World,
        user: LivingEntity,
        entityList: MutableList<Entity>,
        level: Int,
        effect: AugmentEffect
    ): Boolean {
        if (entityList.isEmpty()) return false
        var successes = 0
        for (entity3 in entityList) {
            if(entity3 is Monster && entity3 is LivingEntity){
                successes++
                EffectQueue.addStatusToQueue(entity3,StatusEffects.SLOWNESS,effect.duration(level),effect.amplifier(level+ 1))
                EffectQueue.addStatusToQueue(entity3,StatusEffects.WEAKNESS,effect.duration(level),effect.amplifier(level))
                effect.accept(entity3, AugmentConsumer.Type.HARMFUL)
            }
        }
        val bl = successes > 0
        if (bl){
            effect.accept(user,AugmentConsumer.Type.BENEFICIAL)
        }
        return bl
    }

    override fun augmentStat(imbueLevel: Int): AugmentDatapoint {
        return AugmentDatapoint(SpellType.GRACE,400,35,11,imbueLevel,LoreTier.HIGH_TIER, Items.FERMENTED_SPIDER_EYE)
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.ENTITY_EVOKER_PREPARE_ATTACK
    }
}