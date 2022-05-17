package me.fzzyhmstrs.amethyst_imbuement.scepter

import me.fzzyhmstrs.amethyst_imbuement.item.ScepterItem
import me.fzzyhmstrs.amethyst_imbuement.util.RaycasterUtil
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import me.fzzyhmstrs.amethyst_imbuement.scepter.base_augments.MiscAugment
import me.fzzyhmstrs.amethyst_imbuement.util.SpellType
import net.minecraft.entity.Entity
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.item.Items
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.MathHelper
import net.minecraft.world.World
import kotlin.math.max
import kotlin.math.min

class GustingAugment(tier: Int, maxLvl: Int, vararg slot: EquipmentSlot): MiscAugment(tier, maxLvl, *slot) {

    override val baseEffect: ScepterObject.AugmentEffect
        get() = super.baseEffect.withRange(8.0,0.0).withAmplifier(2,1)

    override fun effect(
        world: World,
        target: Entity?,
        user: LivingEntity,
        level: Int,
        hit: HitResult?,
        effect: ScepterObject.AugmentEffect
    ): Boolean {
        val entityList = RaycasterUtil.raycastEntityArea(effect.range(level), user)
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
                val distNorm = 1.0 - (entity.squaredDistanceTo(user) - minDist)/maxDist
                val strength = effect.amplifier(level) * level * MathHelper.lerp(distNorm,minDistNorm,maxDistNorm)
                entityTask(world,entity,user,strength,null)
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

    override fun augmentStat(imbueLevel: Int): ScepterObject.AugmentDatapoint {
        return ScepterObject.AugmentDatapoint(SpellType.WIT,80,15,1,imbueLevel,1, Items.FEATHER)
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.ITEM_ELYTRA_FLYING
    }

}