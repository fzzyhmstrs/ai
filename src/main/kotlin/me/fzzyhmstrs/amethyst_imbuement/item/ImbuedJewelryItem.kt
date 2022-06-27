package me.fzzyhmstrs.amethyst_imbuement.item

import com.google.common.collect.Multimap
import dev.emi.trinkets.api.SlotReference
import me.fzzyhmstrs.amethyst_core.registry.EventRegistry
import me.fzzyhmstrs.amethyst_core.trinket_util.AugmentTasks
import me.fzzyhmstrs.amethyst_imbuement.augment.ShieldingAugment
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEvent
import me.fzzyhmstrs.amethyst_imbuement.util.AugmentTasks
import net.minecraft.client.item.TooltipContext
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.attribute.EntityAttribute
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.world.World
import java.util.*

open class ImbuedJewelryItem(settings: Settings,_ttn: String):CopperJewelryItem(settings,"copper_ring"), AugmentTasks {
    private val ttn: String = _ttn

    override fun appendTooltip(stack: ItemStack?, world: World?, tooltip: MutableList<Text>?, context: TooltipContext?) {
        super.appendTooltip(stack, world, tooltip, context)
        tooltip?.removeAt(tooltip.size-1)
        tooltip?.add(Text.translatable("item.amethyst_imbuement.$ttn.tooltip1").formatted(Formatting.WHITE, Formatting.ITALIC))
    }

    override fun getModifiers(
        stack: ItemStack,
        slot: SlotReference,
        entity: LivingEntity,
        uuid: UUID
    ): Multimap<EntityAttribute, EntityAttributeModifier> {
        val modifiers = super.getModifiers(stack, slot, entity, uuid)
        val modifiersNew = modifierEnchantmentTasks(stack,entity.world,entity)
        if (modifiersNew != null){
            for (mod in modifiersNew.keys){
                modifiers.put(mod,modifiersNew[mod])
            }
        }
        return modifiers
    }

    override fun onEquip(stack: ItemStack, slot: SlotReference, entity: LivingEntity) {
        super.onEquip(stack, slot, entity)
        if (entity.world.isClient()) return
        val shieldLevel = EnchantmentHelper.getLevel(RegisterEnchantment.SHIELDING, stack)
        ShieldingAugment.addTrinket(entity,ShieldingAugment.baseAmount + shieldLevel + shieldingAdder())
        passiveEnchantmentTasks(stack,entity.world,entity)
    }

    override fun onUnequip(stack: ItemStack, slot: SlotReference, entity: LivingEntity) {
        super.onUnequip(stack, slot, entity)
        if(entity.world.isClient()) return
        unequipEnchantmentTasks(stack,entity.world,entity)
        val shieldLevel = EnchantmentHelper.getLevel(RegisterEnchantment.SHIELDING, stack)
        ShieldingAugment.removeTrinket(entity,ShieldingAugment.baseAmount + shieldLevel+ shieldingAdder())
    }

    override fun tick(stack: ItemStack, slot: SlotReference, entity: LivingEntity) {
        if(entity.world.isClient()) return
        if (RegisterEvent.ticker_jewelry.isReady()){
            ShieldingAugment.applyEntityShielding(entity)
            passiveEnchantmentTasks(stack,entity.world,entity)
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