package me.fzzyhmstrs.amethyst_imbuement.scepter.base_augments

import me.fzzyhmstrs.amethyst_imbuement.util.RaycasterUtil
import net.minecraft.enchantment.EnchantmentTarget
import net.minecraft.entity.Entity
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.mob.ZombieEntity
import net.minecraft.entity.passive.GolemEntity
import net.minecraft.entity.passive.PassiveEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.util.Hand
import net.minecraft.world.World

abstract class MinorSupportAugment(tier: Int, maxLvl: Int, vararg slot: EquipmentSlot): ScepterAugment(tier,maxLvl,EnchantmentTarget.WEAPON, *slot) {

    override fun applyTasks(world: World, user: LivingEntity, hand: Hand, level: Int): Boolean {
        var target = RaycasterUtil.raycastEntity(distance = rangeOfEffect())
        if (target == null) {
            target = ZombieEntity(world)
        }
        return supportEffect(world, target, user, level)
    }

    open fun supportEffect(world: World, target: Entity?, user: LivingEntity?, level: Int): Boolean {
        return false
    }
}