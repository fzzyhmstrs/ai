package me.fzzyhmstrs.amethyst_imbuement.effects

import me.fzzyhmstrs.amethyst_core.raycaster_util.RaycasterUtil
import me.fzzyhmstrs.amethyst_core.trinket_util.BaseAugment
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.entity.effect.StatusEffectCategory
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.mob.Monster
import net.minecraft.entity.passive.PassiveEntity

class SpectralVisionStatusEffect(statusEffectCategory:StatusEffectCategory, i: Int):
    StatusEffect(statusEffectCategory,i) {

    override fun canApplyUpdateEffect(duration: Int, amplifier: Int): Boolean {
        return duration % 20 == 0
    }

    override fun applyUpdateEffect(entity: LivingEntity, amplifier: Int) {
        if (entity.world.isClient) return
        val entityList: MutableList<Entity> = RaycasterUtil.raycastEntityArea(25.0,entity)
        if (entityList.isEmpty()) return
        for (target in entityList){
            if (target is Monster || target is PassiveEntity){
                BaseAugment.addStatusToQueue(target as LivingEntity, StatusEffects.GLOWING, 260, 0)
            }
        }
    }

}