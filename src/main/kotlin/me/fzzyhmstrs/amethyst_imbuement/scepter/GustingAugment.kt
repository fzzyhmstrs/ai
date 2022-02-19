package me.fzzyhmstrs.amethyst_imbuement.scepter

import me.fzzyhmstrs.amethyst_imbuement.item.ScepterItem
import me.fzzyhmstrs.amethyst_imbuement.util.RaycasterUtil
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import me.fzzyhmstrs.amethyst_imbuement.scepter.base_augments.MiscAugment
import net.minecraft.entity.Entity
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.MathHelper
import net.minecraft.world.World
import kotlin.math.max
import kotlin.math.min

class GustingAugment(weight: Rarity, _tier: Int, _maxLvl: Int, vararg slot: EquipmentSlot): MiscAugment(weight, _tier, _maxLvl, *slot) {


    override fun effect(world: World, target: Entity?, user: LivingEntity, level: Int, hit: HitResult?): Boolean {
        val entityList = RaycasterUtil.raycastEntityArea(raycastHitRange())
        if (entityList.isEmpty()) return false
        var minDist = 10000000.0
        var maxDist = 0.0
        for (entity in entityList){
            val dist = entity.squaredDistanceTo(user)
            minDist = min(dist,minDist)
            maxDist = max(dist,maxDist)
        }
        if (maxDist == 0.0) return false
        val minDistNorm = minDist/maxDist
        val maxDistNorm = 1.0
        for (entity in entityList){
            if (entity is LivingEntity){
                var distNorm = 1.0 - (entity.squaredDistanceTo(user) - minDist)/maxDist
                val strength = 1.5 + 1.0 * level * MathHelper.lerp(distNorm,minDistNorm,maxDistNorm)
                println(strength)
                ScepterItem.addEntityToQueue(entity.uuid, ScepterItem.EntityTaskInstance(RegisterEnchantment.GUSTING,user,strength,null))
            }
        }
        world.playSound(null,user.blockPos,soundEvent(),SoundCategory.PLAYERS,0.8F,1.2F)
        return true
    }

    override fun entityTask(world: World, target: Entity, user: LivingEntity, level: Double, hit: HitResult?) {
        if (target is LivingEntity){
            target.takeKnockback(level,user.x - target.x,user.z - target.z)
        }
    }

    /*override fun needsClient(): Boolean {
        return true
    }

    override fun clientTask(world: World, target: Entity?, user: LivingEntity, level: Int, hit: HitResult?) {
        effect(world, target, user, level, hit)
    }*/

    override fun raycastHitRange(): Double {
        return 8.0
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.ITEM_ELYTRA_FLYING
    }

}