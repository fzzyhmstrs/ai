package me.fzzyhmstrs.amethyst_imbuement.enchantment

import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import net.minecraft.enchantment.EnchantmentTarget
import net.minecraft.entity.Entity
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.ExperienceOrbEntity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.mob.Angerable
import net.minecraft.entity.passive.PassiveEntity
import net.minecraft.item.BowItem
import net.minecraft.item.CrossbowItem
import net.minecraft.item.ItemStack
import net.minecraft.item.TridentItem
import net.minecraft.server.world.ServerWorld

class InsightEnchantment(weight: Rarity, vararg slot: EquipmentSlot): ConfigDisableEnchantment(weight, EnchantmentTarget.WEAPON,*slot) {

    private var xpgiven = false

    override fun getMinPower(level: Int): Int {
        return 25 + (level - 1) * 15
    }

    override fun getMaxPower(level: Int): Int {
        return getMinPower(level) + 15
    }

    override fun getMaxLevel(): Int {
        return AiConfig.enchants.getAiMaxLevel(this,3)
    }

    override fun isAcceptableItem(stack: ItemStack): Boolean {
        return ((stack.item is CrossbowItem) || (stack.item is TridentItem) || (stack.item is BowItem) || EnchantmentTarget.WEAPON.isAcceptableItem(stack.item)) && checkEnabled()
    }

    override fun onTargetDamaged(user: LivingEntity, target: Entity, level: Int) {
        if (!checkEnabled()) return
        if (target.world.isClient) return
        if (!target.isAlive){
            xpgiven = if (target is LivingEntity && !xpgiven){
                when (target) {
                    is PassiveEntity -> {
                        ExperienceOrbEntity.spawn(target.world as ServerWorld?, target.pos, 1 * level)
                        true
                    }
                    is Angerable -> {
                        ExperienceOrbEntity.spawn(target.world as ServerWorld?, target.pos, 2 * level)
                        true
                    }
                    else -> {
                        ExperienceOrbEntity.spawn(target.world as ServerWorld?, target.pos, 3 * level)
                        true
                    }
                }
            } else {
                false
            }
        }
    }

}