package me.fzzyhmstrs.amethyst_imbuement.scepter.base_augments

import me.fzzyhmstrs.amethyst_imbuement.item.ScepterItem
import me.fzzyhmstrs.amethyst_imbuement.util.RaycasterUtil
import me.fzzyhmstrs.amethyst_imbuement.util.ScepterObject
import net.minecraft.enchantment.EnchantmentTarget
import net.minecraft.entity.Entity
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.mob.ZombieEntity
import net.minecraft.entity.passive.GolemEntity
import net.minecraft.entity.passive.PassiveEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.Hand
import net.minecraft.world.World

open class MinorSupportAugment(weight: Rarity, tier: Int, maxLvl: Int, vararg slot: EquipmentSlot): ScepterAugment(weight,tier,maxLvl,EnchantmentTarget.WEAPON, *slot) {

    override fun applyTasks(world: World, user: LivingEntity, hand: Hand, level: Int): Boolean {
        var target = RaycasterUtil.raycastEntity(distance = rangeOfEffect())
        if (target == null){
            target = ZombieEntity(world)
        }
        val bl = supportEffect(world,target,user,level)
        if (bl){
            if (needsClient()) ScepterObject.addClientTaskToQueue(this, ScepterItem.ClientTaskInstance(target, level, null))
        }
        return bl
    }

    open fun supportEffect(world: World, target: Entity?, user: LivingEntity?, level: Int): Boolean {
        if(target != null) {
            if (target is PassiveEntity || target is GolemEntity || target is PlayerEntity) {
                (target as LivingEntity).heal((2.0F + (level-1) * 1.0F))
                world.playSound(null, target.blockPos, soundEvent(), SoundCategory.PLAYERS, 1.0F, 1.0F)
                return true
            }
        }
        return if(user != null) {
            if (user is PlayerEntity) {
                user.heal((2.0F + (level-1) * 1.0F))
                world.playSound(null, user.blockPos, soundEvent(), SoundCategory.PLAYERS, 1.0F, 1.0F)
                true
            } else {
                false
            }
        } else {
            false
        }
    }

    open fun soundEvent(): SoundEvent{
        return SoundEvents.BLOCK_BEACON_ACTIVATE
    }

    open fun rangeOfEffect(): Double{
        return 8.0
    }
}