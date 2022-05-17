package me.fzzyhmstrs.amethyst_imbuement.scepter.base_augments

import me.fzzyhmstrs.amethyst_imbuement.scepter.ScepterObject
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
import kotlin.math.max

abstract class MiscAugment(tier: Int, maxLvl: Int, vararg slot: EquipmentSlot): ScepterAugment(tier,maxLvl,EnchantmentTarget.WEAPON, *slot) {

    override fun applyTasks(
        world: World,
        user: LivingEntity,
        hand: Hand,
        level: Int,
        effects: ScepterObject.AugmentEffect
    ): Boolean {
        var target: Entity? = null
        val hit = RaycasterUtil.raycastHit(distance = effects.range(level),user, includeFluids = true)
        if (hit != null) {
            if (hit.type == HitResult.Type.ENTITY) {
                target = (hit as EntityHitResult).entity
            }
        }
        return effect(world, target, user, level, hit, effects)
    }

    open fun effect(world: World, target: Entity?, user: LivingEntity, level: Int = 1, hit: HitResult?, effect: ScepterObject.AugmentEffect): Boolean {
        val entityList = RaycasterUtil.raycastEntityArea(effect.range(level), user)
        if (!effect(world, user, entityList, level, effect)) return false
        world.playSound(null, user.blockPos, soundEvent(), SoundCategory.PLAYERS, 1.0F, 1.0F)
        return true
    }

    open fun effect(world: World, user: LivingEntity, entityList: MutableList<Entity>, level: Int = 1, effect: ScepterObject.AugmentEffect): Boolean{
        return false
    }

    open fun persistentEffect(world: World, user: LivingEntity,blockPos: BlockPos, entityList: MutableList<Entity>, level: Int = 1, effect: ScepterObject.AugmentEffect){
        return
    }

    open fun raycastHitRange(modifier: Double): Double{
        return max(1.0,4.0 + modifier)
    }
}
