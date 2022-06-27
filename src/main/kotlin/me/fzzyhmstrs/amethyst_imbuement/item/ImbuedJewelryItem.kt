package me.fzzyhmstrs.amethyst_imbuement.item

import com.google.common.collect.Multimap
import me.fzzyhmstrs.amethyst_core.item_util.AbstractAugmentJewelryItem
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.augment.ShieldingAugment
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.attribute.EntityAttribute
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier
import net.minecraft.world.World
import java.util.*

open class ImbuedJewelryItem(settings: Settings, flavor: String):AbstractAugmentJewelryItem(settings, Identifier(AI.MOD_ID,flavor)) {

    override fun getAugmentModifiers(
        stack: ItemStack,
        entity: LivingEntity,
        uuid: UUID
    ): Multimap<EntityAttribute, EntityAttributeModifier> {
        val modifiers = super.getAugmentModifiers(stack, entity, uuid)
        modifiers.put(
            EntityAttributes.GENERIC_MOVEMENT_SPEED,
            EntityAttributeModifier(uuid, "amethyst_imbuement:movement_speed", 0.03, EntityAttributeModifier.Operation.MULTIPLY_TOTAL)
        )
        return modifiers
    }

    override fun jewelryEquip(stack: ItemStack, entity: LivingEntity) {
        super.jewelryEquip(stack, entity)
        val shieldLevel = EnchantmentHelper.getLevel(RegisterEnchantment.SHIELDING, stack)
        ShieldingAugment.addTrinket(entity,ShieldingAugment.baseAmount + shieldLevel + shieldingAdder())
    }

    override fun jewelryUnEquip(stack: ItemStack, entity: LivingEntity) {
        if(entity.world.isClient()) return
        super.jewelryUnEquip(stack, entity)
        val shieldLevel = EnchantmentHelper.getLevel(RegisterEnchantment.SHIELDING, stack)
        ShieldingAugment.removeTrinket(entity,ShieldingAugment.baseAmount + shieldLevel+ shieldingAdder())
    }

    override fun jewelryIntermittentTick(stack: ItemStack, entity: LivingEntity) {
        super.jewelryIntermittentTick(stack, entity)
        ShieldingAugment.applyEntityShielding(entity)
    }

    override fun passiveEnchantmentTasks(stack: ItemStack,world: World,entity: Entity){
        if (!entity.isPlayer) return
        super.passiveEnchantmentTasks(stack, world, entity)
    }

    open fun shieldingAdder(): Int{
        return 0
    }
}