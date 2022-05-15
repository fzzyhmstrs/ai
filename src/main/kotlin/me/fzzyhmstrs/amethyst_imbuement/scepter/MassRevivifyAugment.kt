package me.fzzyhmstrs.amethyst_imbuement.scepter

import me.fzzyhmstrs.amethyst_imbuement.augment.base_augments.BaseAugment
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import me.fzzyhmstrs.amethyst_imbuement.scepter.base_augments.MiscAugment
import me.fzzyhmstrs.amethyst_imbuement.util.SpellType
import net.minecraft.entity.Entity
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.mob.Monster
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.world.World

class MassRevivifyAugment(tier: Int, maxLvl: Int, vararg slot: EquipmentSlot): MiscAugment(tier,maxLvl, *slot) {

    override fun effect(world: World, user: LivingEntity, entityList: MutableList<Entity>, level: Int): Boolean {
        var successes = 0
        if (entityList.isEmpty()){
            successes++
            BaseAugment.addStatusToQueue(user,StatusEffects.REGENERATION,240 * level, 1)
            BaseAugment.addStatusToQueue(user,StatusEffects.ABSORPTION, 800, level)
        }
        entityList.add(user)
        for (entity3 in entityList) {
            if(entity3 !is Monster && entity3 is LivingEntity){
                successes++
                BaseAugment.addStatusToQueue(entity3,StatusEffects.REGENERATION,180 * level, 1)
                BaseAugment.addStatusToQueue(entity3,StatusEffects.ABSORPTION, 600, level - 1)

            }
        }
        RegisterEnchantment.MASS_CLEANSE.effect(world, user, entityList, level)
        RegisterEnchantment.MASS_HEAL.effect(world, user, entityList, level)
        return successes > 0
    }

    override fun rangeOfEffect(): Double {
        return 9.0
    }

    override fun augmentStat(imbueLevel: Int): ScepterObject.AugmentDatapoint {
        return ScepterObject.AugmentDatapoint(SpellType.GRACE,300,100,22,imbueLevel,2, RegisterItem.GOLDEN_HEART)
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.ENTITY_ILLUSIONER_PREPARE_BLINDNESS
    }
}