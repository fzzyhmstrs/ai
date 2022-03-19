package me.fzzyhmstrs.amethyst_imbuement.scepter

import me.fzzyhmstrs.amethyst_imbuement.item.ScepterItem
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterStatus
import me.fzzyhmstrs.amethyst_imbuement.scepter.base_augments.MiscAugment
import me.fzzyhmstrs.amethyst_imbuement.util.ScepterObject
import net.minecraft.entity.Entity
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.mob.Monster
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.hit.HitResult
import net.minecraft.world.World

class MassCleanseAugment(weight: Rarity, tier: Int, maxLvl: Int, vararg slot: EquipmentSlot): MiscAugment(weight,tier,maxLvl, *slot) {

    override fun effect(world: World, user: LivingEntity, entityList: MutableList<Entity>, level: Int): Boolean{
        var successes = 0
        for (target in entityList) {
            if(target !is Monster){
                ScepterObject.addEntityToQueue(target.uuid, ScepterItem.EntityTaskInstance(RegisterEnchantment.MASS_CLEANSE,user,level.toDouble(),null))
                successes++
            }
        }
        if (!user.hasStatusEffect(RegisterStatus.IMMUNITY)){
            RegisterEnchantment.CLEANSE.supportEffect(world,null,user,level)
            successes++
        }
        return successes > 0
    }

    override fun entityTask(world: World, target: Entity, user: LivingEntity, level: Double, hit: HitResult?) {
        RegisterEnchantment.CLEANSE.supportEffect(world,target,null,level.toInt())
    }

    override fun rangeOfEffect(): Double {
        return 8.0
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.ENTITY_EVOKER_PREPARE_SUMMON
    }
}