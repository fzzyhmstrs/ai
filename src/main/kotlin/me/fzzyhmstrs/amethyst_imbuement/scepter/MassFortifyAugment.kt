package me.fzzyhmstrs.amethyst_imbuement.scepter

import me.fzzyhmstrs.amethyst_imbuement.augment.base_augments.BaseAugment
import me.fzzyhmstrs.amethyst_imbuement.scepter.base_augments.AugmentConsumer
import me.fzzyhmstrs.amethyst_imbuement.scepter.base_augments.AugmentEffect
import me.fzzyhmstrs.amethyst_imbuement.scepter.base_augments.MiscAugment
import me.fzzyhmstrs.amethyst_imbuement.util.LoreTier
import me.fzzyhmstrs.amethyst_imbuement.util.SpellType
import net.minecraft.entity.Entity
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.mob.Monster
import net.minecraft.entity.passive.PassiveEntity
import net.minecraft.item.Items
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.world.World

class MassFortifyAugment(tier: Int, maxLvl: Int, vararg slot: EquipmentSlot): MiscAugment(tier,maxLvl, *slot) {

    override val baseEffect: AugmentEffect
        get() = super.baseEffect.withDuration(600,200,0)
            .withAmplifier(-1,1,0)
            .withRange(9.0,1.0,0.0)

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
            BaseAugment.addStatusToQueue(user,StatusEffects.RESISTANCE, effect.duration(level), effect.amplifier(level+2))
            BaseAugment.addStatusToQueue(user,StatusEffects.STRENGTH, (effect.duration(level) * 1.25).toInt(), effect.amplifier(level))
            effect.accept(user,AugmentConsumer.Type.BENEFICIAL)
        } else {
            entityList.add(user)
            for (entity3 in entityList) {
                if (entity3 !is Monster && entity3 !is PassiveEntity && entity3 is LivingEntity) {
                    successes++
                    BaseAugment.addStatusToQueue(entity3, StatusEffects.RESISTANCE, effect.duration(level), effect.amplifier(level+1))
                    BaseAugment.addStatusToQueue(entity3, StatusEffects.STRENGTH,  effect.duration(level), effect.amplifier(level))
                    effect.accept(entity3,AugmentConsumer.Type.BENEFICIAL)
                }
            }
        }
        return successes > 0
    }

    override fun augmentStat(imbueLevel: Int): ScepterObject.AugmentDatapoint {
        return ScepterObject.AugmentDatapoint(SpellType.GRACE,1200,60,16,imbueLevel,LoreTier.HIGH_TIER, Items.GOLDEN_APPLE)
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.BLOCK_BEACON_ACTIVATE
    }
}