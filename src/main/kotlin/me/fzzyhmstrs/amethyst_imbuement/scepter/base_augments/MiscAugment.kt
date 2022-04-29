package me.fzzyhmstrs.amethyst_imbuement.scepter.base_augments

import me.fzzyhmstrs.amethyst_imbuement.util.RaycasterUtil
import net.minecraft.enchantment.EnchantmentTarget
import net.minecraft.entity.Entity
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.util.Hand
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

abstract class MiscAugment(tier: Int, maxLvl: Int, vararg slot: EquipmentSlot): ScepterAugment(tier,maxLvl,EnchantmentTarget.WEAPON, *slot) {

    override fun applyTasks(world: World, user: LivingEntity, hand: Hand, level: Int): Boolean {
        var target: Entity? = null
        val hit = RaycasterUtil.raycastHit(distance = rangeOfEffect(),user, includeFluids = true) ?: return false
        if (hit.type == HitResult.Type.ENTITY) {
            target = (hit as EntityHitResult).entity
        }
        return effect(world, target, user, level, hit)
    }

    open fun effect(world: World, target: Entity?, user: LivingEntity, level: Int = 1, hit: HitResult?): Boolean {
        val entityList = RaycasterUtil.raycastEntityArea(rangeOfEffect(), user)
        if (!effect(world, user, entityList, level)) return false
        world.playSound(null, user.blockPos, soundEvent(), SoundCategory.PLAYERS, 1.0F, 1.0F)
        return true
    }

    open fun effect(world: World, user: LivingEntity, entityList: MutableList<Entity>, level: Int = 1): Boolean{
        return false
    }

    open fun persistentEffect(world: World, user: LivingEntity,blockPos: BlockPos, entityList: MutableList<Entity>, level: Int = 1){
        return
    }

    open fun raycastHitRange(): Double{
        return 4.0
    }
}