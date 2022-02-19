package me.fzzyhmstrs.amethyst_imbuement.scepter

import me.fzzyhmstrs.amethyst_imbuement.augment.base_augments.BaseAugment
import me.fzzyhmstrs.amethyst_imbuement.scepter.base_augments.MiscAugment
import net.minecraft.entity.Entity
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.mob.Monster
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.world.World

class MassFortifyAugment(weight: Rarity, tier: Int, maxLvl: Int, vararg slot: EquipmentSlot): MiscAugment(weight,tier,maxLvl, *slot) {

    override fun effect(world: World, user: LivingEntity, entityList: MutableList<Entity>, level: Int): Boolean {
        var successes = 0
        entityList.add(user)
        for (entity3 in entityList) {
            if(entity3 !is Monster){
                successes++
                BaseAugment.addStatusToQueue(entity3.uuid,StatusEffects.RESISTANCE, 400 * level, level, true)
                BaseAugment.addStatusToQueue(entity3.uuid,StatusEffects.STRENGTH, 400 * level, level - 1, true)

            }
        }
        return successes > 0
    }

    override fun rangeOfEffect(): Double {
        return 9.0
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.BLOCK_BEACON_ACTIVATE
    }
}