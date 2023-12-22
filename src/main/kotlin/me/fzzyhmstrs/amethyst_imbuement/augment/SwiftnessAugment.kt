package me.fzzyhmstrs.amethyst_imbuement.augment

import me.fzzyhmstrs.amethyst_imbuement.augment.base_augments.EquipmentAugment
import me.fzzyhmstrs.fzzy_core.trinket_util.EffectQueue
import net.minecraft.enchantment.EnchantmentTarget
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.item.ItemStack

class SwiftnessAugment(weight: Rarity, mxLvl: Int = 1, vararg slot: EquipmentSlot)
: EquipmentAugment(weight, mxLvl,EnchantmentTarget.ARMOR_LEGS, *slot), AttributeProviding 
{

    private val uuid = UUID.fromString("99390d1c-a0d2-11ee-8c90-0242ac120002")
    
    override fun modifyAttributeMap(map: Multimap<EntityAttribute, EntityAttributeModifier>,slot: EquipmentSlot, level: Int) {
        if(!isEnabled()) return
        map.put(
            EntityAttributes.GENERIC_MOVEMENT_SPEED,
            EntityAttributeModifier(uuid, "swiftness_modifier_legs", 0.1 * level, EntityAttributeModifier.Operation.MULTIPLY_TOTAL)
        )
    }
}
