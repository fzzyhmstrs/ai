package me.fzzyhmstrs.amethyst_imbuement.item

import dev.emi.trinkets.api.SlotReference
import me.fzzyhmstrs.amethyst_core.item_util.AbstractAugmentJewelryItem
import me.fzzyhmstrs.amethyst_core.registry.EventRegistry
import me.fzzyhmstrs.amethyst_imbuement.augment.ShieldingAugment
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemStack
import net.minecraft.world.World

open class ImbuedJewelryItem(settings: Settings,_ttn: String):AbstractAugmentJewelryItem(settings, _ttn) {

    override fun onEquip(stack: ItemStack, slot: SlotReference, entity: LivingEntity) {
        if (entity.world.isClient()) return
        super.onEquip(stack, slot, entity)
        val shieldLevel = EnchantmentHelper.getLevel(RegisterEnchantment.SHIELDING, stack)
        ShieldingAugment.addTrinket(entity,ShieldingAugment.baseAmount + shieldLevel + shieldingAdder())
    }

    override fun onUnequip(stack: ItemStack, slot: SlotReference, entity: LivingEntity) {
        if(entity.world.isClient()) return
        super.onUnequip(stack, slot, entity)
        val shieldLevel = EnchantmentHelper.getLevel(RegisterEnchantment.SHIELDING, stack)
        ShieldingAugment.removeTrinket(entity,ShieldingAugment.baseAmount + shieldLevel+ shieldingAdder())
    }

    override fun tick(stack: ItemStack, slot: SlotReference, entity: LivingEntity) {
        if(entity.world.isClient()) return
        if (EventRegistry.ticker_30.isReady()){
            ShieldingAugment.applyEntityShielding(entity)
        }
    }

    override fun passiveEnchantmentTasks(stack: ItemStack,world: World,entity: Entity){
        if (!entity.isPlayer) return
        super.passiveEnchantmentTasks(stack, world, entity)
    }

    open fun shieldingAdder(): Int{
        return 0
    }
}