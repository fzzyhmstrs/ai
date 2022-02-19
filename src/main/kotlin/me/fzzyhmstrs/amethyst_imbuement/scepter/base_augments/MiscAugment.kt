package me.fzzyhmstrs.amethyst_imbuement.scepter.base_augments

import me.fzzyhmstrs.amethyst_imbuement.util.RaycasterUtil
import net.minecraft.enchantment.EnchantmentTarget
import net.minecraft.entity.Entity
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

open class MiscAugment(weight: Rarity, tier: Int, maxLvl: Int, vararg slot: EquipmentSlot): ScepterAugment(weight,tier,maxLvl,EnchantmentTarget.WEAPON, *slot) {

    open fun effect(world: World, target: Entity?, user: LivingEntity, level: Int = 1, hit: HitResult?): Boolean {
        val entityList = RaycasterUtil.raycastEntityArea(rangeOfEffect() + 1.0 * level)
        if (!effect(world, user, entityList, level)) return false
        world.playSound(null, user.blockPos, soundEvent(), SoundCategory.PLAYERS, 1.0F, 1.0F)
        return true
    }

    open fun effect(world: World, user: LivingEntity, entityList: MutableList<Entity>, level: Int = 1): Boolean{
        return true
    }

    open fun persistentEffect(world: World, user: LivingEntity,blockPos: BlockPos, entityList: MutableList<Entity>, level: Int = 1){
        return
    }

    open fun soundEvent(): SoundEvent{
        return SoundEvents.EVENT_RAID_HORN
    }

    open fun rangeOfEffect(): Double{
        return 8.0
    }

    open fun raycastHitRange(): Double{
        return 4.0
    }
}