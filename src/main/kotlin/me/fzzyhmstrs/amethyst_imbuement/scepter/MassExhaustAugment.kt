package me.fzzyhmstrs.amethyst_imbuement.scepter

import me.fzzyhmstrs.amethyst_imbuement.augment.base_augments.BaseAugment
import me.fzzyhmstrs.amethyst_imbuement.scepter.base_augments.MiscAugment
import me.fzzyhmstrs.amethyst_imbuement.util.SpellType
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

    override fun effect(world: World, user: LivingEntity, entityList: MutableList<Entity>, level: Int): Boolean {
        if (entityList.isEmpty()) return false
        var successes = 0
        val amplifier = if (level > 1) {1} else {0}
        for (entity3 in entityList) {
            if(entity3 is Monster){
                successes++
                BaseAugment.addStatusToQueue(entity3.uuid,StatusEffects.SLOWNESS,240 + 100*level,amplifier + 1, true)
                BaseAugment.addStatusToQueue(entity3.uuid,StatusEffects.WEAKNESS,240 + 100*level,amplifier, true)
            }
        }
        return successes > 0
    }

    override fun rangeOfEffect(): Double {
        return 12.0
    }

    override fun augmentStat(imbueLevel: Int): ScepterObject.AugmentDatapoint {
        return ScepterObject.AugmentDatapoint(SpellType.GRACE,400,35,11,imbueLevel,2, Items.FERMENTED_SPIDER_EYE)
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.ENTITY_EVOKER_PREPARE_ATTACK
    }
}