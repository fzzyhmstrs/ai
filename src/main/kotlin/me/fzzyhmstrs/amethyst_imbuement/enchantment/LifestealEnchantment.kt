package me.fzzyhmstrs.amethyst_imbuement.enchantment

import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import net.minecraft.enchantment.EnchantmentTarget
import net.minecraft.entity.Entity
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.item.BowItem
import net.minecraft.item.CrossbowItem
import net.minecraft.item.ItemStack
import net.minecraft.item.TridentItem
import java.util.*

class LifestealEnchantment(weight: Rarity, vararg slot: EquipmentSlot): ConfigDisableEnchantment(weight, EnchantmentTarget.WEAPON,*slot) {

    companion object{
        val timers: MutableMap<UUID,Long> = mutableMapOf()
    }

    override fun getMinPower(level: Int): Int {
        return level * 20
    }

    override fun getMaxPower(level: Int): Int {
        return getMinPower(level) + 30
    }

    override fun getMaxLevel(): Int {
        return AiConfig.enchants.getAiMaxLevel(this,3)
    }

    override fun isAcceptableItem(stack: ItemStack): Boolean {
        return ((stack.item is CrossbowItem) || (stack.item is TridentItem) || (stack.item is BowItem) || EnchantmentTarget.WEAPON.isAcceptableItem(stack.item)) && checkEnabled()
    }

    override fun onTargetDamaged(user: LivingEntity, target: Entity, level: Int) {
        if (!checkEnabled()) return
        if (target !is LivingEntity) return
        if(user.world.isClient()) return
        val time = user.world.time
        val uuid = user.uuid
        if (!timers.containsKey(uuid) || ((time - 20L) >= (timers[uuid] ?: Long.MAX_VALUE))){
            timers[uuid] = time
            user.heal(0.5f * level)
        }        
    }
}
